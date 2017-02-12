package com.bpolite.data.enums;

import android.provider.CalendarContract;

import com.bpolite.IConst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public enum EventAvailability {
    BUSY(CalendarContract.Events.AVAILABILITY_BUSY, "Busy"),
    FREE(CalendarContract.Events.AVAILABILITY_FREE, "Free"),
    TENTATIVE(CalendarContract.Events.AVAILABILITY_TENTATIVE, "Tentative");

    EventAvailability(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private int value;
    private String name;

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public int getValue() {
        return value;
    }

    public static EventAvailability getAsEnum(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        for (EventAvailability v : values()) {
            if (str.equalsIgnoreCase(v.getName())) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    public static EventAvailability getAsEnum(int value) {
        for (EventAvailability v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    public static List<EventAvailability> getAsEnumList(String listStr) {
        HashSet<EventAvailability> eventAvailabilitiesSet = new HashSet<>();
        if (listStr != null && !listStr.isEmpty()) {
            String[] tokens = listStr.split(IConst.COMMA);

            for (String token : tokens) {
                eventAvailabilitiesSet.add(getAsEnum(token));
            }
        }

        return new ArrayList<>(eventAvailabilitiesSet);
    }
}
