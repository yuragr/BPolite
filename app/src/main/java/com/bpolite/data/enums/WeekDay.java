package com.bpolite.data.enums;

import com.bpolite.IConst;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public enum WeekDay {
	SUN(Calendar.SUNDAY,	"Sunday"),
	MON(Calendar.MONDAY,	"Monday"),
	TUE(Calendar.TUESDAY,	"Tuesday"),
	WED(Calendar.WEDNESDAY, "Wednesday"),
	THU(Calendar.THURSDAY, 	"Thursday"),
	FRI(Calendar.FRIDAY,	"Friday"),
	SAT(Calendar.SATURDAY, 	"Saturday");

	WeekDay(int weekDayNumber, String name) {
		this.name = name;
		this.weekDayNumber = weekDayNumber;
	}

	public String getName() {
		return name;
	}

	private String name;
	private int weekDayNumber;

	@Override
	public String toString() {
		return this.getName();
	}

	public int getWeekDayNumber() {
		return weekDayNumber;
	}

	public static WeekDay getAsEnum(String str) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		for (WeekDay v : values()) {
			if (str.equalsIgnoreCase(v.getName())) {
				return v;
			}
		}
		throw new IllegalArgumentException();
	}

	public static ArrayList<WeekDay> getAsEnumList(String listStr) {
		HashSet<WeekDay> weekDaysSet = new HashSet<>();
		if (listStr != null && !listStr.isEmpty()) {
			String[] tokens = listStr.split(IConst.COMMA);

			for (String token : tokens) {
				weekDaysSet.add(getAsEnum(token));
			}
		}

		return new ArrayList<>(weekDaysSet);
	}
}
