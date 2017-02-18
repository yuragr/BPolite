package com.bpolite.data.repo.device;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.bpolite.data.comparator.CalendarComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.pojo.Calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yurig on 12-Feb-17.
 */

public class DeviceCalendarRepository {
    // Projection array. Creating indexes for this array instead of doing dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[]{CalendarContract.Calendars._ID, // 0
            CalendarContract.Calendars.ACCOUNT_NAME, // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, // 2
            CalendarContract.Calendars.OWNER_ACCOUNT // 3
    };

    // The indexes for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static List<Calendar> getDeviceCalendars(Context context) {
        List<Calendar> result = new ArrayList<>();
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            cursor = cr.query(uri, CALENDAR_PROJECTION, null, null, null);
            if (cursor != null) {

                // Use the cursor to step through the returned records
                while (cursor.moveToNext()) {
                    Calendar calendar = new Calendar();

                    // TODO what about deleted calendars?
                    // http://developer.android.com/reference/android/provider/CalendarContract.SyncColumns.html#DELETED

                    // Get the field values
                    calendar.setCalendarId(cursor.getLong(PROJECTION_ID_INDEX));
                    calendar.setDisplayName(cursor.getString(PROJECTION_DISPLAY_NAME_INDEX));
                    calendar.setAccountName(cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX));
                    calendar.setOwnerName(cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX));
                    calendar.setStatus(CalendarStatus.NONE);

                    // Log.d(AppCalendarRepository.class.getSimpleName(), calendar.toString());
                    result.add(calendar);
                }
                cursor.close();
            }
            Collections.sort(result, new CalendarComparator());
        }
        return result;
    }

}
