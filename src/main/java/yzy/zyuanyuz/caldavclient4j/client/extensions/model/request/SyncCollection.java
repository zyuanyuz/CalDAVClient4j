package yzy.zyuanyuz.caldavclient4j.client.extensions.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.model.request.Prop;
import com.github.caldav4j.xml.OutputsDOMBase;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import java.util.Collection;
import java.util.Map;

/**
 * @author George.Yu
 * @since 2019/12/26 18:48
 * @see
 */
public class SyncCollection extends OutputsDOMBase implements CalDAVReportRequest {
  public static final String ELEMENT_NAME = "sync-collection";
  public static final String ELEM_SYNC_TOKEN = "sync-token";
  public static final String ELEM_SYNC_LEVEL = "sync-level";
  public static final String ELEM_LIMIT = "limit";   //TODO this time don't care the parameter

  private String syncToken = null;
  private String syncLevel = null; /*1 or infinite*/
  private Limit limit = null;

  private Prop properties = new Prop();

  public SyncCollection() {}

  public SyncCollection(DavPropertyNameSet properties,String syncToken, String syncLevel, String limit){

  }

  /** {@inheritDoc} */
  @Override
  protected String getElementName() {
    return ELEMENT_NAME;
  }

  /** {@inheritDoc} */
  @Override
  protected Namespace getNamespace() {
    return CalDAVConstants.NAMESPACE_CALDAV;
  }

  @Override
  protected Collection<? extends XmlSerializable> getChildren() {
    return null; // do nothing
  }

  /** {@inheritDoc} */
  @Override
  protected Map<String, String> getAttributes() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  protected String getTextContent() {
    return null;
  }
}
