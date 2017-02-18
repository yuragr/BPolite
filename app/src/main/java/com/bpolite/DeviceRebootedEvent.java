package com.bpolite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bpolite.utils.CalendarCheckSchedulerUtils;

public class DeviceRebootedEvent extends BroadcastReceiver {
    @Override
    /**
     * When device was rebooted, start the repeating calendar checks
     */
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), "device was rebooted");
        CalendarCheckSchedulerUtils.scheduleCalendarCheck(context);
    }
}
