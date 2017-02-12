package com.bpolite.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bpolite.service.CalendarCheckService;

public class CalendarChangedEvent extends BroadcastReceiver {
    // TODO understand why this is called sometimes when the calendar was not changed at all
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(this.getClass().getSimpleName(), "Calendar was changed...");
        Intent calendarCheck = new Intent(context, CalendarCheckService.class);
        context.startService(calendarCheck);
    }
}
