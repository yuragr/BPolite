package com.bpolite.utils;

import com.bpolite.IConst;
import com.bpolite.data.comparator.WeekDayComparator;
import com.bpolite.data.enums.WeekDay;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeekDayUtils {
    private static WeekDayComparator weekDayComparator = null;
    private static List<WeekDay> allWeekDays = null;

    static {
        weekDayComparator = new WeekDayComparator();
        if (CollectionUtils.isEmpty(allWeekDays)) {
            allWeekDays = new ArrayList<>(Arrays.asList(WeekDay.values()));
            Collections.sort(allWeekDays, weekDayComparator);
            allWeekDays = Collections.unmodifiableList(allWeekDays);
        }
    }

    /**
     * Create a string representation of the given week days. For example, "Monday, Thursday".
     * Also creates ranges. For example, "Sunday-Saturday"
     *
     * @param weekDays the given week days
     * @return string representation of the given week days
     */
    public static String convertListToCommaSeparatedStringForDisplay(List<WeekDay> weekDays) {
        StringBuilder result = new StringBuilder();
        int weekDaysAmount = WeekDay.values().length;

        if (CollectionUtils.isNotEmpty(weekDays)) {

            Set<WeekDay> weekDaysSet = new HashSet<>(weekDays);
            Collections.sort(weekDays, weekDayComparator);
            if (weekDaysSet.size() == WeekDay.values().length) {
                result.append(weekDays.get(0).toString()).append(" - ").append(weekDays.get(weekDays.size() - 1));
            } else {
                List<List<WeekDay>> weekDayGroups = new ArrayList<>(weekDaysAmount);
                List<WeekDay> currentGroup = new ArrayList<>(weekDaysAmount);
                for (int i = 0, j = 0; i < allWeekDays.size() && j < weekDays.size(); i++) {
                    if (allWeekDays.get(i).equals(weekDays.get(j))
                            && (currentGroup.isEmpty() || (i > 0 && currentGroup.get(currentGroup.size() - 1)
                            .equals(allWeekDays.get(i - 1))))) {
                        currentGroup.add(weekDays.get(j));
                        j++;
                    } else {
                        if (!currentGroup.isEmpty())
                            weekDayGroups.add(currentGroup);
                        currentGroup = new ArrayList<>(weekDaysAmount);
                    }
                }
                if (!currentGroup.isEmpty())
                    weekDayGroups.add(currentGroup);

                for (int i = 0; i < weekDayGroups.size(); i++) {
                    if (weekDayGroups.get(i).size() > 2)
                        result.append(weekDayGroups.get(i).get(0)).append(" - ").append(weekDayGroups.get(i).get(weekDayGroups.get(i).size() - 1));
                    else if (weekDayGroups.get(i).size() == 2)
                        result.append(weekDayGroups.get(i).get(0)).append(IConst.COMMA).append(" ").append(weekDayGroups.get(i).get(1));
                    else if (weekDayGroups.get(i).size() == 1)
                        result.append(weekDayGroups.get(i).get(0));
                    if (i + 1 < weekDayGroups.size())
                        result.append(IConst.COMMA).append(" ");
                }
            }
        }
        return result.toString();
    }
}
