package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpPropFindMethod;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
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
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.w3c.dom.Document;
import yzy.zyuanyuz.caldavclient4j.client.AbstractCalDAVManager;
import yzy.zyuanyuz.caldavclient4j.util.AppleCalDAVUtil;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static net.fortuna.ical4j.model.Component.VEVENT;

/**
 * @author zyuanyuz
 * @since 2019/10/15 20:59
 */
public class ICloudCalDAVManager extends AbstractCalDAVManager {

  private static final String ICLOUD_CALDAV_URI = "https://caldav.icloud.com:443";

  private String principal;

  private List<EventEntry> eventList;

  public ICloudCalDAVManager(String appleId, String password, String calName) throws Exception {
    this.calName = calName;

    HttpHost target =
        new HttpHost(
            ICloudCalDAVConstants.APPLE_CALDAV_HOST,
            ICloudCalDAVConstants.APPLE_CALDAV_PORT,
            ICloudCalDAVConstants.APPLE_CALDAV_URL_SCHEME);

    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(
        new AuthScope(target.getHostName(), target.getPort()),
        new UsernamePasswordCredentials(appleId, password));

    setHttpClient(
        HttpClients.custom()
            .setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setDefaultCredentialsProvider(provider)
            .build());

    setMethodFactory(new CalDAV4JMethodFactory());

    this.principal = initPrincipal();

    setCalendarCollectionRoot(
        ICloudCalDAVConstants.APPLE_CALDAV_HOST + "/" + this.principal + "/" + this.calName);

    refreshAllEvents(); // init the eventlist
  }

  private String initPrincipal() throws Exception {
    DavPropertyNameSet nameSet = new DavPropertyNameSet();
    nameSet.add(DavPropertyName.create(ICloudCalDAVConstants.CURRENT_USER_PRINCIPAL));
    HttpPropFindMethod propFindMethod =
        methodFactory.createPropFindMethod(ICLOUD_CALDAV_URI, nameSet, 0);
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
  public String getETag(String uuid) throws CalDAV4JException {
    String pathGetETag = AppleCalDAVUtil.pathToCalendar(this.principal, this.calName, uuid);
    return getETag(this.httpClient, pathGetETag);
  }

  /** refresh all events */
  public void refreshAllEvents() {}

  /**
   * refresh the event with specific uuid
   *
   * @param uuid the event uuid
   */
  public void refreshEvent(String uuid) {}

  /**
   *
   * @param uuidList
   * @return
   * @throws Exception
   */
  public List<VEvent> multiGetEvents(List<String> uuidList) throws Exception {
    List<String> urls =
        uuidList.stream()
            .map(uuid -> AppleCalDAVUtil.pathToCalendar(this.principal, this.calName, uuid))
            .collect(toList());

    return multigetCalendarUris(this.httpClient, urls).stream()
        .map(c -> (VEvent) (c.getComponent(VEVENT)))
        .collect(toList());
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
    return (VEvent) calendar.getComponent(VEVENT);
  }

  // TODO VTimezone
  public void addEvent(VEvent event) throws CalDAV4JException {
    add(this.httpClient, event, null);
  }

  // TODO path need test
  public void deleteEvent(String uuid) throws CalDAV4JException {
    String pathToDelete =
        ICLOUD_CALDAV_URI
            + "/"
            + this.principal
            + "/calendars/"
            + this.calName
            + "/"
            + uuid
            + ".ics";
    delete(this.httpClient, pathToDelete);
  }

  // getter and setter

  public String getPrincipal() {
    return principal;
  }

  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  public List<EventEntry> getEventList() {
    return eventList;
  }

  public void setEventList(List<EventEntry> eventList) {
    this.eventList = eventList;
  }
}
