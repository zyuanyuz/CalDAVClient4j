package yzy.zyuanyuz.caldavclient4j.client.util;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpPropFindMethod;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.CalDAVStatus;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.w3c.dom.Document;
import yzy.zyuanyuz.caldavclient4j.client.commons.ResourceEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.Component.VEVENT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.jackrabbit.webdav.property.DavPropertyName.DISPLAYNAME;
import static yzy.zyuanyuz.caldavclient4j.client.commons.ICloudCalDAVConstants.CURRENT_USER_PRINCIPAL_STR;
import static yzy.zyuanyuz.caldavclient4j.client.commons.ICloudCalDAVConstants.ICLOUD_CALDAV_HOST_PORT;

/**
 * @author George Yu
 * @since 2019/10/9 10:43
 */
public final class ICloudCalendarUtil {

  private ICloudCalendarUtil() {}

  public static String getPrincipalId(HttpClient httpClient, CalDAV4JMethodFactory methodFactory)
      throws CalDAV4JException {
    DavPropertyNameSet nameSet = new DavPropertyNameSet();
    nameSet.add(DavPropertyName.create(CURRENT_USER_PRINCIPAL_STR));
    Document doc;
    try {
      HttpPropFindMethod propFindMethod =
          methodFactory.createPropFindMethod(ICLOUD_CALDAV_HOST_PORT, nameSet, 0);
      HttpResponse response = httpClient.execute(propFindMethod);
      doc = propFindMethod.getResponseBodyAsDocument(response.getEntity());
    } catch (Exception e) {
      throw new CalDAV4JException("Get PrincipalId failed with :" + e.getCause());
    }
    String href = doc.getElementsByTagName("href").item(1).getFirstChild().getNodeValue();
    return href.substring(1, href.indexOf("/principal/"));
  }

  /**
   * If use JDK 11+ this may cause SSLException
   *
   * @param appleId
   * @param password
   * @return
   */
  public static HttpClient createHttpClient(String appleId, String password)
      throws CalDAV4JException {
    HttpHost target = new HttpHost("caldav.icloud.com", 443, "https");
    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(
        new AuthScope(target.getHostName(), target.getPort()),
        new UsernamePasswordCredentials(appleId, password));
    try {
      return HttpClients.custom()
          .setSSLContext(
              new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
          .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          .setDefaultCredentialsProvider(provider)
          .build();
    } catch (Exception e) {
      throw new CalDAV4JException("Build http client failed! May appleId or password not right");
    }
  }

  /**
   * get all custom resource from server, so some default resource was excluded.
   *
   * @param httpClient the http client with user auth
   * @param principalId
   * @return
   */
  public static List<ResourceEntry> getAllResourceFromServer(
      HttpClient httpClient, CalDAV4JMethodFactory methodFactory, String principalId)
      throws CalDAV4JException {
    String url = ICLOUD_CALDAV_HOST_PORT + principalId + "/calendars";
    DavPropertyNameSet propertyNameSet = new DavPropertyNameSet();
    propertyNameSet.add(DISPLAYNAME);
    MultiStatusResponse[] multiStatusResponses = null;
    try {
      HttpPropFindMethod propFindMethod =
          methodFactory.createPropFindMethod(url, propertyNameSet, 1);
      multiStatusResponses =
          propFindMethod
              .getResponseBodyAsMultiStatus(httpClient.execute(propFindMethod))
              .getResponses();
    } catch (Exception e) {
      throw new CalDAV4JException(
          "Get all resource from server failed with exception:" + e.getCause());
    }
    if (null == multiStatusResponses) {
      return new ArrayList<>();
    }
    return Arrays.stream(multiStatusResponses)
        .filter(res -> null != res.getProperties(CalDAVStatus.SC_OK).get(DISPLAYNAME))
        .map(
            res ->
                new ResourceEntry(
                    res.getHref(),
                    (String) res.getProperties(CalDAVStatus.SC_OK).get(DISPLAYNAME).getValue()))
        .collect(Collectors.toList());
  }

  /**
   * handle the sync collection response
   *
   * @param multiStatus
   * @return Triple<List<String> eventsHrefToMGet, List<String> eventUidDeletedFromServer, String
   *     nextSyncToken>
   */
  public static Triple<List<String>, List<String>, String> getSyncHrefsAndToDel(
      MultiStatus multiStatus) {
    MultiStatusResponse[] multiStatusResponses = multiStatus.getResponses();

    List<String> hrefsToMGet = new ArrayList<>();
    List<String> uidToDel = new ArrayList<>();
    String nextSyncToken = multiStatus.getResponseDescription(); // TODO [bug] how get the next syncToken?

    for (int i = 1; i < multiStatusResponses.length; i++) {
      if (null != multiStatusResponses[i].getProperties(SC_OK)) {
        hrefsToMGet.add(multiStatusResponses[i].getHref());
      }
      if (null != multiStatusResponses[i].getProperties(SC_NOT_FOUND)) {
        uidToDel.add(getUidFromHref(multiStatusResponses[i].getHref()));
      }
    }
    return Triple.of(hrefsToMGet, uidToDel, nextSyncToken);
  }

  public static List<VEvent> getVEventFromMultiStatus(MultiStatus multiStatus) {
    MultiStatusResponse[] multiStatusResponses = multiStatus.getResponses();
    List<VEvent> eventList = new ArrayList<>();
    for (int i = 1; i < multiStatusResponses.length; i++) { // skip one
      eventList.add(
          (VEvent)
              CalendarDataProperty.getCalendarfromResponse(multiStatusResponses[i])
                  .getComponent(VEVENT));
    }
    return eventList;
  }

  public static String pathToCalendarPath(String principalId, String resourceId, String uuid) {
    return ICLOUD_CALDAV_HOST_PORT
        + "/"
        + principalId
        + "/calendars/"
        + resourceId
        + "/"
        + uuid
        + ".ics";
  }

  public static String getUidFromHref(String href) {
    return href.substring(href.lastIndexOf("/") + 1, href.indexOf(".ics"));
  }

  /**
   * for iCloud event from calendar
   *
   * @param calendars
   * @return
   */
  public static List<VEvent> getEventsFromCalendars(List<Calendar> calendars) {
    return calendars.stream()
        .flatMap(c -> c.getComponents(VEVENT).stream())
        .map(e -> (VEvent) e)
        .collect(Collectors.toList());
  }
}
