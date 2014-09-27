package com.miru.needit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class NotificationService extends Service {

	static NotificationManager notificationManager;
	Notification myNotification;
	static final int MY_NOTIFICATION_ID = 1;

	@Override
	public void onCreate() {
		super.onCreate();
		
		String viewstyle = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("viewstyle", "VIEWSTYLE_FLOATING");

		if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
			startNotify();
			notificationManager.cancel(MY_NOTIFICATION_ID);
		} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
			startNotify();
		}
	}
	
	public void startNotify() {
		int icon = R.drawable.ic_notification_small;
        long when = System.currentTimeMillis();
        myNotification = new Notification(icon, getString(R.string.app_name), when);
 
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
         
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);
        //contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
        //contentView.setTextViewText(R.id.title, "Custom notification");
        //contentView.setTextViewText(R.id.text, "This is a custom layout");
        myNotification.contentView = contentView;
         
        Intent notificationIntent = new Intent(this, DialogActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        myNotification.contentIntent = contentIntent;
   
        myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
         
        notificationManager.notify(1, myNotification);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		notificationManager.cancel(MY_NOTIFICATION_ID);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
