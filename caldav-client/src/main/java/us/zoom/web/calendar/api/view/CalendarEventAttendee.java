package us.zoom.web.calendar.api.view;

import lombok.Data;

/**
 * @author George
 * @date 2019/11/25 11:19
 */
@Data
public class CalendarEventAttendee {
    private String displayName;

    private String email;

    private Boolean required;

    /**
     * @see
     */
    private Integer responseStatus;
}
