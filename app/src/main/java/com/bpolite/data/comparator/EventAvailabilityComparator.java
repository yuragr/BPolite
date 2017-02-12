package com.bpolite.data.comparator;

import com.bpolite.data.enums.EventAvailability;

import java.util.Comparator;

public class EventAvailabilityComparator implements Comparator<EventAvailability> {
    @Override
    public int compare(EventAvailability o1, EventAvailability o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
