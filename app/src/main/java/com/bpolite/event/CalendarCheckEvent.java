package com.bpolite.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bpolite.service.CalendarCheckService;

public class CalendarCheckEvent extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), "trying to check calendars...");
        Intent calendarCheck = new Intent(context, CalendarCheckService.class);
        context.startService(calendarCheck);
    }
}
