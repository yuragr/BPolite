package com.bpolite.data.repo.device;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;
import com.bpolite.data.repo.app.AppEventInstanceRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yurig on 12-Feb-17.
 */

public class DeviceEventInstanceRepository {
    public static final String[] EVENT_PROJECTION = new String[]{CalendarContract.Instances.TITLE, // 0
            CalendarContract.Instances.BEGIN, // 1
            CalendarContract.Instances.END, // 2
            CalendarContract.Instances.ALL_DAY, // 3
            CalendarContract.Instances.AVAILABILITY // 4
    };

    // The indexes for the projection array above.
    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_END_INDEX = 2;
    private static final int PROJECTION_ALL_DAY_INDEX = 3;
    private static final int PROJECTION_AVAILABILITY_INDEX = 4;

    public static Set<EventInstance> getDeviceEventInstances(Context context, Calendar calendar) {
        HashSet<EventInstance> eventInstances = new HashSet<>();

        long now = System.currentTimeMillis();

        String selection = "calendar_id = " + calendar.getCalendarId();
        String path = "instances/when/" + now + "/" + (now + IConst.WEEK);
        String sortOrder = "begin DESC";
        Cursor cursor = getCalendarManagedCursor(context, EVENT_PROJECTION, selection, path, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                EventInstance startEventInstance = new EventInstance();
                EventInstance endEventInstance = new EventInstance();

                int allDay = cursor.getInt(PROJECTION_ALL_DAY_INDEX);
                if (allDay != IConst.ALL_DAY_EVENT) {

                    startEventInstance.setCalendar(calendar);
                    startEventInstance.setType(EventInstanceType.START);
                    startEventInstance.setStartTime(cursor.getLong(PROJECTION_BEGIN_INDEX));
                    startEventInstance.setEndTime(cursor.getLong(PROJECTION_END_INDEX));
                    startEventInstance.setTitle(cursor.getString(PROJECTION_TITLE_INDEX));
                    startEventInstance.setAvailability(cursor.getInt(PROJECTION_AVAILABILITY_INDEX));

                    endEventInstance.setCalendar(calendar);
                    endEventInstance.setType(EventInstanceType.END);
                    endEventInstance.setStartTime(cursor.getLong(PROJECTION_BEGIN_INDEX));
                    endEventInstance.setEndTime(cursor.getLong(PROJECTION_END_INDEX));
                    endEventInstance.setTitle(cursor.getString(PROJECTION_TITLE_INDEX));
                    endEventInstance.setAvailability(cursor.getInt(PROJECTION_AVAILABILITY_INDEX));

                    eventInstances.add(startEventInstance);
                    eventInstances.add(endEventInstance);
                    //Log.d(AppEventInstanceRepository.class.getSimpleName(), startEventInstance.toString());
                }
            }
            cursor.close();
        }
        return eventInstances;
    }

    private static Cursor getCalendarManagedCursor(Context context, String[] projection, String selection,
                                                   String path, String sort) {
        Cursor managedCursor = null;
        Uri calendars = Uri.parse("content://com.android.calendar/" + path);
        try {
            managedCursor = context.getContentResolver().query(calendars, projection, selection, null, sort);
        } catch (IllegalArgumentException e) {
            Log.w(AppEventInstanceRepository.class.getSimpleName(),
                    "Failed to get provider at [" + calendars.toString() + "]");
        }
        return managedCursor;
    }
}
