package yzy.zyuanyuz.caldavclient4j.client.icloud;

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

import java.util.List;

import static net.fortuna.ical4j.model.Component.VEVENT;

/**
 * @author zyuanyuz
 * @since 2019/10/15 20:59
 */
public class ICloudCalDAVManager extends AbstractCalDAVManager {

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
  public String getETag(String uuid) {

    return null;
  }

  public void refreshAllEvents() {}

  public void refreshEvent(String uuid) {}

  public List<VEvent> multiGetEvents() throws Exception {

    return null;
  }

  public List<VEvent> getEvents(Date startTime, Date endTime) {

    return null;
  }

  public VEvent gestEvent(String uuid) throws Exception {
    String relativePath = uuid + ".ics";
    Calendar calendar = getCalendar(getHttpClient(), relativePath);
    return (VEvent) calendar.getComponent(VEVENT);
  }

  public void addEvent(VEvent event) {}

  public void deleteEvent(String uuid) {}
}
