package yzy.zyuanyuz.caldavclient4j.client.commons;

import org.apache.http.HttpHost;

import java.util.Arrays;
import java.util.List;

/**
 * @author zyuanyuz
 * @since 2019/10/20 11:14
 */
public final class ICloudCalDAVConstants {
  private ICloudCalDAVConstants() {}

  // some http args
  public static final String ICLOUD_CALDAV_HOST_PORT_STR = "https://caldav.icloud.com:443/";
  public static final String ICLOUD_CALDAV_HOST_STR = "https://caldav.icloud.com/";
  public static final HttpHost ICLOUD_CALDAV_HOST = new HttpHost("caldav.icloud.com", 443, "https");
  public static final int APPLE_CALDAV_PORT = 443;
  public static final String APPLE_CALDAV_URL_SCHEME = "https";

  // some dav property names
  public static final String CURRENT_USER_PRINCIPAL_STR = "current-user-principal";

  // the default not resource names
  public static final List<String> DEFAULT_NOT_RESOURCE_NAMES =
      Arrays.asList("outbox", "notification");
}
