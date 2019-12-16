package us.zoom.web.calendar.api.enums;

import lombok.Getter;

/**
 * for demo & test
 * @author George
 * @date 2019/11/25 11:25
 */
public enum EventResponseState {

    /**
     * Pending State
     */
    PENDING(0, "pending"),

    /**
     * Meeting Invitation Accepted
     */
    ACCEPTED(1, "accepted"),

    /**
     * Meeting Invitation Declined
     */
    DECLINED(2, "declined");

    @Getter
    private int code;

    @Getter
    private String name;

    EventResponseState(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
