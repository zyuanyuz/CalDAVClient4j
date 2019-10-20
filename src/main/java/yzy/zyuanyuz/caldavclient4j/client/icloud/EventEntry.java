package yzy.zyuanyuz.caldavclient4j.client.icloud;

import net.fortuna.ical4j.model.component.VEvent;

/**
 * @author zyuanyuz
 * @since 2019/10/20 22:36
 */
public class EventEntry {
  private String uuid;
  private VEvent event;

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public VEvent getEvent() {
    return event;
  }

  public void setEvent(VEvent event) {
    this.event = event;
  }
}
