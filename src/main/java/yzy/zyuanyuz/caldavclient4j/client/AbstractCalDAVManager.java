package yzy.zyuanyuz.caldavclient4j.client;

import com.github.caldav4j.CalDAVCollection;
import org.apache.http.client.HttpClient;

/**
 * @author zyuanyuz
 * @since 2019/10/15 20:59
 */
public abstract class AbstractCalDAVManager extends CalDAVCollection {
  protected HttpClient httpClient = null;

  protected String calName; // calendar folder name such as "work"

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpClient getHttpClient() {
    return this.httpClient;
  }

  public void setString(String calName) {
    this.calName = calName;
  }

  public String getCalName() {
    return this.calName;
  }
}
