package com.bpolite.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bpolite.IConst;
import com.bpolite.R;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.service.EventInstanceService;

import java.util.Date;

public class NotificationUtils {
	private static final int NOTIFICATION_ID = 1;

	public static void removeNotification(Context context) {
		getNotificationManager(context).cancel(NOTIFICATION_ID);
	}

	private static NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static void showNotification(Context context, long startTime, long endTime, long delayTime,
			CalendarStatus status) {

		removeNotification(context);

		Intent intent = new Intent(context, EventInstanceService.class);
		intent.setAction("restoreRinger");
		intent.putExtra("userAction", true);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

		//String start = IConst.SHORT_DATE_FORMAT.format(new Date(startTime));
		String end = IConst.SHORT_DATE_FORMAT.format(new Date(endTime + delayTime));

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker(status.getValue() + " until " + end);
		builder.setContentTitle(status.getValue() + " until " + end);
		builder.setContentIntent(pendingIntent);
		builder.setContentText("Touch here to restore sound");
		builder.setSmallIcon(R.drawable.volume_muted);

		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;

		getNotificationManager(context).notify(NOTIFICATION_ID, notification);
	}
}
