package com.bpolite.data.pojo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.enums.RingerRestoreDelay;
import com.bpolite.service.EventInstanceService;

import java.io.Serializable;
import java.util.Date;

public class EventInstance implements Serializable {
	private static final long serialVersionUID = -7223204725837140553L;
	private Calendar calendar;
	private long startTime;
	private long endTime;
	private EventInstanceType type;
	private int calendarHashCode = 0;
	private boolean active = true;
	private RingerRestoreDelay ringerRestoreDelay;

	/** Event title in the calendar */
	private String title;

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public EventInstanceType getType() {
		return (type == null) ? EventInstanceType.START : this.type;
	}

	public void setType(EventInstanceType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setActive(boolean active) {
		this.active = active;
		validateActiveValue();
	}

	public boolean isActive() {
		validateActiveValue();
		return active;
	}

	public RingerRestoreDelay getRingerRestoreDelay() {
		if (calendar != null)
			return ringerRestoreDelay = calendar.getRingerRestoreDelay();
		if (ringerRestoreDelay == null) {
			ringerRestoreDelay = RingerRestoreDelay.NO_DELAY;
		}
		return ringerRestoreDelay;
	}

	public void setRingerRestoreDelay(RingerRestoreDelay ringerRestoreDelay) {
		this.ringerRestoreDelay = ringerRestoreDelay;
	}

	private void validateActiveValue() {
		if (active) {
			long now = System.currentTimeMillis();
			if (now > endTime)
				active = false;
		}
	}

	public int getCalendarHashCode() {
		if (calendar != null)
			return calendar.hashCode();
		else
			return calendarHashCode;
	}

	public void setCalendarHashCode(int calendarHashCode) {
		if (calendar != null)
			throw new RuntimeException("cannot set calendarHashCode because there is an existing calendar");
		this.calendarHashCode = calendarHashCode;
	}

	/**
	 * Cancels the scheduled mute / vibrate action for this event instance
	 */
	public void cancel(Context context) {
		if (active) {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, EventInstanceService.class);
			putExtraIntentData(intent);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
			//			Log.d(this.getClass().getSimpleName(), "EventInstance: cancel - " + title + "(" + type.getValue()
			//					+ " on " + IConst.LONG_DATE_FORMAT.format(new Date(getEventTime())) + ")");
		}
		active = false;
	}

	/**
	 * Creates the scheduled mute / vibrate / restore the ringer alarm for this event instance, that will do
	 * the actual silencing
	 */
	public void activate(Context context) {

		long eventTime = getEventTime();

		// prepare the action for muting / silencing the ringer
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, EventInstanceService.class);
		putExtraIntentData(intent);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, eventTime, pendingIntent);

		Log.d(EventInstance.class.getSimpleName(), "EventInstance: activate - " + title + "(" + type.getValue()
				+ " on " + IConst.LONG_DATE_FORMAT.format(new Date(eventTime)) + ")");
	}

	private long getEventTime() {
		return this.type.equals(EventInstanceType.START) ? startTime : endTime
				+ getRingerRestoreDelay().getValue() * IConst.MINUTE;
	}

	private void putExtraIntentData(Intent intent) {
		intent.putExtra("eventInstanceHashCode", this.hashCode());
		intent.putExtra("calendarHashCode", calendar.hashCode());
		intent.putExtra("type", this.getType().getValue());
		intent.putExtra("title", this.getTitle());
		intent.putExtra("startTime", this.getStartTime());
		intent.putExtra("endTime", this.getEndTime());
		intent.putExtra("delayTime", ((long) this.getRingerRestoreDelay().getValue()) * IConst.MINUTE);
		intent.putExtra("userAction", false);
		intent.setAction("" + this.hashCode());
	}

	public String toString() {
		String start = IConst.LONG_DATE_FORMAT.format(new Date(startTime));
		String end = IConst.LONG_DATE_FORMAT.format(new Date(endTime));
		return "EventInstance[calendar=" + calendar.getDisplayName() + ", title=" + title + ", startTime="
				+ start + ", endTime=" + end + ", type=" + type.getValue() + "]";
	}

	public boolean equals(Object o) {
		if (o instanceof EventInstance) {
			EventInstance other = (EventInstance) o;

			if (this.startTime == other.startTime && this.endTime == other.endTime
					&& this.type.equals(other.type)) {
				if (this.calendar != null && other.calendar != null) {
					if (this.calendar.equals(other.calendar))
						return true;
				}
			}
		}
		return false;
	}

	public int hashCode() {
		return ("" + calendar.hashCode() + startTime + endTime + type.getValue() + getRingerRestoreDelay()
				.getValue()).hashCode();
	}
}
