package com.bpolite.data.enums;


public enum RingerDelay {
    NO_DELAY("0 minutes", 0), ONE_MIN("1 minute", 1), TWO_MIN("2 minutes", 2), FIVE_MIN("5 minutes", 5), TEN_MIN(
            "10 minutes", 10), FIFTEEN_MIN("15 minutes", 15), TWENTY_MIN("20 minutes", 20);

    RingerDelay(String name, int value) {
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

    public static CharSequence[] getNames() {
        RingerDelay[] allValues = RingerDelay.values();
        CharSequence[] names = new CharSequence[allValues.length];
        for (int i = 0; i < allValues.length; i++) {
            names[i] = allValues[i].getName();
        }
        return names;
    }

    public static RingerDelay getByValue(int value) {
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
