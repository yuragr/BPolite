package com.bpolite.utils;

import com.bpolite.data.enums.WeekDay;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by yurig on 11-Feb-17.
 */

public class WeekDayUtilsTest {
    @Test
    public void convertListToCommaSeparatedStringForDisplay_singleGroup() {
        for (int i = 6; i >= 2; i--) {
            List<WeekDay> weekDayList = Arrays.asList(Arrays.copyOfRange(WeekDay.values(), 0, i + 1));
            String result = WeekDayUtils.convertListToCommaSeparatedStringForDisplay(weekDayList);
            if (WeekDay.values()[0] == WeekDay.values()[i]) {
                assertEquals(WeekDay.values()[0].toString(), result);
            } else {
                assertEquals(WeekDay.values()[0].toString() + " - " + WeekDay.values()[i].toString(), result);
            }
        }

        for (int i = 0; i <= 4; i++) {
            List<WeekDay> weekDayList = Arrays.asList(Arrays.copyOfRange(WeekDay.values(), i, 7));
            String result = WeekDayUtils.convertListToCommaSeparatedStringForDisplay(weekDayList);
            if (WeekDay.values()[i] == WeekDay.values()[6]) {
                assertEquals(WeekDay.values()[6].toString(), result);
            } else {
                assertEquals(WeekDay.values()[i].toString() + " - " + WeekDay.values()[6].toString(), result);
            }
        }
    }

    @Test
    public void convertListToCommaSeparatedStringForDisplay_adjacentDays() {
        for (int i = 0; i < 6; i++) {
            List<WeekDay> weekDayList = Arrays.asList(Arrays.copyOfRange(WeekDay.values(), i, i + 2));
            String result = WeekDayUtils.convertListToCommaSeparatedStringForDisplay(weekDayList);
            assertEquals(WeekDay.values()[i].toString() + ", " + WeekDay.values()[i + 1].toString(), result);
        }
    }
}
