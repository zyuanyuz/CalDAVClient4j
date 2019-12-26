package yzy.zyuanyuz.caldavclient4j.client.extensions.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.xml.OutputsDOMBase;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import java.util.Collection;
import java.util.Map;

/**
 * @author George.Yu
 * @since 2019/12/26 18:48
 */
public class SyncCollection extends OutputsDOMBase implements CalDAVReportRequest {
  public static final String ELEMENT_NAME = "sync-collection";

  public SyncCollection() {}

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
    return null;  //do nothing
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
