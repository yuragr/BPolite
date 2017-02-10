package com.bpolite.data.comparator;

import com.bpolite.data.enums.WeekDay;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Locale;

public class WeekDayComparator implements Comparator<WeekDay> {

	private static LinkedList<WeekDay> weekDaysByLocale = null;
	private static WeekDay[] days = WeekDay.values();
	private static int firstDayOfWeekIndex = Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek();

	@Override
	public int compare(WeekDay wd1, WeekDay wd2) {
		if (wd1.equals(wd2)) {
			return 0;
		}

		if (wd1.equals(days[firstDayOfWeekIndex - 1])) {
			return -1;
		}

		if (wd2.equals(days[firstDayOfWeekIndex - 1])) {
			return 1;
		}

		if (weekDaysByLocale == null) {
			weekDaysByLocale = new LinkedList<>(Arrays.asList(days));
			for (int i = 0; i < firstDayOfWeekIndex - 1; i++) {
				weekDaysByLocale.addLast(weekDaysByLocale.removeFirst());
			}
		}

		for (WeekDay weekDay : weekDaysByLocale) {
			if (weekDay.equals(wd1))
				return -1;
			else if (weekDay.equals(wd2))
				return 1;
		}

		return 0;
	}
}
