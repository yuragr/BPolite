package com.bpolite.data.repo.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.comparator.CalendarComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.RingerDelay;
import com.bpolite.data.enums.WeekDay;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.repo.device.DeviceCalendarRepository;

import org.apache.commons.lang3.CharEncoding;
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
import java.util.List;
import java.util.Set;

public class AppCalendarRepository {
    private static final Object sync = new Object();

    public static void saveCalendar(Context context, Calendar calendar) {
        synchronized (sync) {
            List<Calendar> calendars = getCalendars(context);
            int replaceIndex = calendars.indexOf(calendar);
            calendars.set(replaceIndex, calendar);
            saveCalendarsToXml(getCalendarsXmlFile(context), calendars);
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
    public static List<Calendar> getCalendars(Context context) {
        List<Calendar> calendars = new ArrayList<>();

        synchronized (sync) {
            try {
                Set<Calendar> deviceCalendars = new HashSet<>();
                deviceCalendars.addAll(DeviceCalendarRepository.getDeviceCalendars(context));

                if (!deviceCalendars.isEmpty()) {
                    List<Calendar> fileCalendarList = loadCalendarsFromXml(getCalendarsXmlFile(context));

                    // there are calendars in the device. we have to load and compare the saved calendars
                    if (fileCalendarList != null && !fileCalendarList.isEmpty()) {
                        Set<Calendar> fileCalendars = new HashSet<>();
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
                            saveCalendarsToXml(getCalendarsXmlFile(context), calendars);
                        }
                    } else {

                        // probably running for the first time since there are no saved calendars save device's calendars to file
                        calendars.clear();
                        calendars.addAll(deviceCalendars);
                        saveCalendarsToXml(getCalendarsXmlFile(context), calendars);
                    }
                }
            } catch (Exception e) {
                Log.e(AppCalendarRepository.class.getSimpleName(), "problem loading calendar file", e);
            }
        }
        return calendars;
    }

    protected static File saveCalendarsToXml(File calendarsXmlFile, List<Calendar> calendars) {
        synchronized (sync) {
            try {
                if (calendarsXmlFile.exists()) {
                    calendarsXmlFile.delete();
                }
                calendarsXmlFile.createNewFile();

                if (calendars != null)
                    Collections.sort(calendars, new CalendarComparator());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlSerializer xs = factory.newSerializer();
                xs.setOutput(new FileOutputStream(calendarsXmlFile), CharEncoding.UTF_8);
                xs.startDocument(CharEncoding.UTF_8, true);

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
                    List<WeekDay> weekDays = calendar.getWeekDays();
                    xs.text("" + StringUtils.join(weekDays, IConst.COMMA));
                    xs.endTag("", "weekDays");

                    xs.startTag("", "eventAvailabilities");
                    List<EventAvailability> eventAvailabilities = calendar.getEventAvailabilities();
                    xs.text("" + StringUtils.join(eventAvailabilities, IConst.COMMA));
                    xs.endTag("", "eventAvailabilities");

                    xs.startTag("", "ringerRestoreDelay");
                    xs.text("" + calendar.getRingerRestoreDelay().getValue());
                    xs.endTag("", "ringerRestoreDelay");

                    xs.endTag("", "calendar");
                }
                xs.endDocument();
            } catch (IOException e) {
                Log.e(AppCalendarRepository.class.getSimpleName(), "problem getting calendars from file", e);
            } catch (XmlPullParserException e) {
                Log.e(AppCalendarRepository.class.getSimpleName(), "problem reading xml file", e);
            }
        }
        return calendarsXmlFile;
    }

    protected static List<Calendar> loadCalendarsFromXml(File calendarsXmlFile) {
        List<Calendar> calendars = new ArrayList<>();

        try {
            if (calendarsXmlFile.exists()) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new FileInputStream(calendarsXmlFile), CharEncoding.UTF_8);
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
            Log.e(AppCalendarRepository.class.getSimpleName(), "problem getting calendars from file", e);
        } catch (XmlPullParserException e) {
            Log.e(AppCalendarRepository.class.getSimpleName(), "problem reading xml file", e);
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
                } else if (property.equalsIgnoreCase("eventAvailabilities")) {
                    calendar.setEventAvailabilities(EventAvailability.getAsEnumList(xpp.getText()));
                } else if (property.equalsIgnoreCase("ringerRestoreDelay")) {
                    calendar.setRingerRestoreDelay(RingerDelay.getByValue(Integer.parseInt(xpp
                            .getText())));
                }
            }
            if (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
                xpp.next();
        }
        return calendar;
    }

    public static Calendar getCalendarByHashCode(Context context, int calendarHashCode) {
        List<Calendar> calendars = getCalendars(context);
        for (Calendar calendar : calendars) {
            if (calendar.hashCode() == calendarHashCode)
                return calendar;
        }
        return null;
    }

    @NonNull
    private static File getCalendarsXmlFile(Context context) {
        File filesDir = context.getFilesDir();
        return new File(filesDir, IConst.CALENDARS_FILE);
    }
}
