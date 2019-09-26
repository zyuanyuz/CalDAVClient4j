package yzy.zyuanyuz.caldavclient4j.client;

import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.PostMethod;

import java.io.IOException;

/**
 * @author George Yu
 * @since 2019/9/26 9:20
 */
public class CalDAVManager extends CalDAVCollection {

  private HttpClient httpClient = null;
  private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

  public CalDAVManager() {

    HostConfiguration hostConfiguration = new HostConfiguration();
    hostConfiguration.setHost("localhost", 5232, "http");

    httpClient = new HttpClient();
    httpClient.setHostConfiguration(hostConfiguration);
    Credentials credentials = new UsernamePasswordCredentials("root", "root");
    httpClient.getParams().setAuthenticationPreemptive(true);
    httpClient.getState().setCredentials(AuthScope.ANY, credentials);

    this.setHostConfiguration(httpClient.getHostConfiguration());
    this.setCalendarCollectionRoot("/");
    this.setMethodFactory(new CalDAV4JMethodFactory());
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

//  public void addEvent(HttpClient client, String path, VEvent event) {
//    PostMethod postMethod = methodFactory.createPostMethod();
//    postMethod.setPath();
//    postMethod.setRequestBody(event);
//    try {
//      client.executeMethod(client.getHostConfiguration(), postMethod);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
}
