package com.miru.needit;

import com.miru.needit.AdvancedActivity;
import com.miru.needit.DialogActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

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
public class SearchSettingsActivity extends PreferenceActivity implements
		OnPreferenceClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_searchsettings);

		Preference googleimage = findPreference("googleimage");

		googleimage.setOnPreferenceClickListener(this);

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals("googleimage")) {
			
			String viewstyle = getSharedPreferences(
					getPackageName() + "_preferences", Context.MODE_PRIVATE)
					.getString("viewstyle", "VIEWSTYLE_FLOATING");

			if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
				startService(new Intent(SearchSettingsActivity.this, FloatingViewService.class));
			} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
			}

			Intent dialog = new Intent(this, DialogActivity.class);
			startActivity(dialog);

			Handler mhandler = new Handler();
			mhandler.postDelayed(new Runnable() {
				public void run() {
					Intent i = new Intent(SearchSettingsActivity.this,
							AdvancedActivity.class);
					startActivity(i);
				}
			}, 200);
		}
		return false;
	}
}
