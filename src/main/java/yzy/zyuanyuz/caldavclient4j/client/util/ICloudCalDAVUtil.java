package yzy.zyuanyuz.caldavclient4j.client.util;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author George Yu
 * @since 2019/10/29 17:32
 */
public abstract class ICloudCalDAVUtil {

    public static String getUidFromHref(String href){
        return href.substring(href.lastIndexOf("/")+1,href.indexOf(".ics"));
    }

    public static List<VEvent> getEventsFromCalendars(List<Calendar> calendars){
        return calendars.stream().map(c->(VEvent)c.getComponent(Component.VEVENT)).collect(Collectors.toList());
    }
}
