package com.bpolite.data.enums;


public enum CalendarStatus {
	MUTED("Mute"),
	VIBRATE("Vibrate"),
	NONE("Not Active (Ignore this calendar)");

	CalendarStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	String value;

	@Override
	public String toString() {
		return this.getValue();
	}

	public static CalendarStatus getAsEnum(String str) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		for (CalendarStatus v : values()) {
			if (str.equalsIgnoreCase(v.getValue())) {
				return v;
			}
		}

		return NONE;
	}
}
