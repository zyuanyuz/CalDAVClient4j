package us.zoom.web.calendar.core.domain;

// import lombok.Data;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;
// import us.zoom.web.calendar.api.enums.CalendarEventState;
// import us.zoom.web.calendar.api.enums.CalendarProviderType;
 import us.zoom.web.calendar.api.view.CalendarEventAttendee;
// import us.zoom.web.calendar.api.view.ConferenceInfo;
// import us.zoom.web.calendar.api.view.EventReminderInfo;
// import us.zoom.web.commons.cache.core.intercepter.key.Versionable;

import lombok.Data;
 import us.zoom.web.calendar.api.view.ConferenceInfo;
 import us.zoom.web.calendar.api.view.EventReminderInfo;

 import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author : Joey Yang
 * @see us.zoom.web.calendar.api.view.CalendarEventInfo
 * @since : 2019-05-20 18:05
 */
@Data
// @Document(collection = "zm_cal_event_item")
public class EventItem {

    //@Id
    private String eventId;

    private String resourceId;

    /**
     * The client id if scheduled by Zoom clients (ZRC, ZRP, IM...)
     * If it is invited or from calendar page of Google/Exchange, it should be empty
     */
    private String scheduledBy;

    /**
     * identical to changeKey in Exchange
     */
    private String etag;

    /**
     * @see CalendarEventState
     */
    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String timeZone;

    /**
     * by minute
     */
    private Integer duration;

    private Boolean recurring;

    private String recurringEventId;

    private Boolean allDay;

    /**
     * identical to sensitivity/visibility: public or private
     */
    private Boolean privateEvent;

    private String location;

    private String topic;

    private String description;

    private List<CalendarEventAttendee> attendees;

    private EventReminderInfo reminder;

    private ConferenceInfo conferenceInfo;

    /**
     * @see CalendarProviderType
     */
    private Integer calendarProvider;

    private String organizerName;

    private String organizerEmail;

    /**
     * Customer properties, String-String map
     */
    private Map<String, String> customProperties;

    private Boolean deleted;

    private Boolean isCanceled;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    // @Override
    // public String toString() {
    //     return "EventItem(topic=" + this.getTopic() + ",eventId=" + this.getEventId() + ", resourceId=" + this.getResourceId() +
    //             ", scheduledBy=" + this.getScheduledBy() + ", etag=" + this.getEtag() + ", status=" +
    //             this.getStatus() + ", startTime=" + this.getStartTime() + ", endTime=" + this.getEndTime() +
    //             ", timeZone=" + this.getTimeZone() + ", privateEvent=" + this.getPrivateEvent() +
    //             ", calendarProvider=" + this.getCalendarProvider() + "...)";
    // }
}
