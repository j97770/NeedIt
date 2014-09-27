package com.miru.needit;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	
	ListPreference ViewStyle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);

		Preference tabsettings = findPreference("tabsettings");
		Preference mapsettings = findPreference("mapsettings");
		Preference searchsettings = findPreference("searchsettings");
		
		ViewStyle = (ListPreference) getPreferenceManager().findPreference(
				"viewstyle");
		
		ViewStyle.setOnPreferenceChangeListener(this);
		
		//FloatingViewService.hideView();
		
		CheckBoxPreference boot = (CheckBoxPreference)getPreferenceManager().findPreference("autorun");  

		tabsettings.setOnPreferenceClickListener(this);
		mapsettings.setOnPreferenceClickListener(this);
		searchsettings.setOnPreferenceClickListener(this);

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals("tabsettings")) {
			Intent go = new Intent(SettingsActivity.this, TabSettingsActivity.class);
			startActivity(go);
		}
		if (preference.getKey().equals("mapsettings")) {
			Intent go = new Intent(SettingsActivity.this, MapSettingsActivity.class);
			startActivity(go);
		}
		if (preference.getKey().equals("searchsettings")) {
			Intent go = new Intent(SettingsActivity.this, SearchSettingsActivity.class);
			startActivity(go);
		}
		return false;
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == ViewStyle) {
			if (((CharSequence)newValue).equals("VIEWSTYLE_NOTIFICATION")) {
				Toast toast = Toast.makeText(this, R.string.toast_viewstyle,
						Toast.LENGTH_SHORT);
				toast.show();
			} else if (((CharSequence)newValue).equals("VIEWSTYLE_FLOATING")) {
				Toast toast = Toast.makeText(this, R.string.toast_viewstyle,
						Toast.LENGTH_SHORT);
				toast.show();
			}
			return true;
		}

		return false;
	}
	
	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch(item.getItemId()){
			case R.id.action_restart:
				String viewstyle = getSharedPreferences(
						getPackageName() + "_preferences", Context.MODE_PRIVATE)
						.getString("viewstyle", "");

				if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
					stopService(new Intent(SettingsActivity.this, NotificationService.class));
					NotificationService.notificationManager.cancel(NotificationService.MY_NOTIFICATION_ID);
				}
				System.exit(1);
				Intent reLaunchMain=new Intent(this,MainActivity.class);
				startActivity(reLaunchMain);
				break;
			}
			return super.onOptionsItemSelected(item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
}
