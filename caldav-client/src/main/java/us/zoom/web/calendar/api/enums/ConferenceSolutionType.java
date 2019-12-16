package us.zoom.web.calendar.api.enums;

import lombok.Getter;

/**
 * for demo & test
 * @author George
 * @date 2019/11/25 11:23
 */
public enum ConferenceSolutionType {
    /**
     * Not a video/voice conference
     */
    NONE(0, "None"),

    /**
     * Zoom Meeting
     */
    ZOOM(1, "Zoom"),

    /**
     * WebEX Meeting
     */
    WEBEX(2, "WebEX"),

    /**
     * Skype Meeting
     */
    SKYPE4BUSINESS(3, "SkypeForBusiness"),

    /**
     * BLUEJEANS Meeting
     */
    BLUEJEANS(4, "BlueJeans"),

    /**
     * Google GoToMeeting
     */
    GOTOMEETING(5, "GoToMeeting"),

    /**
     * real connect Meeting
     */
    REAL_CONNECT(6, "realConnect"),

    /**
     * General SIP Meeting
     */
    GENERAL_SIP(7, "SIPMeeting");

    @Getter
    private int code;

    @Getter
    private String name;

    ConferenceSolutionType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
