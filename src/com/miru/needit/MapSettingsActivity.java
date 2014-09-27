package com.miru.needit;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

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
public class MapSettingsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	ListPreference mapviewmode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_mapsettings);

		CheckBoxPreference showcountry = (CheckBoxPreference) getPreferenceManager().findPreference(
				"showcountry");
		
		mapviewmode = (ListPreference) getPreferenceManager().findPreference(
				"mapviewmode");
		mapviewmode.setOnPreferenceChangeListener(this);
		updateSummary();
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mapviewmode) {
			if (((CharSequence)newValue).equals("VIEW_MODE_VECTOR")) {
				mapviewmode.setSummary(R.string.mapview_vector);
			} else {
				mapviewmode.setSummary(R.string.mapview_hybrid);
			}
			return true;
		}

		return false;
	}

	public void updateSummary() {
		if (getSharedPreferences(getPackageName() + "_preferences",
				Context.MODE_PRIVATE).getString("mapviewmode",
				"VIEW_MODE_VECTOR").equals("VIEW_MODE_VECTOR")) {
			mapviewmode.setSummary(R.string.mapview_vector);
		} else {
			mapviewmode.setSummary(R.string.mapview_hybrid);
		}
	}
}
