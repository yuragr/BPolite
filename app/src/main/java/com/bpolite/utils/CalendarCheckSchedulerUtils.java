package com.bpolite.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bpolite.IConst;
import com.bpolite.event.CalendarCheckEvent;

public class CalendarCheckSchedulerUtils {
	public static void scheduleCalendarCheck(Context context) {
		cancelScheduledCalendarCheck(context);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, CalendarCheckEvent.class);
		PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + IConst.SCHEDULE_DELAY,
				IConst.CALENDAR_CHECK_PERIOD, pendingAlarmIntent);
	}

	public static void cancelScheduledCalendarCheck(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, CalendarCheckEvent.class);
		PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
		alarmManager.cancel(pendingAlarmIntent);
	}
}
