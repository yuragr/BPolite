package com.bpolite.data.comparator;

import com.bpolite.data.pojo.Calendar;

import java.util.Comparator;

public class CalendarComparator implements Comparator<Calendar> {

	@Override
	public int compare(Calendar cal1, Calendar cal2) {
		int result = 0;

		if (cal1.getAccountName() != null && cal2.getAccountName() != null)
			result = cal1.getAccountName().toLowerCase().compareTo(cal2.getAccountName().toLowerCase());

		if (result == 0 && cal1.getDisplayName() != null && cal2.getDisplayName() != null)
			result = cal1.getDisplayName().toLowerCase().compareTo(cal2.getDisplayName().toLowerCase());

		return result;
	}
}
