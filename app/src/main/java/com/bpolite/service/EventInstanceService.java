package com.bpolite.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.enums.WeekDay;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;
import com.bpolite.data.repo.app.AppCalendarRepository;
import com.bpolite.data.repo.app.AppEventInstanceRepository;
import com.bpolite.utils.NotificationUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This service will be activated when a scheduled EventInstance will occur. This service will do the actual
 * mute / vibrate / ringer restore actions
 *
 * @author Yuri
 */
public class EventInstanceService extends IntentService {

    private static final Object syncLock = new Object();
    private Calendar calendar = null;

    public EventInstanceService() { // DO NOT REMOVE
        this("EventInstanceService");
    }

    public EventInstanceService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getSimpleName(), ">> onHandleIntent");

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();

        boolean userAction = intent.getBooleanExtra("userAction", false);
        if (userAction) {
            restoreRinger(audioManager, ringerMode, 0, 0, true);
        } else {
            String type = intent.getStringExtra("type");
            String title = intent.getStringExtra("title");
            EventAvailability availability = EventAvailability.getAsEnum(intent.getIntExtra("availability", 0));
            int eventInstanceHashCode = intent.getIntExtra("eventInstanceHashCode", 0);
            int calendarHashCode = intent.getIntExtra("calendarHashCode", 0);
            long startTime = intent.getLongExtra("startTime", 0);
            long endTime = intent.getLongExtra("endTime", 0);
            long delayTime = intent.getLongExtra("delayTime", 0);
            Log.d(this.getClass().getSimpleName(), "title=" + title + ", type=" + type
                    + ", calendarHashCode=" + calendarHashCode + ", eventInstanceHashCode="
                    + eventInstanceHashCode + ", startTime=" + startTime + ", endTime=" + endTime
                    + ", delayTime=" + delayTime);
            if (type != null && !type.isEmpty() && eventInstanceHashCode != 0 && calendarHashCode != 0
                    && startTime != 0 && endTime != 0) {
                EventInstanceType eventType = EventInstanceType.getAsEnum(type);
                calendar = AppCalendarRepository.getCalendarByHashCode(this, calendarHashCode);

                // check if this event still exists in the saved file
                if (eventInstanceExists(eventInstanceHashCode)) {

                    if (eventType.equals(EventInstanceType.START)) {
                        silenceRinger(audioManager, ringerMode, startTime, endTime, delayTime, availability);
                    }

                    if (eventType.equals(EventInstanceType.END)) {
                        restoreRinger(audioManager, ringerMode, startTime, endTime, false);
                    }
                }
            } else {
                Log.w(this.getClass().getSimpleName(), "nothing was done - invalid data");
            }
        }
        Log.d(this.getClass().getSimpleName(), "<< onHandleIntent");
    }

    private boolean eventInstanceExists(int eventInstanceHashCode) {
        boolean result = false;
        Set<EventInstance> eventInstances = AppEventInstanceRepository.loadEventInstancesFromXml(this);
        for (EventInstance eventInstance : eventInstances) {
            if (eventInstance.hashCode() == eventInstanceHashCode) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void restoreRinger(AudioManager audioManager, int ringerMode, long startTime, long endTime,
                               boolean userInitiated) {
        synchronized (syncLock) {
            if (ringerMode != AudioManager.RINGER_MODE_NORMAL) {

				/*
                 * check if there is another event that is ongoing at the same time and it will end later, and
				 * it will restore the ringer
				 */
                Set<EventInstance> eventInstances = AppEventInstanceRepository.loadEventInstancesFromXml(this);

                long maxEndTime = endTime;
                if (!eventInstances.isEmpty()) {
                    for (EventInstance eventInstance : eventInstances) {
                        if (endTime < eventInstance.getEndTime() && endTime > eventInstance.getStartTime()) {
                            maxEndTime = Math.max(maxEndTime, eventInstance.getEndTime()
                                    + eventInstance.getRingerRestoreDelay().getValue() * IConst.MINUTE);
                        }
                    }
                }

                if (userInitiated || endTime >= maxEndTime) {

                    // restore the ringer
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    NotificationUtils.removeNotification(this);
                    Log.d(this.getClass().getSimpleName(), "restoring ringer to normal");

                } else {

                    // there is another event that overlaps this event and it is longer. It will restore the ringer
                    NotificationUtils.removeNotification(this);
                    NotificationUtils.showNotification(this, startTime, maxEndTime, 0, calendar.getStatus());
                    Log.d(this.getClass().getSimpleName(),
                            "updating the ringer restore to be at "
                                    + IConst.LONG_DATE_FORMAT.format(new Date(maxEndTime)));
                }
            } else {
                Log.i(this.getClass().getSimpleName(),
                        "not restoring the ringer since it is already on");
            }
        }
    }

    /**
     * The method that does the silencing
     *
     * @param audioManager
     * @param ringerMode
     * @param startTime
     * @param endTime
     */
    private void silenceRinger(AudioManager audioManager, int ringerMode, long startTime, long endTime,
                               long delayTime, EventAvailability availability) {
        Log.d(this.getClass().getSimpleName(), ">> silenceRinger, startTime=" + startTime + ", endTime=" + endTime + ", delayTime=" + delayTime);
        if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            if (System.currentTimeMillis() < endTime) {
                if (isAllowedToSilenceOnWeekDay()) {
                    if (isAllowedToSilenceOnAvailability(availability)) {
                        disableShushPopup();
                        try {
                            Thread.sleep(IConst.SHUSH_RINGER_SILENCE_DELAY);
                        } catch (Exception e) {
                        }

                        // silence the ringer
                        if (calendar.getStatus().equals(CalendarStatus.VIBRATE))
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        else if (calendar.getStatus().equals(CalendarStatus.MUTED))
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                        Log.d(this.getClass().getSimpleName(), "silencing the ringer");
                        NotificationUtils.showNotification(this, startTime, endTime, delayTime,
                                calendar.getStatus());
                    } else {
                        Log.d(this.getClass().getSimpleName(),
                                "not silencing the ringer due to event availability: " + availability);
                    }
                } else {
                    Log.d(this.getClass().getSimpleName(),
                            "not silencing the ringer because not allowed to silence today");
                }
            } else {
                Log.w(this.getClass().getSimpleName(),
                        "not silencing the ringer because end time had passed");
            }
        } else if (calendar != null) {
            Log.i(this.getClass().getSimpleName(),
                    "not silencing the ringer since it is already silenced");

            // update the notification anyway
            NotificationUtils.showNotification(this, startTime, endTime, delayTime, calendar.getStatus());
        } else {
            Log.w(this.getClass().getSimpleName(),
                    "not silencing the ringer because calendar was not found!");
        }
        Log.d(this.getClass().getSimpleName(), "<< silenceRinger");
    }

    /**
     * Need to disable "Shush!" popup window when disabling the ringer<br/>
     * http://andydennie.com/shush-app-to-app-integration/
     */
    private void disableShushPopup() {
        Log.d(this.getClass().getSimpleName(), ">> disableShushPopup");

        // the custom action
        Intent intent = new Intent("com.androidintents.PRE_RINGER_MODE_CHANGE");

        // identify your app as the sender (optional, but useful)
        intent.putExtra("com.androidintents.EXTRA_SENDER", "com.your.app");

        // and away we go!
        getApplicationContext().sendBroadcast(intent);
        Log.d(this.getClass().getSimpleName(), "<< disableShushPopup");
    }

    /**
     * Check if the days that the calendar is active includes today
     *
     * @return
     */
    private boolean isAllowedToSilenceOnWeekDay() {
        boolean result = false;

        java.util.Calendar javaCalendar = java.util.Calendar.getInstance();
        int dayOfWeek = javaCalendar.get(java.util.Calendar.DAY_OF_WEEK);

        List<WeekDay> weekDays = calendar.getWeekDays();

        for (WeekDay weekDay : weekDays) {
            if (weekDay.getWeekDayNumber() == dayOfWeek) {
                result = true;
                break;
            }
        }

        return result;
    }

    private boolean isAllowedToSilenceOnAvailability(EventAvailability availability) {
        Log.d(this.getClass().getSimpleName(), ">> isAllowedToSilenceOnAvailability");
        boolean result = calendar.getEventAvailabilities().contains(availability);
        Log.d(this.getClass().getSimpleName(), "<< isAllowedToSilenceOnAvailability returns: " + result);
        return result;
    }
}
