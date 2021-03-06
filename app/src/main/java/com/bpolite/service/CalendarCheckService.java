package com.bpolite.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bpolite.data.comparator.EventInstanceComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;
import com.bpolite.data.repo.app.AppCalendarRepository;
import com.bpolite.data.repo.app.AppEventInstanceRepository;
import com.bpolite.data.repo.device.DeviceEventInstanceRepository;
import com.bpolite.utils.CalendarCheckSchedulerUtils;
import com.bpolite.utils.EventInstanceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarCheckService extends IntentService {
    public static final Object calendarCheckLock = new Object();

    public CalendarCheckService(String name) {
        super(name);
    }

    public CalendarCheckService() { // DO NOT REMOVE
        this("CalendarCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (calendarCheckLock) {

            List<Calendar> calendars = AppCalendarRepository.getCalendars(this);
            Set<EventInstance> savedEventInstances = AppEventInstanceRepository.loadEventInstancesFromXml(this);

            if (!isAppActive(calendars)) {
                Log.d(this.getClass().getSimpleName(), "App is not active. No calendar should be checked");
                CalendarCheckSchedulerUtils.cancelScheduledCalendarCheck(this);

                // since we don't have any active calendars, we have to cancel all the pending EventInstances notifications
                cancelAllEventInstances(savedEventInstances);
                return;
            }

            Log.d(this.getClass().getSimpleName(), "App is active. Checking calendar events...");

            Set<Calendar> managedCalendars = new HashSet<>();

            Map<Calendar, Set<EventInstance>> savedEventInstancesMap = new HashMap<>();
            for (EventInstance savedEventInstance : savedEventInstances) {
                if (!savedEventInstancesMap.containsKey(savedEventInstance.getCalendar())) {
                    savedEventInstancesMap.put(savedEventInstance.getCalendar(), new HashSet<EventInstance>());
                }
                savedEventInstancesMap.get(savedEventInstance.getCalendar()).add(savedEventInstance);
            }

            boolean eventInstancesChanged = false;

            // find only the managed calendars. cancel events of non managed calendars
            for (Calendar calendar : calendars) {
                if (calendar.getStatus().equals(CalendarStatus.NONE)) {
                    if (savedEventInstancesMap.containsKey(calendar)) {
                        Set<EventInstance> nonRelevantEvents = savedEventInstancesMap.get(calendar);
                        for (EventInstance nonRelevantEvent : nonRelevantEvents) {
                            EventInstanceUtils.cancelEventInstance(this, nonRelevantEvent);
                            eventInstancesChanged = true;
                        }
                    }
                } else {
                    managedCalendars.add(calendar);
                }
            }

            // search for new events and make sure that the saved events still exist
            for (Calendar managedCalendar : managedCalendars) {
                Set<EventInstance> deviceEventInstances = DeviceEventInstanceRepository.getDeviceEventInstances(this, managedCalendar);

                // activate the new event instances
                List<EventInstance> deviceEventInstancesList = new ArrayList<>(deviceEventInstances);
                Collections.sort(deviceEventInstancesList, new EventInstanceComparator());
                for (EventInstance deviceEventInstance : deviceEventInstancesList) { // TODO order by time?
                    if (!savedEventInstances.contains(deviceEventInstance)) {
                        savedEventInstances.add(deviceEventInstance);
                        EventInstanceUtils.activateEventInstance(this, deviceEventInstance);
                        eventInstancesChanged = true;
                    }
                }

                // cancel non existing events or those which are out of date
                if (savedEventInstancesMap.containsKey(managedCalendar)) {
                    for (EventInstance savedEventInstance : savedEventInstancesMap.get(managedCalendar)) {
                        if (!deviceEventInstances.contains(savedEventInstance) || savedEventInstance.getEndTime() < System.currentTimeMillis()) {
                            EventInstanceUtils.cancelEventInstance(this, savedEventInstance);
                            eventInstancesChanged = true;
                        }
                    }
                }
            }

            if (eventInstancesChanged) {
                Log.d(this.getClass().getSimpleName(), "eventInstancesChanged=true");
                AppEventInstanceRepository.saveEventInstancesToXml(this, savedEventInstances);
            }
        }
    }

    private void cancelAllEventInstances(Set<EventInstance> eventInstances) {
        boolean needToSave = false;

        for (EventInstance eventInstance : eventInstances) {
            if (eventInstance.isActive()) {
                EventInstanceUtils.cancelEventInstance(this, eventInstance);
                needToSave = true;
            }
        }
        if (needToSave) {
            AppEventInstanceRepository.saveEventInstancesToXml(this, eventInstances);
        }
    }

    /**
     * The App is considered "active" when at least one calendar should be treated by the app
     *
     * @param calendars
     * @return true if the app is active
     */
    private boolean isAppActive(List<Calendar> calendars) { // TODO do this check with a system property instead of reading from the file system
        boolean result = false;

        if (calendars != null && !calendars.isEmpty()) {
            for (Calendar calendar : calendars) {
                if (!calendar.getStatus().equals(CalendarStatus.NONE)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
