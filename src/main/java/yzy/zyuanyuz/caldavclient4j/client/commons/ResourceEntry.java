package yzy.zyuanyuz.caldavclient4j.client.commons;

/**
 * @author zyuanyuz
 * @since 2019/12/17 22:01
 */
public class ResourceEntry {
  private String href;
  private String displayName;

  public ResourceEntry(String href, String displayName) {
    this.href = href;
    this.displayName = displayName;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getResourceName() {
    return displayName;
  }

  public void setResourceName(String resourceName) {
    this.displayName = resourceName;
  }

  @Override
  public String toString() {
    return "Resource - href:" + this.href + " displayName:" + this.displayName;
  }
}
