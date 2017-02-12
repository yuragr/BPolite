package com.bpolite.data.repo.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.bpolite.IConst;
import com.bpolite.data.enums.EventAvailability;
import com.bpolite.data.enums.EventInstanceType;
import com.bpolite.data.enums.RingerRestoreDelay;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppEventInstanceRepository {
    public static final Object readWriteLock = new Object();

    @SuppressLint("UseSparseArrays")
    public static HashSet<EventInstance> getEventInstancesFromXml(Context context) {
        Log.d(AppEventInstanceRepository.class.getSimpleName(), ">> getEventInstancesFromXml");

        HashSet<EventInstance> eventInstances = new HashSet<>();

        List<Calendar> calendars = AppCalendarRepository.getCalendars(context);
        HashMap<Integer, Calendar> calendarsMap = new HashMap<>();
        for (Calendar calendar : calendars) {
            calendarsMap.put(calendar.hashCode(), calendar);
        }

        try {
            File filesDir = context.getFilesDir();
            File eventInstancesFile = new File(filesDir, IConst.EVENT_INSTANCES_FILE);

            if (eventInstancesFile.exists()) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new FileInputStream(eventInstancesFile), "UTF-8");
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (xpp.getEventType() == XmlPullParser.START_TAG
                            && xpp.getName().equalsIgnoreCase("eventInstance")) {
                        xpp.next();
                        EventInstance eventInstance = parseEventInstance(xpp);
                        if (calendarsMap.containsKey(eventInstance.getCalendarHashCode())) {
                            eventInstance.setCalendar(calendarsMap.get(eventInstance.getCalendarHashCode()));
                            eventInstances.add(eventInstance);
                        }
                    }
                    if (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
                        xpp.next();
                }
            }
        } catch (IOException e) {
            Log.e(AppEventInstanceRepository.class.getSimpleName(), "problem getting calendars from file", e);
        } catch (XmlPullParserException e) {
            Log.e(AppEventInstanceRepository.class.getSimpleName(), "problem reading xml file", e);
        }

        Log.d(AppEventInstanceRepository.class.getSimpleName(), "<< getEventInstancesFromXml returns: " + eventInstances);

        return eventInstances;
    }

    private static EventInstance parseEventInstance(XmlPullParser xpp) throws XmlPullParserException,
            IOException {
        EventInstance eventInstance = new EventInstance();
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT
                && !(xpp.getEventType() == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase(
                "eventInstance"))) {

            if (xpp.getEventType() == XmlPullParser.START_TAG) {
                String property = xpp.getName();
                xpp.next();
                if (property.equalsIgnoreCase("calendarHashCode")) {
                    eventInstance.setCalendarHashCode(Integer.parseInt(xpp.getText()));
                } else if (property.equalsIgnoreCase("title")) {
                    eventInstance.setTitle(xpp.getText());
                } else if (property.equalsIgnoreCase("startTime")) {
                    eventInstance.setStartTime(Long.parseLong(xpp.getText()));
                } else if (property.equalsIgnoreCase("endTime")) {
                    eventInstance.setEndTime(Long.parseLong(xpp.getText()));
                } else if (property.equalsIgnoreCase("type")) {
                    eventInstance.setType(EventInstanceType.getAsEnum(xpp.getText()));
                } else if (property.equalsIgnoreCase("active")) {
                    eventInstance.setActive(Boolean.parseBoolean(xpp.getText()));
                } else if (property.equalsIgnoreCase("ringerRestoreDelay")) {
                    eventInstance.setRingerRestoreDelay(RingerRestoreDelay.getAsEnum(Integer.parseInt(xpp.getText())));
                } else if (property.equalsIgnoreCase("availability")) {
                    eventInstance.setAvailability(EventAvailability.getAsEnum(Integer.parseInt(xpp.getText())));
                }
            }
            if (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
                xpp.next();
        }
        return eventInstance;
    }

    public static void saveEventInstancesToXml(Context context, HashSet<EventInstance> eventInstances) {
        Log.d(AppEventInstanceRepository.class.getSimpleName(), ">> saveEventInstancesToXml");
        File eventInstancesFile;
        synchronized (readWriteLock) {
            try {
                File filesDir = context.getFilesDir();
                eventInstancesFile = new File(filesDir, IConst.EVENT_INSTANCES_FILE);

                if (eventInstancesFile.exists()) {
                    eventInstancesFile.delete();
                }
                eventInstancesFile.createNewFile();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlSerializer xs = factory.newSerializer();
                xs.setOutput(new FileOutputStream(eventInstancesFile), "UTF-8");
                xs.startDocument("UTF-8", true);

                for (EventInstance eventInstance : eventInstances) {
                    if (eventInstance.isActive()) {
                        xs.startTag("", "eventInstance");

                        xs.startTag("", "title");
                        xs.text("" + eventInstance.getTitle());
                        xs.endTag("", "title");

                        xs.startTag("", "calendarHashCode");
                        xs.text("" + eventInstance.getCalendar().hashCode());
                        xs.endTag("", "calendarHashCode");

                        xs.startTag("", "startTime");
                        xs.text("" + eventInstance.getStartTime());
                        xs.endTag("", "startTime");

                        xs.startTag("", "endTime");
                        xs.text("" + eventInstance.getEndTime());
                        xs.endTag("", "endTime");

                        xs.startTag("", "type");
                        xs.text("" + eventInstance.getType().getValue());
                        xs.endTag("", "type");

                        xs.startTag("", "ringerRestoreDelay");
                        xs.text("" + eventInstance.getRingerRestoreDelay().getValue());
                        xs.endTag("", "ringerRestoreDelay");

                        xs.startTag("", "active");
                        xs.text("" + Boolean.toString(eventInstance.isActive()));
                        xs.endTag("", "active");

                        xs.startTag("", "availability");
                        xs.text("" + eventInstance.getAvailability().getValue());
                        xs.endTag("", "availability");

                        xs.endTag("", "eventInstance");
                    }
                }
                xs.endDocument();

            } catch (IOException e) {
                Log.e(AppEventInstanceRepository.class.getSimpleName(),
                        "problem getting event instances from file", e);
            } catch (XmlPullParserException e) {
                Log.e(AppEventInstanceRepository.class.getSimpleName(), "problem reading xml file", e);
            }
        }
        Log.d(AppEventInstanceRepository.class.getSimpleName(), "<< saveEventInstancesToXml");
    }

    public static void deleteEventInstances(Context context, Set<EventInstance> eventInstancesToRemove) {
        synchronized (readWriteLock) {
            if (eventInstancesToRemove != null && !eventInstancesToRemove.isEmpty()) {
                HashSet<EventInstance> allEventInstances = getEventInstancesFromXml(context);
                allEventInstances.removeAll(eventInstancesToRemove);

                saveEventInstancesToXml(context, allEventInstances);
            }
        }
    }
}
