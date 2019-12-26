package yzy.zyuanyuz.caldavclient4j.client.extensions.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.xml.OutputsDOMBase;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import java.util.Collection;
import java.util.Map;

/**
 * TODO implement this class for WebDAV sync [RFC6578]
 * @author zyuanyuz
 * @since 2019/12/26 22:19
 */

public class Limit extends OutputsDOMBase {
    public static final String ELEMENT_NAME = "limit";
    public static final String ELEM_NRESULTS = "nresults";

    private Integer nResults = null;

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    @Override
    protected Collection<? extends XmlSerializable> getChildren() {
        return null;
    }

    @Override
    protected Map<String, String> getAttributes() {
        return null;
    }

    @Override
    protected String getTextContent() {
        return null;
    }
}
