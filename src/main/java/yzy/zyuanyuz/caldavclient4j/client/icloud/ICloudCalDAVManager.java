package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpPropFindMethod;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.ICalendarUtils;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VTimeZoneFactory;
import net.fortuna.ical4j.model.property.Uid;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.w3c.dom.Document;
import yzy.zyuanyuz.caldavclient4j.client.AbstractCalDAVManager;
import yzy.zyuanyuz.caldavclient4j.client.util.ICloudCalDAVUtil;
import yzy.zyuanyuz.caldavclient4j.util.AppleCalDAVUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.fortuna.ical4j.model.Component.VEVENT;
import static yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalDAVConstants.CURRENT_USER_PRINCIPAL;
import static yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalDAVConstants.ICLOUD_CALDAV_URI;

/**
 * @author zyuanyuz
 * @since 2019/10/15 20:59
 */
public class ICloudCalDAVManager extends AbstractCalDAVManager {

  private String principal;

  private Map<String, EventEntry> eventsMap;

  public ICloudCalDAVManager(String appleId, String password, String calName) throws Exception {
    this.calName = calName;
    this.eventsMap = new ConcurrentHashMap<>();

    HttpHost target = new HttpHost("caldav.icloud.com", 443, "https");
    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(
        new AuthScope(target.getHostName(), target.getPort()),
        new UsernamePasswordCredentials(appleId, password));
    this.httpClient =
        HttpClients.custom()
            .setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setDefaultCredentialsProvider(provider)
            .build();
    setMethodFactory(new CalDAV4JMethodFactory());

    setMethodFactory(new CalDAV4JMethodFactory());

    // this.principal = AppleCalDAVUtil.getPrincipalId(this.httpClient, this.methodFactory);
    this.principal = initPrincipal();

    setCalendarCollectionRoot(
        ICloudCalDAVConstants.APPLE_CALDAV_HOST + "/" + this.principal + "/" + this.calName);
  }

  private String initPrincipal() throws Exception {
    DavPropertyNameSet nameSet = new DavPropertyNameSet();
    nameSet.add(DavPropertyName.create(CURRENT_USER_PRINCIPAL));
    HttpPropFindMethod propFindMethod =
        methodFactory.createPropFindMethod("https://caldav.icloud.com:443", nameSet, 0);
    HttpResponse response = httpClient.execute(propFindMethod);
    Document doc = propFindMethod.getResponseBodyAsDocument(response.getEntity());
    String href = doc.getElementsByTagName("href").item(1).getFirstChild().getNodeValue();
    return href.substring(1, href.indexOf("/principal/"));
  }

  /**
   * * get etag by event uuid
   *
   * @param uuid
   * @return
   */
  // TODO test
  public String getETag(String uuid) throws CalDAV4JException {
    String pathGetETag =
        ICLOUD_CALDAV_URI
            + "/"
            + this.principal
            + "/calendars/"
            + this.calName
            + "/"
            + uuid
            + ".ics";
    return getETag(this.httpClient, pathGetETag);
  }

  /**
   * refresh all events ,may dangerous to get recurrence event? TODO
   *
   * @throws Exception
   */
  public void refreshAllEvents() throws Exception {

    String calendarFolder = ICLOUD_CALDAV_URI + this.principal + "/calendars/" + calName;
    DavPropertyNameSet properties = new DavPropertyNameSet();
    properties.add(DavPropertyName.GETETAG);

    CompFilter filter = new CompFilter(Calendar.VCALENDAR);
    filter.addCompFilter(new CompFilter(Component.VEVENT));

    CalendarQuery query = new CalendarQuery(properties, filter, null, false, false);
    HttpCalDAVReportMethod reportMethod =
        methodFactory.createCalDAVReportMethod(calendarFolder, query, CalDAVConstants.DEPTH_1);
    MultiStatusResponse[] responses =
        reportMethod.getResponseBodyAsMultiStatus(httpClient.execute(reportMethod)).getResponses();
    List<String> hrefsToGetEvent = new ArrayList<>();
    for (int i = 1; i < responses.length; i++) {
      String href = responses[i].getHref();
      String uid = ICloudCalDAVUtil.getUidFromHref(href);
      // ↓↓ class cast need test
      String etag =
          (String)
              responses[i]
                  .getProperties(CalDAVStatus.SC_OK)
                  .get(DavPropertyName.GETETAG)
                  .getValue();
      if(eventsMap.containsKey(uid)&&!etag.equals(eventsMap.get(uid).getEtag())){
          hrefsToGetEvent.add(href);
      }
    }
    
  }

  public void refreshEvent(String uuid) throws Exception {
    String path = getCalendarCollectionRoot() + "/" + uuid + ".ics";
    String etag = getETag(this.httpClient, path);
    System.out.println(etag);
  }

  public List<VEvent> multiGetEvents(List<String> hrefs) throws Exception {

    return null;
  }

  public List<VEvent> getEvents(Date startTime, Date endTime) {

    return null;
  }

  /**
   * @param uuid
   * @return
   * @throws Exception
   */
  public VEvent getEvent(String uuid) throws CalDAV4JException {
    String relativePath = uuid + ".ics";
    Calendar calendar = getCalendar(getHttpClient(), relativePath);
    return ICalendarUtils.getFirstEvent(calendar);
  }

  // TODO VTimezone how use it?
  public void addEvent(VEvent event) throws Exception {
    //    String uid = event.getUid().toString();
    if (ICalendarUtils.getUIDValue(event) == null) {
      event.getProperty(Property.UID).setValue(UUID.randomUUID().toString());
    }
    add(this.httpClient, event, null);
    String etag = getETag(event.getUid().toString());
  }

  // TODO path need test
  public void deleteEvent(String uuid) throws CalDAV4JException {
    String pathToDelete =
        ICLOUD_CALDAV_URI + this.principal + "/calendars/" + this.calName + "/" + uuid + ".ics";
    delete(this.httpClient, pathToDelete);
  }

  // ↓ getter and setter

  public String getPrincipal() {
    return principal;
  }

  public void setPrincipal(String principal) {
    this.principal = principal;
  }
}
