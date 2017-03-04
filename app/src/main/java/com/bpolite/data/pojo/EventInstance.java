package com.bpolite.data.pojo;

import com.bpolite.IConst;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.enums.RingerDelay;

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
    private RingerDelay ringerRestoreDelay;
    private EventAvailability availability = EventAvailability.BUSY;

    /**
     * Event title in the calendar
     */
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

    public RingerDelay getRingerRestoreDelay() {
        if (calendar != null)
            return ringerRestoreDelay = calendar.getRingerRestoreDelay();
        if (ringerRestoreDelay == null) {
            ringerRestoreDelay = RingerDelay.NO_DELAY;
        }
        return ringerRestoreDelay;
    }

    public void setRingerRestoreDelay(RingerDelay ringerRestoreDelay) {
        this.ringerRestoreDelay = ringerRestoreDelay;
    }

    public EventAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = EventAvailability.getAsEnum(availability);
    }

    public void setAvailability(EventAvailability availability) {
        this.availability = availability;
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


    public long getEventTime() {
        return this.type.equals(EventInstanceType.START) ? startTime : endTime
                + getRingerRestoreDelay().getValue() * IConst.MINUTE;
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
