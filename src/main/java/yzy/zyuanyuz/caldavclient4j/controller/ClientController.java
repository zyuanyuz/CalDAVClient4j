package yzy.zyuanyuz.caldavclient4j.controller;

import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author George Yu
 * @since 2019/9/25 9:32
 */
@RestController
public class ClientController {
  @GetMapping("/caldav")
  public String getEvent() throws Exception {

    HostConfiguration hostConfiguration = new HostConfiguration();
    hostConfiguration.setHost("localhost", 5232, "http");

    HttpClient httpClient = new HttpClient();
    httpClient.setHostConfiguration(hostConfiguration);
    Credentials credentials = new UsernamePasswordCredentials("root", "root");
    httpClient.getParams().setAuthenticationPreemptive(true);
    httpClient.getState().setCredentials(AuthScope.ANY, credentials);

    CalDAVCollection calDAVCollection = new CalDAVCollection();
    calDAVCollection.setHostConfiguration(httpClient.getHostConfiguration());
    calDAVCollection.setCalendarCollectionRoot("/");
    calDAVCollection.setMethodFactory(new CalDAV4JMethodFactory());
    Calendar cal =
        calDAVCollection.getCalendar(httpClient, "/root/68ab2a13-ee99-31d8-5baa-4594545fcd36/");

    return cal.toString();
  }
}
