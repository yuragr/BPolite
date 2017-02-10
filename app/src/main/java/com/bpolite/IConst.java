package com.bpolite;

import java.text.SimpleDateFormat;

public interface IConst {
	public static final String CALENDARS_FILE = "Calendars.xml";
	public static final String EVENT_INSTANCES_FILE = "EventInstances.xml";

	public static final String SEMICOLON = ";";
	public static final String UNDERSCORE = "_";
	public static final String COMMA = ",";

	public static final long WEEK = 7L * 24L * 60L * 60L * 1000L;
	public static final long DAY = 24L * 60L * 60L * 1000L;
	public static final long HOUR = 60L * 60L * 1000L;
	public static final long MINUTE = 60L * 1000L;
	public static final long SECOND = 1000L;

    /** how much we should wait after notifying "Shush!" app not to activate popup */
    public static final long SHUSH_RINGER_SILENCE_DELAY = 1L * SECOND;
	public static final long CALENDAR_CHECK_PERIOD = 2L * HOUR;
	public static final long SCHEDULE_DELAY = 2L * SECOND;

	public static final int ALL_DAY_EVENT = 1;

	public static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("HH:mm");
}
