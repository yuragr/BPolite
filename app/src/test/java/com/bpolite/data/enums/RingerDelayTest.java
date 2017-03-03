package com.bpolite.data.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by yurig on 03-Mar-17.
 */

public class RingerDelayTest {
    @Test
    public void getByValueTest() {
        for (RingerDelay ringerDelay : RingerDelay.values()) {
            assertEquals(ringerDelay, RingerDelay.getByValue(ringerDelay.getValue()));
        }
    }
}
