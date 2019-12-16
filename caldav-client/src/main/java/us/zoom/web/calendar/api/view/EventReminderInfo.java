package us.zoom.web.calendar.api.view;

import lombok.Data;

/**
 * @author George
 * @date 2019/11/25 11:20
 */
@Data
public class EventReminderInfo {
    private Boolean remindBefore;

    private Boolean remindAfter;

    private Integer minutesBeforeStart;

    private Integer minutesAfterEnd;
}
