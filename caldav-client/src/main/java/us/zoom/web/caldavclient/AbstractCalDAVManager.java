package us.zoom.web.caldavclient;

import com.github.caldav4j.CalDAVCollection;
import org.apache.http.client.HttpClient;

/**
 * @author George Yu
 * @since 2019/10/14 18:33
 */
public class AbstractCalDAVManager extends CalDAVCollection {
  protected HttpClient httpClient;

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }
}
