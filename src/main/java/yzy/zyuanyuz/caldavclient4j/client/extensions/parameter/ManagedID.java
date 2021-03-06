package yzy.zyuanyuz.caldavclient4j.client.extensions.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;


/**
 * <pre>
 *     Parameter Name:  MANAGED-ID
 *
 *    Purpose:  To uniquely identify a managed attachment.
 *
 *    Format Definition:  This property parameter is defined by the
 *       following notation:
 *
 *    managedidparam = "MANAGED-ID" "=" paramtext
 *
 *    Description:  This property parameter MUST be specified on "ATTACH"
 *       properties corresponding to managed attachments.  Its value is
 *       generated by the server and uniquely identifies a managed
 *       attachment within the scope of the CalDAV server.  This property
 *       parameter MUST NOT be present in the case of non-managed
 *       attachments.
 *
 *    Example:
 *
 *    ATTACH;MANAGED-ID=aUNhbGVuZGFy:https://attachments.example.c
 *     om/abcd.txt
 * </pre>
 *
 * @author George.Yu
 * @since 2019/12/26 12:38
 * @see <a href="https://tools.ietf.org/html/draft-ietf-calext-caldav-attachments-04#page-19"/>
 */
public class ManagedID extends Parameter {
    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_NAME = "MANAGED-ID";

    private String value;

    public ManagedID(String value){
        super(PARAMETER_NAME,new Factory());
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory extends Content.Factory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PARAMETER_NAME);
        }

        public net.fortuna.ical4j.model.Parameter createParameter(final String value) {
            return new ManagedID(value);
        }
    }
}
