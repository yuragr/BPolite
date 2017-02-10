package com.bpolite.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bpolite.utils.NotificationUtils;

import static android.media.AudioManager.EXTRA_RINGER_MODE;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

public class MutedEvent extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// if the user had muted
		int newRingerMode = intent.getIntExtra(EXTRA_RINGER_MODE, -1);
		if (newRingerMode == RINGER_MODE_SILENT || newRingerMode == RINGER_MODE_VIBRATE) {
			return;
		} else {
			NotificationUtils.removeNotification(context);
		}
	}
}
