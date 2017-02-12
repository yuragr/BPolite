package com.bpolite.data.comparator;

import com.bpolite.data.pojo.EventInstance;

import java.util.Comparator;

public class EventInstanceComparator implements Comparator<EventInstance> {
    @Override
    public int compare(EventInstance e1, EventInstance e2) {
        return Long.valueOf(e1.getEventTime()).compareTo(e2.getEventTime());
    }
}
