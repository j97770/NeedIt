package com.miru.needit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			boolean boot = context.getSharedPreferences(
					context.getPackageName() + "_preferences", Context.MODE_PRIVATE)
					.getBoolean("autorun", true);
			if (boot) {
				run(context, intent);
			}
		}
	}
	
	public static void run(Context context, Intent intent) {
		String viewstyle = context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("viewstyle", "");

		if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
			context.startService(new Intent(context, FloatingViewService.class));
		} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
			context.startService(new Intent(context, NotificationService.class));
			//stopService(new Intent(FloatingViewService.this, FloatingViewService.class));
		}
	}
}