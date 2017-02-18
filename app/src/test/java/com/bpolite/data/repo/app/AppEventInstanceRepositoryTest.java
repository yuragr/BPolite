package com.bpolite.data.repo.app;

import com.bpolite.BuildConfig;
import com.bpolite.TestUtils;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.enums.RingerRestoreDelay;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by yurig on 18-Feb-17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppEventInstanceRepositoryTest {
    @Test
    public void readWriteTest() {
        File file = null;
        try {
            file = File.createTempFile("bpolite_readWriteTest_", ".xml");
        } catch (IOException e) {
            fail("could not create a temporary file for write");
        }

        try {
            Map<Integer, Calendar> calendarsHashCodeMap = new HashMap<>();

            EventInstance eventInstance1 = TestUtils.createEventInstance();
            calendarsHashCodeMap.put(eventInstance1.getCalendar().hashCode(), eventInstance1.getCalendar());

            EventInstance eventInstance2 = TestUtils.createEventInstance();
            calendarsHashCodeMap.put(eventInstance2.getCalendar().hashCode(), eventInstance2.getCalendar());

            List<EventInstance> expectedEventInstances = Arrays.asList(eventInstance1, eventInstance2);
            AppEventInstanceRepository.saveEventInstancesToXml(file, expectedEventInstances);
            List<EventInstance> actualEventInstances = AppEventInstanceRepository.loadEventInstancesFromXml(calendarsHashCodeMap, file);

            assertEquals(expectedEventInstances.size(), actualEventInstances.size());
            for (int i = 0; i < actualEventInstances.size(); i++) {
                TestUtils.assertEqualsEventInstance(expectedEventInstances.get(i), actualEventInstances.get(i));
            }
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }
}
