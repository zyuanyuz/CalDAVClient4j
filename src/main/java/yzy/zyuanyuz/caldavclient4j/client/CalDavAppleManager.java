package yzy.zyuanyuz.caldavclient4j.client;

import com.github.caldav4j.CalDAVCollection;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.CalDAVResource;
import com.github.caldav4j.exceptions.BadStatusException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpGetMethod;
import com.github.caldav4j.methods.HttpPropFindMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.MethodUtil;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.RandomUidGenerator;
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
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.w3c.dom.Document;
import yzy.zyuanyuz.caldavclient4j.util.AppleCalDAVUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author George Yu
 * @since 2019/9/27 14:21
 */
public class CalDavAppleManager extends CalDAVCollection {
  protected HttpClient httpClient;

  public CalDavAppleManager() throws Exception {
    this.httpClient =
        HttpClients.custom()
            .setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            //            .setDefaultCredentialsProvider()
            .build();
    setMethodFactory(new CalDAV4JMethodFactory());
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

  public Calendar getReportWithAuth() throws Exception {
    HttpHost target = new HttpHost("caldav.icloud.com", 443, "https");
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
    AppleCalDAVUtil.getEventUidList("work", httpClient, methodFactory);
    return new Calendar();
  }

  public Calendar getiCloudCalendar() throws Exception {
    HttpHost target = new HttpHost("caldav.icloud.com", 443, "https");
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
    setCalendarCollectionRoot("https://caldav.icloud.com:443/16884482682/calendars/work");
    Calendar calendar = getCalendar(httpClient, "/EFE46473-85FB-4CD6-BF4B-A383B1F8EBBF.ics");
    return calendar;
  }

  public void addEvent() throws Exception {
    HttpHost target = new HttpHost("caldav.icloud.com", 443, "https");
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
    //    this.calendarCollectionRoot =
    //        "https://p46-caldav.icloud.com:443/16884482682/calendars/work/";
    this.setCalendarCollectionRoot("https://p46-caldav.icloud.com:443/16884482682/calendars/work");
    // System.out.println(this.getAbsolutePath("EFE46473-85FB-4CD6-BF4B-A383B1F8EBBF.ics"));
    // System.out.println(getCalendarCollectionRoot());
    //    HttpGetMethod getMethod =
    //        methodFactory.createGetMethod(
    //
    // "https://p46-caldav.icloud.com:443/16884482682/calendars/work/EFE46473-85FB-4CD6-BF4B-A383B1F8EBBF.ics");
    //    HttpResponse response = httpClient.execute(getDefaultHttpHost(getMethod.getURI()),
    // getMethod);
    // Calendar calendar = getCalendar(httpClient, "EFE46473-85FB-4CD6-BF4B-A383B1F8EBBF.ics");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    Date start = dateFormat.parse("2019-10-22-18-00-00");
    Date end = dateFormat.parse("2019-10-22-20-00-00");
    VEvent event =
        new VEvent(
            new net.fortuna.ical4j.model.DateTime(start),
            new net.fortuna.ical4j.model.DateTime(end),
            "new day event!");
    Uid uid = new RandomUidGenerator().generateUid();
    event.getProperties().add(uid);
    add(httpClient, event, null);
    CalDAVResource caldavResource = getCalDAVResourceByUID(httpClient, "VEVENT", uid.getValue());

    System.out.println(caldavResource.getCalendar());
  }
}
