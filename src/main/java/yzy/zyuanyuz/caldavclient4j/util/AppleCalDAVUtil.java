package yzy.zyuanyuz.caldavclient4j.util;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpPropFindMethod;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CompFilter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.w3c.dom.Document;

import java.util.List;

/**
 * @author George Yu
 * @since 2019/10/9 10:43
 */
public abstract class AppleCalDAVUtil {
  private static final String CURRENT_USER_PRINCIPAL = "current-user-principal";

  private static final String CALDAV_ICLOUD_HOST = "https://caldav.icloud.com:443/";

  public static String getPrincipalId(HttpClient httpClient, CalDAV4JMethodFactory methodFactory)
      throws Exception {
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
   * @param calendarFolder
   * @param httpClient
   * @param methodFactory
   * @return
   * @throws Exception
   */
  public static List<String> getEventUidList(
      String calendarFolder, HttpClient httpClient, CalDAV4JMethodFactory methodFactory)
      throws Exception {
    String userId = getPrincipalId(httpClient, methodFactory); // 16884482682
    String url = CALDAV_ICLOUD_HOST + userId + "/calendars/" + calendarFolder;

    DavPropertyNameSet properties = new DavPropertyNameSet();
    properties.add(DavPropertyName.GETETAG);

    CompFilter filter = new CompFilter(Calendar.VCALENDAR);
    filter.addCompFilter(new CompFilter(Component.VEVENT));

    CalendarQuery query = new CalendarQuery(properties, filter, null, false, false);
    HttpCalDAVReportMethod reportMethod =
        methodFactory.createCalDAVReportMethod(url, query, CalDAVConstants.DEPTH_1);
    HttpResponse response = httpClient.execute(reportMethod);
    System.out.println(EntityUtils.toString(response.getEntity()));
    return null;
    //    MultiStatus multiStatus = reportMethod.getResponseBodyAsMultiStatus(response);
    //    MultiStatusResponse[] multiStatusResponses = multiStatus.getResponses();
    //    return Arrays.stream(multiStatusResponses)
    //        .skip(1)
    //        .map(MultiStatusResponse::getHref)
    //        .map(href -> href.substring(href.indexOf(calendarFolder) + calendarFolder.length()))
    //        .collect(Collectors.toList());
  }
}
