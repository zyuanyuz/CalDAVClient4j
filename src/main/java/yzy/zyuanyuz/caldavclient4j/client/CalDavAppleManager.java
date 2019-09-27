package yzy.zyuanyuz.caldavclient4j.client;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.springframework.stereotype.Component;

/**
 * @author George Yu
 * @since 2019/9/27 14:21
 */

public class CalDavAppleManager extends CalDAVCollection {
  private HttpClient httpClient = null;
  private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

  public CalDavAppleManager() throws Exception {

    HostConfiguration hostConfiguration = new HostConfiguration();
    //    hostConfiguration.setHost("caldav.icloud.com", 443, "https");
    hostConfiguration.setHost("p46-caldav.icloud.com",443,"https");

    httpClient = new HttpClient();
    httpClient.setHostConfiguration(hostConfiguration);
    Credentials credentials =
        new UsernamePasswordCredentials("zoom2019097@icloud.com", "itkg-ogby-zxti-hpav");
    httpClient.getParams().setAuthenticationPreemptive(true);
    httpClient.getState().setCredentials(AuthScope.ANY, credentials);

    this.setHostConfiguration(httpClient.getHostConfiguration());
    this.setCalendarCollectionRoot("/16884482682/calendars");
    this.setMethodFactory(new CalDAV4JMethodFactory());
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }
}
