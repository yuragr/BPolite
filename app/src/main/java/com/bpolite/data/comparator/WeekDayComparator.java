package com.bpolite.data.comparator;

import com.bpolite.data.enums.WeekDay;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class WeekDayComparator implements Comparator<WeekDay> {

    private static WeekDay[] weekDays = WeekDay.values();
    private int firstDayOfWeekIndex = Calendar.getInstance().getFirstDayOfWeek() - 1;

    public WeekDayComparator() {
        firstDayOfWeekIndex = Calendar.getInstance().getFirstDayOfWeek() - 1;
    }

    /**
     *
     * @param firstDayOfWeekIndex 0 = Sun, 1 = Mon ...
     */
    public WeekDayComparator(int firstDayOfWeekIndex) {
        this.firstDayOfWeekIndex = firstDayOfWeekIndex;
    }


    @Override
    public int compare(WeekDay wd1, WeekDay wd2) {
        if (wd1.equals(wd2)) {
            return 0;
        }

        if (wd1.equals(weekDays[firstDayOfWeekIndex])) {
            return -1;
        }

        if (wd2.equals(weekDays[firstDayOfWeekIndex])) {
            return 1;
        }

        for (WeekDay weekDay : getWeekDaysByLocale(firstDayOfWeekIndex)) {
            if (weekDay.equals(wd1))
                return -1;
            else if (weekDay.equals(wd2))
                return 1;
        }

        return 0;
    }

    protected List<WeekDay> getWeekDaysByLocale(int firstDayOfWeekIndex) {
        LinkedList<WeekDay> result;
        result = new LinkedList<>(Arrays.asList(weekDays));
        for (int i = 0; i < firstDayOfWeekIndex; i++) {
            result.addLast(result.removeFirst());
        }
        return result;
    }
}
