package com.bpolite.data.pojo;

import com.bpolite.data.comparator.WeekDayComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.RingerRestoreDelay;
import com.bpolite.data.enums.WeekDay;
import com.bpolite.utils.WeekDayUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The calendar POJO
 *
 * @author Yuri
 */
public class Calendar implements Serializable {
    private static final long serialVersionUID = 983709719328371881L;
    private long calendarId;
    private String displayName;
    private String accountName;
    private String ownerName;
    private CalendarStatus status;
    private List<WeekDay> weekDays;
    private List<EventAvailability> eventAvailabilities;
    private RingerRestoreDelay ringerRestoreDelay;

    public Calendar() {
    }

    public Calendar(Calendar other) {
        this.calendarId = other.calendarId;
        this.displayName = other.displayName;
        this.ownerName = other.ownerName;
        this.displayName = other.displayName;
        this.status = other.status;
        this.weekDays = new ArrayList<>(other.weekDays);
        this.ringerRestoreDelay = other.ringerRestoreDelay;
        this.eventAvailabilities = new ArrayList<>(other.eventAvailabilities);
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public RingerRestoreDelay getRingerRestoreDelay() {
        if (ringerRestoreDelay == null)
            ringerRestoreDelay = RingerRestoreDelay.NO_DELAY;
        return ringerRestoreDelay;
    }

    public void setRingerRestoreDelay(RingerRestoreDelay ringerRestoreDelay) {
        this.ringerRestoreDelay = ringerRestoreDelay;
    }

    public List<WeekDay> getWeekDays() {
        if (weekDays == null)
            weekDays = new ArrayList<>(Arrays.asList(WeekDay.values()));
        return weekDays;
    }

    public void setWeekDays(List<WeekDay> weekDays) {
        this.weekDays = weekDays;
    }

    public Map<String, Serializable> getAsMap() {
        Map<String, Serializable> map = new HashMap<>();

        map.put("calendarId", calendarId);
        map.put("displayName", displayName);
        map.put("accountName", accountName);
        map.put("ownerName", ownerName);
        if (!status.equals(CalendarStatus.NONE)) {
            String statusStr = status.getValue();

            Collections.sort(getWeekDays(), new WeekDayComparator());

            statusStr += " on " + WeekDayUtils.convertListToCommaSeparatedStringForDisplay(getWeekDays());

            if (!getRingerRestoreDelay().equals(RingerRestoreDelay.NO_DELAY)) {
                statusStr += " (+" + getRingerRestoreDelay().getValue() + ")";
            }

            map.put("status", statusStr);
        } else
            map.put("status", status.getValue());

        return map;
    }

    public CalendarStatus getStatus() {
        return (status == null) ? CalendarStatus.NONE : status;
    }

    public void setStatus(CalendarStatus status) {
        this.status = status;
    }

    public void addWeekDay(WeekDay weekDay) {
        if (!getWeekDays().contains(weekDay))
            getWeekDays().add(weekDay);
    }

    public void removeWeekDay(WeekDay weekDay) {
        if (getWeekDays().contains(weekDay))
            getWeekDays().remove(weekDay);
    }

    public List<EventAvailability> getEventAvailabilities() {
        if (eventAvailabilities == null)
            eventAvailabilities = new ArrayList<>(Arrays.asList(EventAvailability.values()));
        return eventAvailabilities;
    }

    public void setEventAvailabilities(List<EventAvailability> eventAvailabilities) {
        this.eventAvailabilities = eventAvailabilities;
    }

    public void addEventAvailability(EventAvailability eventAvailability) {
        if (!getEventAvailabilities().contains(eventAvailability))
            getEventAvailabilities().add(eventAvailability);
    }

    public void removeEventAvailability(EventAvailability eventAvailability) {
        if (getEventAvailabilities().contains(eventAvailability))
            getEventAvailabilities().remove(eventAvailability);
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof Calendar) {
            Calendar other = (Calendar) o;
            if (this.calendarId != other.calendarId)
                return false;
            if (!StringUtils.equals(this.accountName, other.accountName))
                return false;
            if (!StringUtils.equals(this.ownerName, other.ownerName))
                return false;

            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("calendarId", calendarId)
                .append("displayName", displayName)
                .append("accountName", accountName)
                .append("ownerName", ownerName)
                .append("status", status.getValue())
                .toString();
    }

    @Override
    public int hashCode() {
        return ("" + calendarId + accountName + ownerName).hashCode();
    }
}
