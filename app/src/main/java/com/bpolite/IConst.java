package com.bpolite;

import java.text.SimpleDateFormat;

public interface IConst {
    String CALENDARS_FILE = "Calendars.xml";
    String EVENT_INSTANCES_FILE = "EventInstances.xml";

    String COMMA = ",";

    long WEEK = 7L * 24L * 60L * 60L * 1000L;
    long DAY = 24L * 60L * 60L * 1000L;
    long HOUR = 60L * 60L * 1000L;
    long MINUTE = 60L * 1000L;
    long SECOND = 1000L;

    /**
     * how much we should wait after notifying "Shush!" app not to activate popup
     */
    long SHUSH_RINGER_SILENCE_DELAY = SECOND;
    long CALENDAR_CHECK_PERIOD = 2L * HOUR;
    long SCHEDULE_DELAY = 2L * SECOND;

    int ALL_DAY_EVENT = 1;

    SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("HH:mm");
}
