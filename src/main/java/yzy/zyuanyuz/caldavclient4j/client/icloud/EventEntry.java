package yzy.zyuanyuz.caldavclient4j.client.icloud;

import net.fortuna.ical4j.model.component.VEvent;

/**
 * @author zyuanyuz
 * @since 2019/10/20 22:36
 */
public class EventEntry {
  private String uuid;
  private String etag;
  private VEvent event;

  public EventEntry(String uuid, String etag, VEvent event) {
    this.uuid = uuid;
    this.etag = etag;
    this.event = event;
  }

  public EventEntry(String etag, VEvent event) {
    this.etag = etag;
    this.event = event;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }

  public VEvent getEvent() {
    return event;
  }

  public void setEvent(VEvent event) {
    this.event = event;
  }
}
