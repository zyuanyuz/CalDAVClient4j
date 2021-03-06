package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.model.response.CalendarDataProperty;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.extensions.parameter.Email;
import net.fortuna.ical4j.extensions.parameter.Filename;
import net.fortuna.ical4j.model.ParameterFactoryRegistry;
import net.fortuna.ical4j.model.PropertyFactoryRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import yzy.zyuanyuz.caldavclient4j.client.extensions.parameter.ManagedID;
import yzy.zyuanyuz.caldavclient4j.client.extensions.parameter.Size;

/**
 * @author George.Yu
 * @date 2019/12/25 10:59
 */
public class ICloudCalendarDataProperty extends CalendarDataProperty {

    /**
     * add some not standard parameters(such as EMAIL,FILENAME,SIZE etc.) to resolve the ICloud calendar
     */
    static{
        ParameterFactoryRegistry parameterFactoryRegistry = new ParameterFactoryRegistry();
        parameterFactoryRegistry.register("EMAIL", new Email.Factory());
        parameterFactoryRegistry.register("FILENAME",new Filename.Factory());
        parameterFactoryRegistry.register("SIZE",new Size.Factory());
        parameterFactoryRegistry.register("MANAGED-ID",new ManagedID.Factory());

        ThreadLocal<CalendarBuilder> threadLocal =
                ThreadLocal.withInitial(
                        () ->
                                new CalendarBuilder(
                                        CalendarParserFactory.getInstance().createParser(),
                                        new PropertyFactoryRegistry(),
                                        parameterFactoryRegistry,
                                        TimeZoneRegistryFactory.getInstance().createRegistry()));
        CalendarDataProperty.setCalendarBuilderThreadLocal(threadLocal);
    }

}
