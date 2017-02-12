package com.bpolite.activity;

/*
 * We have to find what is the corporate calendar. For this we have to find the only calendar whose
 * accountName property doesn't ends with "...@gmail.com". This should be the corporate's account calendar. If
 * we don't have a corporate account, then we have to make the main google account's calendar as the one that
 * we mute. In order to identify the main google calendar - it should be the one with the accountName =
 * ownerName = "googleAccountName"
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bpolite.R;
import com.bpolite.data.comparator.CalendarComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.repo.app.AppCalendarRepository;
import com.bpolite.utils.CalendarCheckSchedulerUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private ListView mListView;
    private List<Calendar> calendars = new ArrayList<>();
    private static boolean started = false;

    private static final int MY_PERMISSIONS_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        readCalendars();

        if (calendars == null || calendars.isEmpty()) {
            setContentView(R.layout.activity_main_no_calendars);
            CalendarCheckSchedulerUtils.scheduleCalendarCheck(MainActivity.this);
        } else {
            initView();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.calendarList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createUI();
                CalendarCheckSchedulerUtils.scheduleCalendarCheck(MainActivity.this);
            }
        });
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR,
                                Manifest.permission.VIBRATE,
                                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                Manifest.permission.WAKE_LOCK},
                        MY_PERMISSIONS_REQUEST);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (Manifest.permission.READ_CALENDAR.equals(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            initView();
                        }
                    }
                }
            }
        }
    }

    private synchronized List<Map<String, Serializable>> getCalendars() {
        List<Map<String, Serializable>> calendarMaps = new ArrayList<>();
        readCalendars();

        Collections.sort(calendars, new CalendarComparator());

        for (Calendar calendar : calendars) {
            calendarMaps.add(calendar.getAsMap());
        }

        return calendarMaps;
    }

    private synchronized void readCalendars() {
        if (!started) {
            calendars = AppCalendarRepository.getCalendars(MainActivity.this);
            started = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        started = false;
    }

    @Override
    protected void onRestart() {
        started = false;
        super.onRestart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createUI();
            }
        });
    }

    private void createUI() {
        List<Map<String, Serializable>> data = getCalendars();
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.calendar_entry,
                new String[]{"calendarIcon", "displayName", "accountName", "status"}, new int[]{
                R.drawable.ic_launcher, R.id.calendarName, R.id.accountName, R.id.calendarUsage}) {
            @Override
            public void setViewText(TextView textView, String text) {
                super.setViewText(textView, text);
                if (text.startsWith(CalendarStatus.VIBRATE.getValue())
                        || text.startsWith(CalendarStatus.MUTED.getValue())) {
                    textView.setTextColor(0xFF669900);
                    textView.setTypeface(null, Typeface.BOLD);

                } else {
                    textView.setTextColor(Color.BLACK);
                    textView.setTypeface(null, Typeface.NORMAL);
                }
            }
        };

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //						handleCalendarClick(view, id);
                Intent intent = new Intent(getApplicationContext(), CalendarSettingsActivity.class);
                intent.putExtra("calendarHashCode", calendars.get((int) id).hashCode());
                startActivity(intent);

            }
        });
    }
}
