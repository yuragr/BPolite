package com.bpolite;

import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.enums.RingerDelay;
import com.bpolite.data.enums.WeekDay;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by yurig on 18-Feb-17.
 */

public class TestUtils {

    // generators

    public static EventInstance createEventInstance() {
        EventInstance eventInstance = new EventInstance();

        Calendar calendar = createCalendar();
        long now = System.currentTimeMillis();
        eventInstance.setCalendar(calendar);
        eventInstance.setStartTime(now + RandomUtils.nextInt(0, 1000));
        eventInstance.setEndTime(now + RandomUtils.nextInt(1000, 2000));
        eventInstance.setType(getRandomEnumValue(EventInstanceType.class));
        eventInstance.setActive(true);
        eventInstance.setRingerRestoreDelay(getRandomEnumValue(RingerDelay.class));
        eventInstance.setAvailability(getRandomEnumValue(EventAvailability.class));
        eventInstance.setTitle(UUID.randomUUID().toString());

        return eventInstance;
    }

    public static Calendar createCalendar() {
        Calendar calendar = new Calendar();

        calendar.setCalendarId(RandomUtils.nextLong());
        calendar.setDisplayName(UUID.randomUUID().toString());
        calendar.setAccountName(UUID.randomUUID().toString());
        calendar.setOwnerName(UUID.randomUUID().toString());
        calendar.setStatus(getRandomEnumValue(CalendarStatus.class));
        calendar.setWeekDays(getRandomEnumValues(WeekDay.class));
        calendar.setEventAvailabilities(getRandomEnumValues(EventAvailability.class));
        calendar.setRingerRestoreDelay(getRandomEnumValue(RingerDelay.class));

        return calendar;
    }

    public static <T> void assertEqualsList(List<T> expectedList, List<T> actualList) {
        if (expectedList == null) {
            assertNull(actualList);
        } else {
            assertTrue(actualList.containsAll(expectedList));
            assertTrue(expectedList.containsAll(actualList));
        }
    }

    public static <T> List<T> getRandomEnumValues(Class<T> enumType) {
        List<T> members = enumValues(enumType);

        int listSizeToReturn = RandomUtils.nextInt(1, members.size());
        while (members.size() > listSizeToReturn) {
            int indexToRemove = RandomUtils.nextInt(0, members.size());
            members.remove(indexToRemove);
        }
        return members;
    }


    public static <T> T getRandomEnumValue(Class<T> enumType) {
        List<T> members = enumValues(enumType);
        return members.get(RandomUtils.nextInt(0, members.size()));
    }

    public static <T> List<T> enumValues(Class<T> enumType) {
        List<T> members = new ArrayList<>();
        for (T c : enumType.getEnumConstants()) {
            members.add(c);
        }
        return members;
    }

    // Asserts

    public static void assertEqualsCalendar(Calendar expectedCalendar, Calendar actualCalendar) {
        assertEquals(expectedCalendar.getCalendarId(), actualCalendar.getCalendarId());
        assertEquals(expectedCalendar.getDisplayName(), actualCalendar.getDisplayName());
        assertEquals(expectedCalendar.getAccountName(), actualCalendar.getAccountName());
        assertEquals(expectedCalendar.getOwnerName(), actualCalendar.getOwnerName());
        assertEquals(expectedCalendar.getStatus(), actualCalendar.getStatus());
        assertEqualsList(expectedCalendar.getWeekDays(), actualCalendar.getWeekDays());
        assertEqualsList(expectedCalendar.getEventAvailabilities(), actualCalendar.getEventAvailabilities());
        assertEquals(expectedCalendar.getRingerRestoreDelay(), actualCalendar.getRingerRestoreDelay());
        assertEquals(expectedCalendar.hashCode(), actualCalendar.hashCode());
    }

    public static void assertEqualsEventInstance(EventInstance expectedEventInstance, EventInstance actualEventInstance) {
        assertEqualsCalendar(expectedEventInstance.getCalendar(), actualEventInstance.getCalendar());
        assertEquals(expectedEventInstance.getStartTime(), actualEventInstance.getStartTime());
        assertEquals(expectedEventInstance.getEndTime(), actualEventInstance.getEndTime());
        assertEquals(expectedEventInstance.getType(), actualEventInstance.getType());
        assertEquals(expectedEventInstance.getCalendarHashCode(), actualEventInstance.getCalendarHashCode());
        assertEquals(expectedEventInstance.isActive(), actualEventInstance.isActive());
        assertEquals(expectedEventInstance.getRingerRestoreDelay(), actualEventInstance.getRingerRestoreDelay());
        assertEquals(expectedEventInstance.getAvailability(), actualEventInstance.getAvailability());
        assertEquals(expectedEventInstance.getTitle(), actualEventInstance.getTitle());
    }
}
