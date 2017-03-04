package com.bpolite.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.pojo.EventInstance;
import com.bpolite.service.EventInstanceService;

import java.util.Date;

/**
 * Created by yurig on 04-Mar-17.
 */

public class EventInstanceUtils {
    /**
     * Cancels the scheduled mute / vibrate action for the given event instance
     */
    public static void cancelEventInstance(Context context, EventInstance eventInstance) {
        if (eventInstance.isActive()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, EventInstanceService.class);
            putExtraIntentData(intent, eventInstance);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            alarmManager.cancel(pendingIntent);

            Log.d(EventInstanceUtils.class.getSimpleName(), "EventInstanceUtils: cancel - " + eventInstance.getTitle() + "(" + eventInstance.getType().getValue()
                    + " on " + IConst.LONG_DATE_FORMAT.format(new Date(eventInstance.getEventTime())) + ")");
        }
        eventInstance.setActive(false);
    }

    /**
     * Creates the scheduled mute / vibrate / restore the ringer alarm for the given event instance, that will do
     * the actual silencing
     */
    public static void activateEventInstance(Context context, EventInstance eventInstance) {

        long eventTime = eventInstance.getEventTime();

        // prepare the action for muting / silencing the ringer
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, EventInstanceService.class);
        putExtraIntentData(intent, eventInstance);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, eventTime, pendingIntent);

        Log.d(EventInstanceUtils.class.getSimpleName(), "EventInstanceUtils: activate - " + eventInstance.getTitle() + "(" + eventInstance.getType().getValue()
                + " on " + IConst.LONG_DATE_FORMAT.format(new Date(eventTime)) + ")");
    }

    private static void putExtraIntentData(Intent intent, EventInstance eventInstance) {
        intent.putExtra("eventInstanceHashCode", eventInstance.hashCode());
        intent.putExtra("calendarHashCode", eventInstance.getCalendarHashCode());
        intent.putExtra("type", eventInstance.getType().getValue());
        intent.putExtra("title", eventInstance.getTitle());
        intent.putExtra("startTime", eventInstance.getStartTime());
        intent.putExtra("endTime", eventInstance.getEndTime());
        intent.putExtra("delayTime", ((long) eventInstance.getRingerRestoreDelay().getValue()) * IConst.MINUTE);
        intent.putExtra("userAction", false);
        intent.putExtra("availability", eventInstance.getAvailability().getValue());
        intent.setAction("" + eventInstance.hashCode());
    }
}
