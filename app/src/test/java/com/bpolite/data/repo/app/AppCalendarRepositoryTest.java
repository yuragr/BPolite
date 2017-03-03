package com.bpolite.data.repo.app;

import com.bpolite.BuildConfig;
import com.bpolite.TestUtils;
import com.bpolite.data.pojo.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by yurig on 18-Feb-17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppCalendarRepositoryTest {
    @Test
    public void readWriteTest() {
        File file = null;
        try {
            file = File.createTempFile("bpolite_readWriteTest_", ".xml");
        } catch (IOException e) {
            fail("could not create a temporary file for write");
        }

        try {
            Calendar calendar1 = TestUtils.createCalendar();
            Calendar calendar2 = TestUtils.createCalendar();
            List<Calendar> expectedCalendars = Arrays.asList(calendar1, calendar2);

            AppCalendarRepository.saveCalendarsToXml(file, expectedCalendars);
            List<Calendar> actualCalendars = AppCalendarRepository.loadCalendarsFromXml(file);

            assertEquals(expectedCalendars.size(), actualCalendars.size());
            for (int i = 0; i < actualCalendars.size(); i++) {
                TestUtils.assertEqualsCalendar(expectedCalendars.get(i), actualCalendars.get(i));
            }
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }
}
