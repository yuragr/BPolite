package com.bpolite.utils;

import com.bpolite.IConst;
import com.bpolite.data.comparator.WeekDayComparator;
import com.bpolite.data.enums.WeekDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class StringUtils {
	public static boolean isStringsEqual(String string1, String string2) {
		return isStringsEqual(string1, string2, false);
	}

	public static boolean isStringsEqualIgnoreCase(String string1, String string2) {
		return isStringsEqual(string1, string2, true);
	}

	private static boolean isStringsEqual(String string1, String string2, boolean ignoreCase) {
		if (string1 == string2)
			return true;

		if (string1 != null && string2 != null) {
			return ignoreCase ? string1.equalsIgnoreCase(string2) : string1.equals(string2);
		}
		return false;
	}

	public static String convertListToCommaSeparatedString(ArrayList<WeekDay> weekDays) {
		String result = "";

		if (weekDays != null) {
			for (int i = 0; i < weekDays.size(); i++) {
				result += weekDays.get(i).toString();
				if (i + 1 < weekDays.size())
					result += IConst.COMMA;
			}
		}
		return result;
	}

	public static String convertListToCommaSeparatedStringForDisplay(ArrayList<WeekDay> weekDays) {
		String result = "";
		int weekDaysAmount = WeekDay.values().length;

		if (weekDays != null && !weekDays.isEmpty()) {
			WeekDayComparator comparator = new WeekDayComparator();

			HashSet<WeekDay> weekDaysSet = new HashSet<WeekDay>(weekDays);
			Collections.sort(weekDays, comparator);
			if (weekDaysSet.size() == WeekDay.values().length) {
				return weekDays.get(0).toString() + " - " + weekDays.get(weekDays.size() - 1);
			}

			ArrayList<WeekDay> allWeekDays = new ArrayList<WeekDay>(Arrays.asList(WeekDay.values()));
			Collections.sort(allWeekDays, comparator);

			ArrayList<ArrayList<WeekDay>> weekDayGroups = new ArrayList<ArrayList<WeekDay>>(weekDaysAmount);
			ArrayList<WeekDay> currentGroup = new ArrayList<WeekDay>(weekDaysAmount);
			for (int i = 0, j = 0; i < allWeekDays.size() && j < weekDays.size(); i++) {
				if (allWeekDays.get(i).equals(weekDays.get(j))
						&& (currentGroup.isEmpty() || (i > 0 && currentGroup.get(currentGroup.size() - 1)
								.equals(allWeekDays.get(i - 1))))) {
					currentGroup.add(weekDays.get(j));
					j++;
				} else {
					if (!currentGroup.isEmpty())
						weekDayGroups.add(currentGroup);
					currentGroup = new ArrayList<WeekDay>(weekDaysAmount);
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
