package com.bpolite.data.repo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.comparator.CalendarComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.enums.RingerRestoreDelay;
import com.bpolite.data.enums.WeekDay;
import com.bpolite.data.pojo.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class CalendarRepository {
    public static final Object readWriteLock = new Object();

    // Projection array. Creating indexes for this array instead of doing dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[]{Calendars._ID, // 0
            Calendars.ACCOUNT_NAME, // 1
            Calendars.CALENDAR_DISPLAY_NAME, // 2
            Calendars.OWNER_ACCOUNT // 3
    };

    // The indexes for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static CalendarStatus getCalendarStatus(Context context, Calendar calendar) {

        SharedPreferences calendarPreferences = context.getSharedPreferences(IConst.CALENDARS_FILE,
                Context.MODE_PRIVATE);

        String statusStr = calendarPreferences.getString(calendar.getRepositoryKey(),
                CalendarStatus.NONE.getValue());

        return CalendarStatus.getAsEnum(statusStr);
    }

    public static void saveCalendar(Context context, Calendar calendar) {
        synchronized (readWriteLock) {
            ArrayList<Calendar> calendars = getCalendars(context);
            int replaceIndex = calendars.indexOf(calendar);
            calendars.set(replaceIndex, calendar);
            saveCalendarsToXml(context, calendars);
        }

        // FIXME if the status was changed to NONE, then we have to clean all the notifications
    }

    /**
     * Gets the updated calendar list. Checks both the saved calendar file and the device
     *
     * @param context
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static ArrayList<Calendar> getCalendars(Context context) {
        ArrayList<Calendar> calendars = new ArrayList<>();

        synchronized (readWriteLock) {
            try {
                HashSet<Calendar> deviceCalendars = new HashSet<>();
                deviceCalendars.addAll(getDeviceCalendars(context));

                if (!deviceCalendars.isEmpty()) {
                    ArrayList<Calendar> fileCalendarList = loadCalendarsFromXml(context);

                    // there are calendars in the device. we have to load and compare the saved calendars
                    if (fileCalendarList != null && !fileCalendarList.isEmpty()) {
                        HashSet<Calendar> fileCalendars = new HashSet<>();
                        fileCalendars.addAll(fileCalendarList);

                        if (fileCalendars.equals(deviceCalendars)) {

                            // no changes to the calendars themselves
                            calendars = fileCalendarList;
                        }

                        // what to do if there were changes to the calendars themselves
                        else {

                            // add all existing calendars (that also exist in the device)
                            for (Calendar fileCalendar : fileCalendars) {
                                if (deviceCalendars.contains(fileCalendar)) {
                                    calendars.add(fileCalendar);
                                }
                            }

                            // add new calendars
                            for (Calendar deviceCalendar : deviceCalendars) {
                                if (!fileCalendars.contains(deviceCalendar)) {
                                    calendars.add(deviceCalendar);
                                }
                            }

                            // save the calendar changes to file
                            saveCalendarsToXml(context, calendars);
                        }
                    } else {

                        // probably running for the first time since there are no saved calendars save device's calendars to file
                        calendars.clear();
                        calendars.addAll(deviceCalendars);
                        saveCalendarsToXml(context, calendars);
                    }
                }
            } catch (Exception e) {
                Log.e(CalendarRepository.class.getSimpleName(), "problem loading calendar file", e);
            }
        }
        return calendars;
    }

    private static File saveCalendarsToXml(Context context, ArrayList<Calendar> calendars) {
        File calendarsFile = null;
        synchronized (readWriteLock) {
            try {
                File filesDir = context.getFilesDir();
                calendarsFile = new File(filesDir, IConst.CALENDARS_FILE);

                if (calendarsFile.exists()) {
                    calendarsFile.delete();
                }
                calendarsFile.createNewFile();

                if (calendars != null)
                    Collections.sort(calendars, new CalendarComparator());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlSerializer xs = factory.newSerializer();
                xs.setOutput(new FileOutputStream(calendarsFile), "UTF-8");
                xs.startDocument("UTF-8", true);

                for (Calendar calendar : calendars) {
                    xs.startTag("", "calendar");

                    xs.startTag("", "calendarId");
                    xs.text("" + calendar.getCalendarId());
                    xs.endTag("", "calendarId");

                    xs.startTag("", "displayName");
                    xs.text("" + calendar.getDisplayName());
                    xs.endTag("", "displayName");

                    xs.startTag("", "accountName");
                    xs.text("" + calendar.getAccountName());
                    xs.endTag("", "accountName");

                    xs.startTag("", "ownerName");
                    xs.text("" + calendar.getOwnerName());
                    xs.endTag("", "ownerName");

                    xs.startTag("", "status");
                    xs.text("" + calendar.getStatus().getValue());
                    xs.endTag("", "status");

                    xs.startTag("", "weekDays");
                    ArrayList<WeekDay> weekDays = calendar.getWeekDays();
                    xs.text("" + StringUtils.join(weekDays, IConst.COMMA));
                    xs.endTag("", "weekDays");

                    xs.startTag("", "ringerRestoreDelay");
                    xs.text("" + calendar.getRingerRestoreDelay().getValue());
                    xs.endTag("", "ringerRestoreDelay");

                    xs.endTag("", "calendar");
                }
                xs.endDocument();
            } catch (IOException e) {
                Log.e(CalendarRepository.class.getSimpleName(), "problem getting calendars from file", e);
            } catch (XmlPullParserException e) {
                Log.e(CalendarRepository.class.getSimpleName(), "problem reading xml file", e);
            }
        }
        return calendarsFile;
    }

    private static ArrayList<Calendar> loadCalendarsFromXml(Context context) {
        ArrayList<Calendar> calendars = new ArrayList<>();

        try {
            File filesDir = context.getFilesDir();
            File calendarsFile = new File(filesDir, IConst.CALENDARS_FILE);

            if (calendarsFile.exists()) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new FileInputStream(calendarsFile), "UTF-8");
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (xpp.getEventType() == XmlPullParser.START_TAG
                            && xpp.getName().equalsIgnoreCase("calendar")) {
                        xpp.next();
                        calendars.add(parseCalendar(xpp));
                    }
                    if (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
                        xpp.next();
                }
            }
        } catch (IOException e) {
            Log.e(CalendarRepository.class.getSimpleName(), "problem getting calendars from file", e);
        } catch (XmlPullParserException e) {
            Log.e(CalendarRepository.class.getSimpleName(), "problem reading xml file", e);
        }

        return calendars;
    }

    private static Calendar parseCalendar(XmlPullParser xpp) throws XmlPullParserException, IOException {
        Calendar calendar = new Calendar();
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT
                && !(xpp.getEventType() == XmlPullParser.END_TAG && xpp.getName()
                .equalsIgnoreCase("calendar"))) {

            if (xpp.getEventType() == XmlPullParser.START_TAG) {
                String property = xpp.getName();
                xpp.next();
                if (property.equalsIgnoreCase("calendarId")) {
                    calendar.setCalendarId(Long.parseLong(xpp.getText()));
                } else if (property.equalsIgnoreCase("displayName")) {
                    calendar.setDisplayName(xpp.getText());
                } else if (property.equalsIgnoreCase("accountName")) {
                    calendar.setAccountName(xpp.getText());
                } else if (property.equalsIgnoreCase("ownerName")) {
                    calendar.setOwnerName(xpp.getText());
                } else if (property.equalsIgnoreCase("status")) {
                    calendar.setStatus(CalendarStatus.getAsEnum(xpp.getText()));
                } else if (property.equalsIgnoreCase("weekDays")) {
                    calendar.setWeekDays(WeekDay.getAsEnumList(xpp.getText()));
                } else if (property.equalsIgnoreCase("ringerRestoreDelay")) {
                    calendar.setRingerRestoreDelay(RingerRestoreDelay.getByValue(Integer.parseInt(xpp
                            .getText())));
                }

            }
            if (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
                xpp.next();
        }
        return calendar;
    }

    private static ArrayList<Calendar> getDeviceCalendars(Context context) {
        ArrayList<Calendar> result = new ArrayList<>();
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();
        Uri uri = Calendars.CONTENT_URI;

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

                    // Log.d(CalendarRepository.class.getSimpleName(), calendar.toString());
                    result.add(calendar);
                }
                cursor.close();
            }
            Collections.sort(result, new CalendarComparator());
        }
        return result;
    }

    public static Calendar getCalendarByHashCode(Context context, int calendarHashCode) {
        ArrayList<Calendar> calendars = getCalendars(context);
        for (Calendar calendar : calendars) {
            if (calendar.hashCode() == calendarHashCode)
                return calendar;
        }
        return null;
    }
}
