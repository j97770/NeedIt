package com.miru.needit;

import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout main = (LinearLayout) findViewById(R.id.LinearLayout1);

		Calendar now = Calendar.getInstance();
		int a = now.get(Calendar.AM_PM);
		if (a == Calendar.AM) {
			main.setBackgroundResource(R.drawable.daylight);
		} else if (a == Calendar.PM) {
			main.setBackgroundResource(R.drawable.daydark);
		}
		
		Button launch = (Button) findViewById(R.id.btn_float);
		launch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String viewstyle = getSharedPreferences(
						getPackageName() + "_preferences", Context.MODE_PRIVATE)
						.getString("viewstyle", "VIEWSTYLE_FLOATING");

				if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
					startService(new Intent(MainActivity.this, FloatingViewService.class));
				} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
					startService(new Intent(MainActivity.this, NotificationService.class));
					//stopService(new Intent(FloatingViewService.this, FloatingViewService.class));
				}
			}
		});

		Button tutorial = (Button) findViewById(R.id.btn_tutorial);
		tutorial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent tutorial = (new Intent(MainActivity.this,
						TutorialActivity.class));
				startActivity(tutorial);
			}
		});
		
		Button settings = (Button) findViewById(R.id.btn_settings);
		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settings = (new Intent(MainActivity.this,
						SettingsActivity.class));
				startActivity(settings);
			}
		});

	}
}

