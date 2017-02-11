package com.bpolite.utils;

import com.bpolite.IConst;
import com.bpolite.data.comparator.WeekDayComparator;
import com.bpolite.data.enums.WeekDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class WeekDayUtils {

	public static String convertListToCommaSeparatedStringForDisplay(List<WeekDay> weekDays) {
		String result = "";
		int weekDaysAmount = WeekDay.values().length;

		if (weekDays != null && !weekDays.isEmpty()) {
			WeekDayComparator comparator = new WeekDayComparator();

			HashSet<WeekDay> weekDaysSet = new HashSet<>(weekDays);
			Collections.sort(weekDays, comparator);
			if (weekDaysSet.size() == WeekDay.values().length) {
				return weekDays.get(0).toString() + " - " + weekDays.get(weekDays.size() - 1);
			}

			ArrayList<WeekDay> allWeekDays = new ArrayList<>(Arrays.asList(WeekDay.values())); // TODO can be static member
			Collections.sort(allWeekDays, comparator);

			ArrayList<ArrayList<WeekDay>> weekDayGroups = new ArrayList<>(weekDaysAmount);
			ArrayList<WeekDay> currentGroup = new ArrayList<>(weekDaysAmount);
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
					result += weekDayGroups.get(i).get(0) + " - "
							+ weekDayGroups.get(i).get(weekDayGroups.get(i).size() - 1);
				else if (weekDayGroups.get(i).size() == 2)
					result += weekDayGroups.get(i).get(0) + IConst.COMMA + " " + weekDayGroups.get(i).get(1);
				else if (weekDayGroups.get(i).size() == 1)
					result += weekDayGroups.get(i).get(0);
				if (i + 1 < weekDayGroups.size())
					result += IConst.COMMA + " ";
			}
		}
		return result;
	}
}
