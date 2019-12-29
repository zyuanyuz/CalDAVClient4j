package yzy.zyuanyuz.caldavclient4j.client.icloud;

import yzy.zyuanyuz.caldavclient4j.client.AbstractCalDAVCalendar;

/**
 * @author zyuanyuz
 * @since 2019/12/28 22:09
 */
public class ICloudCalendar extends AbstractCalDAVCalendar {

  public IResource iResource() {
    return new IResource();
  }

  public class IResource {}

  public IEvent iEvent() {
    return new IEvent();
  }

  public class IEvent {
    public Get get(String resourceId) {
      return new Get();
    }

    public class Get {}

    public List list(String resourceId) {
      return new List();
    }

    public class List {}
  }

  /** ICloudCalendar builder */
  public static final class Builder {
    public Builder() {}

    public ICloudCalendar build() {
      // new ICloudCalendar(this);
      return null;
    }
  }
}
