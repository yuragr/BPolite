package com.bpolite.data.comparator;

import com.bpolite.data.enums.WeekDay;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by yurig on 11-Feb-17.
 */
public class WeekDayComparatorTest {

    @Test
    public void compare_self() {
        WeekDayComparator comparator = new WeekDayComparator();

        assertEquals(0, comparator.compare(WeekDay.MON, WeekDay.MON));
        assertEquals(0, comparator.compare(WeekDay.TUE, WeekDay.TUE));
        assertEquals(0, comparator.compare(WeekDay.WED, WeekDay.WED));
        assertEquals(0, comparator.compare(WeekDay.THU, WeekDay.THU));
        assertEquals(0, comparator.compare(WeekDay.FRI, WeekDay.FRI));
        assertEquals(0, comparator.compare(WeekDay.SAT, WeekDay.SAT));
        assertEquals(0, comparator.compare(WeekDay.SUN, WeekDay.SUN));
    }

    @Test
    public void compare_saturdayFirstDayOfWeek() {
        int firstDay = 6; // Sat
        WeekDayComparator comparator = new WeekDayComparator(firstDay);

        assertEquals(-1, comparator.compare(WeekDay.SAT, WeekDay.SUN));
        assertEquals(-1, comparator.compare(WeekDay.SAT, WeekDay.MON));
        assertEquals(-1, comparator.compare(WeekDay.SAT, WeekDay.TUE));
        assertEquals(-1, comparator.compare(WeekDay.SAT, WeekDay.WED));
        assertEquals(-1, comparator.compare(WeekDay.SAT, WeekDay.THU));
        assertEquals(-1, comparator.compare(WeekDay.SAT, WeekDay.FRI));

        List<WeekDay> weekDaysByLocale = comparator.getWeekDaysByLocale(firstDay);

        for (int i = 0; i < 6; i++) {
            assertEquals(-1, comparator.compare(weekDaysByLocale.get(i), weekDaysByLocale.get(i + 1)));
            assertEquals(0, comparator.compare(weekDaysByLocale.get(i), weekDaysByLocale.get(i)));
            assertEquals(1, comparator.compare(weekDaysByLocale.get(i + 1), weekDaysByLocale.get(i)));
        }
    }

    @Test
    public void compare_sundayFirstDayOfWeek() {
        int firstDay = 0; // Sun
        WeekDayComparator comparator = new WeekDayComparator(firstDay);

        assertEquals(-1, comparator.compare(WeekDay.SUN, WeekDay.MON));
        assertEquals(-1, comparator.compare(WeekDay.SUN, WeekDay.TUE));
        assertEquals(-1, comparator.compare(WeekDay.SUN, WeekDay.WED));
        assertEquals(-1, comparator.compare(WeekDay.SUN, WeekDay.THU));
        assertEquals(-1, comparator.compare(WeekDay.SUN, WeekDay.FRI));
        assertEquals(-1, comparator.compare(WeekDay.SUN, WeekDay.SAT));

        List<WeekDay> weekDaysByLocale = comparator.getWeekDaysByLocale(firstDay);

        for (int i = 0; i < 6; i++) {
            assertEquals(-1, comparator.compare(weekDaysByLocale.get(i), weekDaysByLocale.get(i + 1)));
            assertEquals(0, comparator.compare(weekDaysByLocale.get(i), weekDaysByLocale.get(i)));
            assertEquals(1, comparator.compare(weekDaysByLocale.get(i + 1), weekDaysByLocale.get(i)));
        }
    }

    @Test
    public void compare_mondayFirstDayOfWeek() {
        int firstDay = 1; // Mon
        WeekDayComparator comparator = new WeekDayComparator(firstDay);

        assertEquals(-1, comparator.compare(WeekDay.MON, WeekDay.TUE));
        assertEquals(-1, comparator.compare(WeekDay.MON, WeekDay.WED));
        assertEquals(-1, comparator.compare(WeekDay.MON, WeekDay.THU));
        assertEquals(-1, comparator.compare(WeekDay.MON, WeekDay.FRI));
        assertEquals(-1, comparator.compare(WeekDay.MON, WeekDay.SAT));
        assertEquals(-1, comparator.compare(WeekDay.MON, WeekDay.SUN));

        List<WeekDay> weekDaysByLocale = comparator.getWeekDaysByLocale(firstDay);

        for (int i = 0; i < 6; i++) {
            assertEquals(-1, comparator.compare(weekDaysByLocale.get(i), weekDaysByLocale.get(i + 1)));
            assertEquals(0, comparator.compare(weekDaysByLocale.get(i), weekDaysByLocale.get(i)));
            assertEquals(1, comparator.compare(weekDaysByLocale.get(i + 1), weekDaysByLocale.get(i)));
        }
    }

    @Test
    public void getWeekDaysByLocale_sundayFirstDayOfWeek() {
        WeekDayComparator comparator = new WeekDayComparator();

        List<WeekDay> weekDayList = comparator.getWeekDaysByLocale(0);

        assertEquals(WeekDay.SUN, weekDayList.get(0));
    }

    @Test
    public void getWeekDaysByLocale_mondayFirstDayOfWeek() {
        WeekDayComparator comparator = new WeekDayComparator();

        List<WeekDay> weekDayList = comparator.getWeekDaysByLocale(1);

        assertEquals(WeekDay.MON, weekDayList.get(0));
    }

    @Test
    public void getWeekDaysByLocale_saturdayFirstDayOfWeek() {
        WeekDayComparator comparator = new WeekDayComparator();

        List<WeekDay> weekDayList = comparator.getWeekDaysByLocale(6);

        assertEquals(WeekDay.SAT, weekDayList.get(0));
    }
}
