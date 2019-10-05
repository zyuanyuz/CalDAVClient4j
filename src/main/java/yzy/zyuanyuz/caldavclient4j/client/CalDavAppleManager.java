package yzy.zyuanyuz.caldavclient4j.client;

import com.github.caldav4j.CalDAVCollection;
import com.github.caldav4j.exceptions.BadStatusException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpGetMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.MethodUtil;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
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

import java.text.SimpleDateFormat;

/**
 * @author George Yu
 * @since 2019/9/27 14:21
 */
public class CalDavAppleManager extends CalDAVCollection {
  private HttpClient httpClient = null;
  private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

  public CalDavAppleManager() throws Exception {
    httpClient =
        HttpClients.custom()
            .setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            //            .setDefaultCredentialsProvider()
            .build();
    //    HttpHost httpHost = new HttpHost("p46-caldav.icloud.com", 80, "http");
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public Calendar getCalendar() throws Exception {
    Calendar calendar = null;
    HttpGetMethod getMethod =
        methodFactory.createGetMethod(
            "http://p46-caldav.icloud.com:80/published/2/MTY4ODQ0ODI2ODIxNjg4NAMjulTFlQOcdNX48keW2Xoo8ipE263TVr0DdPLG2toH");
    try {
      HttpResponse response = httpClient.execute(getDefaultHttpHost(getMethod.getURI()), getMethod);

      if (response.getStatusLine().getStatusCode() != CalDAVStatus.SC_OK) {
        MethodUtil.StatusToExceptions(getMethod, response);
        throw new BadStatusException(getMethod, response);
      }
      calendar = getMethod.getResponseBodyAsCalendar(response);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(calendar);
    return calendar;
  }

  public Calendar getCalendarWithAuth() throws Exception {
    HttpHost target = new HttpHost("p46-caldav.icloud.com", 443, "https");
    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(
        new AuthScope(target.getHostName(), target.getPort()),
        new UsernamePasswordCredentials("zoom2019097@icloud.com", "itkg-ogby-zxti-hpav"));
    httpClient =
        HttpClients.custom()
            .setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setDefaultCredentialsProvider(provider)
            .build();
    Calendar calendar = null;
    //    HttpGetMethod getMethod =
    //
    // methodFactory.createGetMethod("https://p46-caldav.icloud.com:443/16884482682/calendars/");
    //    DavPropertyNameSet names = new DavPropertyNameSet();
    //    names.add(DavPropertyName.create("displayname"));
    //    HttpPropFindMethod propFindMethod =
    //        methodFactory.createPropFindMethod(
    //            "https://p46-caldav.icloud.com:443/16884482682/calendars/", names, 0);

    CompFilter filter = new CompFilter("VCALENDAR");
    filter.addCompFilter(new CompFilter("VEVENT"));

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    CalendarData calendarData =
        new CalendarData(
            CalendarData.LIMIT,
            new DateTime(dateFormat.parse("20190101000000")),
            new DateTime(dateFormat.parse("20200101000000")),
            null);

    CalendarQuery query = new CalendarQuery(filter, calendarData, true, true);

    HttpCalDAVReportMethod reportMethod =
        methodFactory.createCalDAVReportMethod(
            "https://p46-caldav.icloud.com:443/16884482682/calendars/work", query, 1);
    try {
      // HttpResponse response = httpClient.execute(getDefaultHttpHost(getMethod.getURI()),
      // getMethod);
      HttpResponse response = httpClient.execute(reportMethod);
      //      if (response.getStatusLine().getStatusCode() != CalDAVStatus.SC_OK) {
      //        MethodUtil.StatusToExceptions(reportMethod, response);
      //        throw new BadStatusException(reportMethod, response);
      //      }
      System.out.println(EntityUtils.toString(response.getEntity()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(calendar);
    return calendar;
  }
}
