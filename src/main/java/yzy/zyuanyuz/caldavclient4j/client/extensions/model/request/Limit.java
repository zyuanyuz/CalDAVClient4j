package yzy.zyuanyuz.caldavclient4j.client.extensions.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.xml.OutputsDOMBase;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * TODO implement this class for WebDAV sync [RFC6578]
 * <pre>
 *     <!ELEMENT limit (nresults) >
 *    <!ELEMENT nresults (#PCDATA)> <!-- only digits -->
 *
 *    The DAV:limit XML element contains requested limits from the client
 *    to limit the size of the reply or amount of effort expended by the
 *    server.  The DAV:nresults XML element contains a requested maximum
 *    number of DAV:response elements to be returned in the response body.
 *    The server MAY disregard this limit.  The value of this element is an
 *    unsigned integer.
 * </pre>
 *
 * @author zyuanyuz
 * @since 2019/12/26 22:19
 * @see <a href="https://tools.ietf.org/html/rfc5323#section-5.17">RFC5323 Section-5.17</a>
 */
public class Limit extends OutputsDOMBase {
  public static final String ELEMENT_NAME = "limit";
  public static final String ELEM_NRESULTS = "nresults";

  private Integer nResults = null;

  public Limit() {}

  public Limit(int nResults) {
    this.nResults = nResults;
  }

  @Override
  protected String getElementName() {
    return ELEMENT_NAME;
  }

  @Override
  protected Namespace getNamespace() {
    return CalDAVConstants.NAMESPACE_WEBDAV;
  }

  @Override
  protected Collection<? extends XmlSerializable> getChildren() {
    ArrayList<XmlSerializable> children = new ArrayList<>();
    children.add(
        new DefaultDavProperty<Integer>(
            ELEM_NRESULTS, this.nResults, CalDAVConstants.NAMESPACE_WEBDAV));
    return children;
  }

  @Override
  protected Map<String, String> getAttributes() {
    return null;
  }

  @Override
  protected String getTextContent() {
    return null;
  }

  // getter and setter

  public Integer getnResults() {
    return nResults;
  }

  public void setnResults(Integer nResults) {
    this.nResults = nResults;
  }
}
