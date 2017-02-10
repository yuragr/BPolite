package com.bpolite.data.enums;


public enum RingerRestoreDelay {
	NO_DELAY("No delay", 0), ONE_MIN("1 minute", 1), TWO_MIN("2 minutes", 2), FIVE_MIN("5 minutes", 5), TEN_MIN(
			"10 minutes", 10), FIFTEEN_MIN("15 minutes", 15), TWENTY_MIN("20 minutes", 20);

	RingerRestoreDelay(String name, int value) {
		this.name = name;
		this.value = value;
	}

	private String name;
	private int value;

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public static RingerRestoreDelay getAsEnum(String str) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		for (RingerRestoreDelay v : values()) {
			if (str.equalsIgnoreCase(v.getName())) {
				return v;
			}
		}
		return RingerRestoreDelay.NO_DELAY;
	}

	public static RingerRestoreDelay getAsEnum(int number) {
		for (RingerRestoreDelay v : values()) {
			if (number == v.getValue()) {
				return v;
			}
		}
		return RingerRestoreDelay.NO_DELAY;
	}

	public static CharSequence[] getNames() {
		RingerRestoreDelay[] allValues = RingerRestoreDelay.values();
		CharSequence[] names = new CharSequence[allValues.length];
		for (int i = 0; i < allValues.length; i++) {
			names[i] = allValues[i].getName();
		}
		return names;
	}

	public static RingerRestoreDelay getByValue(int value) {
		switch (value) {
			case 0:
				return NO_DELAY;
			case 1:
				return ONE_MIN;
			case 2:
				return TWO_MIN;
			case 5:
				return FIVE_MIN;
			case 10:
				return TEN_MIN;
			case 15:
				return FIFTEEN_MIN;
			case 20:
				return TWENTY_MIN;
			default:
				return NO_DELAY;
		}
	}
}
