package com.bpolite.data.enums;

public enum EventInstanceType {
    START("start"),
    END("end");

    EventInstanceType(String value) {
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

    public static EventInstanceType getAsEnum(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        for (EventInstanceType v : values()) {
            if (str.equalsIgnoreCase(v.getValue())) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }
}
