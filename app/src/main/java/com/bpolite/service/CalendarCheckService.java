package com.bpolite.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;
import com.bpolite.data.repo.CalendarRepository;
import com.bpolite.data.repo.EventInstanceRepository;
import com.bpolite.utils.CalendarCheckSchedulerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CalendarCheckService extends IntentService {
	public static final Object calendarCheckLock = new Object();

	public CalendarCheckService(String name) {
		super(name);
	}

	public CalendarCheckService() {
		this("CalendarCheckService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		synchronized (calendarCheckLock) {

			// check if the app is active (it means that at least one calendar should be treated by the app)
			ArrayList<Calendar> calendars = CalendarRepository.getCalendars(this);
			HashSet<EventInstance> savedEventInstances = EventInstanceRepository
					.getEventInstancesFromXml(this);
			if (!isAppActive(calendars)) {
				Log.d(this.getClass().getSimpleName(),
						"App is not active. No calendar should be checked");

				CalendarCheckSchedulerUtils.cancelScheduledCalendarCheck(this);

				// since we don't have any active calendars, we have to cancel all the pending EventInstances notifications
				if (!savedEventInstances.isEmpty()) {
					for (EventInstance eventInstance : savedEventInstances) {
						eventInstance.cancel(this);
						EventInstanceRepository.saveEventInstancesToXml(this, savedEventInstances);
					}
				}
				return;
			}

			Log.d(this.getClass().getSimpleName(),
					"App is active. Checking calendar events...");

			HashSet<Calendar> managedCalendars = new HashSet<Calendar>();

			HashMap<Calendar, HashSet<EventInstance>> savedEventInstancesMap = new HashMap<Calendar, HashSet<EventInstance>>();
			for (EventInstance savedEventInstance : savedEventInstances) {
				if (!savedEventInstancesMap.containsKey(savedEventInstance.getCalendar())) {
					savedEventInstancesMap
							.put(savedEventInstance.getCalendar(), new HashSet<EventInstance>());
				}
				savedEventInstancesMap.get(savedEventInstance.getCalendar()).add(savedEventInstance);
			}

			boolean eventInstancesChanged = false;

			// find only the managed calendars. cancel events of non managed calendars
			for (Calendar calendar : calendars) {
				if (calendar.getStatus().equals(CalendarStatus.NONE)) {
					if (savedEventInstancesMap.containsKey(calendar)) {
						HashSet<EventInstance> nonRelevantEvents = savedEventInstancesMap.get(calendar);
						for (EventInstance nonReleventEvent : nonRelevantEvents) {
							nonReleventEvent.cancel(this);
							eventInstancesChanged = true;
						}
					}
				} else {
					managedCalendars.add(calendar);
				}
			}

			// search for new events and make sure that the saved events still exist
			for (Calendar managedCalendar : managedCalendars) {
				HashSet<EventInstance> deviceEventInstances = EventInstanceRepository
						.getDeviceEventInstances(this, managedCalendar);

				// activate the new event instances
				for (EventInstance deviceEventInstance : deviceEventInstances) {
					if (!savedEventInstances.contains(deviceEventInstance)) {
						savedEventInstances.add(deviceEventInstance);
						deviceEventInstance.activate(this);
						eventInstancesChanged = true;
					}
				}

				// cancel non existing events or those which are out of date
				if (savedEventInstancesMap.containsKey(managedCalendar)) {
					for (EventInstance savedEventInstance : savedEventInstancesMap.get(managedCalendar)) {
						if (!deviceEventInstances.contains(savedEventInstance)
								|| savedEventInstance.getEndTime() < System.currentTimeMillis()) {
							savedEventInstance.cancel(this);
							eventInstancesChanged = true;
						}
					}
				}
			}

			if (eventInstancesChanged) {
				Log.d(this.getClass().getSimpleName(), "eventInstancesChanged=true");
				EventInstanceRepository.saveEventInstancesToXml(this, savedEventInstances);
			}
		}
	}

	private boolean isAppActive(ArrayList<Calendar> calendars) { // TODO do this check with a system property instead of reading from the file system
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
