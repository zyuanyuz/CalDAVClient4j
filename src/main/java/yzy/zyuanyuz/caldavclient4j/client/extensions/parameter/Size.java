package yzy.zyuanyuz.caldavclient4j.client.extensions.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

/**
 * <pre>
 * https://tools.ietf.org/html/draft-ietf-calext-caldav-attachments-04#page-18
 *
 * Parameter Name:  SIZE
 *
 *    Purpose:  Provide filename for attachments specified with {@link net.fortuna.ical4j.model.property.Attach}.
 *
 *    Format Definition:  This property parameter is defined by the
 *       following notation:
 *
 *    sizeparam = "SIZE" "=" paramtext
 *    ; positive integers
 *
 *    Description:  This property parameter MAY be specified on "ATTACH"
 *       properties.  It indicates the size in octets of the corresponding
 *       attachment data.  Since iCalendar integer values are restricted to
 *       a maximum value of 2147483647, the current parameter is defined as
 *       text to allow an extended range to be used.
 *
 *    Example:
 *
 *    ATTACH;SIZE=1234:https://attachments.example.com/abcd.txt
 * </pre>
 *  {@link }
 *
 * @author George.Yu
 * @since 2019/12/26 10:37
 *
 */
public class Size extends Parameter {

    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_NAME = "SIZE";

    private String value;

    public Size(String value){
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

        public Parameter createParameter(final String value) {
            return new Size(value);
        }
    }
}
