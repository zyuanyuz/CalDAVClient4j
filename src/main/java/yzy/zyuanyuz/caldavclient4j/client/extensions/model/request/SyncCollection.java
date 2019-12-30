package yzy.zyuanyuz.caldavclient4j.client.extensions.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.Prop;
import com.github.caldav4j.xml.OutputsDOMBase;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * this is a report method for WebDAV , so calendar data prop can't work with SyncCollection
 *
 * @author George.Yu
 * @since 2019/12/26 18:48
 * @see <a href = "https://tools.ietf.org/html/rfc6578#page-24">RFC6578 Page-24</a>
 */
public class SyncCollection extends OutputsDOMBase implements CalDAVReportRequest {
  public static final String ELEMENT_NAME = "sync-collection";
  public static final String ELEM_SYNC_TOKEN = "sync-token";
  public static final String ELEM_SYNC_LEVEL = "sync-level";
  public static final String ELEM_LIMIT = "limit";

  public static final String PARAM_SYNC_LEVEL_ONE = "1";
  public static final String PARAM_SYNC_LEVEL_INFINITE = "infinite";

  private String syncToken = null;
  private String syncLevel = null; /*1 or infinite*/
  private Limit limit = null;

  private Prop properties = new Prop();
  private CalendarData calendarData = new CalendarData();

  public SyncCollection() {}

  @SuppressWarnings("unchecked")
  public SyncCollection(
      Prop properties, String syncToken, String syncLevel, Limit limit, CalendarData calendarData) {
    this(syncToken, syncLevel, limit, calendarData);
    if (properties != null) {
      this.properties.addChildren(properties);
    }
  }

  public SyncCollection(
      DavPropertyNameSet properties,
      String syncToken,
      String syncLevel,
      Limit limit,
      CalendarData calendarData) {
    this(syncToken, syncLevel, limit, calendarData);
    if (properties != null) {
      this.properties.addChildren(properties);
    }
  }

  public SyncCollection(
      Collection<? extends XmlSerializable> properties,
      String syncToken,
      String syncLevel,
      Limit limit,
      CalendarData calendarData) {
    this(syncToken, syncLevel, limit, calendarData);
    if (properties != null) {
      this.properties.addChildren(properties);
    }
  }

  public SyncCollection(String syncToken, String syncLevel, Limit limit) {
    this.syncToken = syncToken;
    this.syncLevel = syncLevel;
    this.limit = limit;
  }

  public SyncCollection(
      String syncToken, String syncLevel, Limit limit, CalendarData calendarData) {
    this.syncToken = syncToken;
    this.syncLevel = syncLevel;
    this.limit = limit;
    this.calendarData = calendarData;
  }

  /** {@inheritDoc} */
  @Override
  protected String getElementName() {
    return ELEMENT_NAME;
  }

  /** {@inheritDoc} */
  @Override
  protected Namespace getNamespace() {
    return CalDAVConstants.NAMESPACE_WEBDAV;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Collection<? extends XmlSerializable> getChildren() {
    ArrayList<XmlSerializable> children = new ArrayList<>();
    children.add(
        new DefaultDavProperty<String>(
            ELEM_SYNC_TOKEN, this.syncToken, CalDAVConstants.NAMESPACE_WEBDAV));
    children.add(
        new DefaultDavProperty<String>(
            ELEM_SYNC_LEVEL,
            null == this.syncLevel ? "1" : this.syncLevel,   // if not set the syncLevel,set a default value "1"
            CalDAVConstants.NAMESPACE_WEBDAV));
    if (this.limit != null) children.add(this.limit);
    if ((this.properties != null && !this.properties.isEmpty()) || this.calendarData != null) {
      Prop tmp = new Prop();
      tmp.addChildren(this.properties.getChildren());
      if (this.calendarData != null) tmp.addChild(this.calendarData);
      children.add(tmp);
    }
    return children;
  }

  /** {@inheritDoc} */
  @Override
  protected Map<String, String> getAttributes() {
    return null; // do nothing
  }

  /** {@inheritDoc} */
  @Override
  protected String getTextContent() {
    return null;
  }

  /**
   * validate the
   *
   * @throws DOMValidationException
   */
  @Override
  public void validate() throws DOMValidationException {

    //    if (syncLevel == null || !syncLevel.matches("1|infinite")) {
    //      throw new DOMValidationException("sync-level is required and must be 1 or infinite");
    //    }
  }

  // getter and setter

  public String getSyncToken() {
    return syncToken;
  }

  public void setSyncToken(String syncToken) {
    this.syncToken = syncToken;
  }

  public String getSyncLevel() {
    return syncLevel;
  }

  public void setSyncLevel(String syncLevel) {
    this.syncLevel = syncLevel;
  }

  public Limit getLimit() {
    return limit;
  }

  public void setLimit(Limit limit) {
    this.limit = limit;
  }

  public Prop getProperties() {
    return properties;
  }

  public void setProperties(Prop properties) {
    this.properties = properties;
  }
}
