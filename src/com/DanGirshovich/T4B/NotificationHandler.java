package com.DanGirshovich.T4B;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class NotificationHandler extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		if (prefs.getBoolean("shortcut", true)) {
			int id;
			Intent notificationIntent;
			if (prefs.getString("mode", "Touch").equals("Tilt")) {
				id = 0;
				notificationIntent = new Intent().setAction(Const.START_TILT);
			} else {
				id = 1;
				notificationIntent = new Intent().setAction(Const.START_TOUCH);
			}

			boolean isVisible = PreferenceManager.getDefaultSharedPreferences(
					context).getBoolean("iconVisible", true);
			boolean isInverted = PreferenceManager.getDefaultSharedPreferences(
					context).getBoolean("iconInverted", false);
			int icon = isVisible ? (!isInverted ? R.drawable.sb_icon
					: R.drawable.sb_icon_inverted) : R.drawable.invisible;
			
			final Notification notification = new Notification();
			
			notification.tickerText = (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) ? "InvisiBright Active" : null;
			notification.icon = icon;
			notification.when = (Integer.parseInt(Build.VERSION.SDK) < 9) ? Long.MAX_VALUE : -Long.MIN_VALUE;
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_NO_CLEAR;
			int titleStr = (id==0) ? R.string.tilt_title : R.string.touch_title;
			int summaryStr = (id==0) ? R.string.tilt_summary : R.string.touch_summary;
			notification.setLatestEventInfo(context, context.getString(titleStr) , context.getString(summaryStr), contentIntent);
			mNotificationManager.notify(id, notification);
		}
	}
}
