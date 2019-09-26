package yzy.zyuanyuz.caldavclient4j.client;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;

import java.io.InputStream;

/**
 * @author George Yu
 * @since 2019/9/26 13:38
 */
public class BaseCalDavClient extends HttpClient {
  protected HostConfiguration hostConfiguration = null;

  public CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

  public BaseCalDavClient(
      String host, int port, String protocol, String username, String password) {
    HostConfiguration hostConfiguration = new HostConfiguration();
    hostConfiguration.setHost(host, port, protocol);
    this.hostConfiguration = hostConfiguration;
    Credentials credentials = new UsernamePasswordCredentials(username, password);
    this.getParams().setAuthenticationPreemptive(true);
    this.getState().setCredentials(AuthScope.ANY, credentials);
  }

  public BaseCalDavClient(HostConfiguration configuration, String username, String password) {
    this(
        configuration.getHost(),
        configuration.getPort(),
        configuration.getProtocol().toString(),
        username,
        password);
  }

    protected Calendar getCalendarResource(String resourceName) {
        Calendar cal;

        InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(resourceName);
        CalendarBuilder cb = new CalendarBuilder();

        try {
            cal = cb.build(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return cal;
    }

}
