package com.example.android.onestop.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    // key value corresponding to value defined in pref_general.xml
    public static String PREF_KEY_FACEBOOK = "fbPref";
    public static String PREF_KEY_GOOGLECALENDAR = "gmailPref";
    public static String PREF_KEY_SEARCH_RADIUS = "pref_search_radius";
    public static String PREF_KEY_SEARCH_MAX = "pref_search_result_max";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));

        EditTextPreference prefSearchRadius = (EditTextPreference)findPreference(PREF_KEY_SEARCH_RADIUS);
        EditTextPreference prefSearchResultMax = (EditTextPreference)findPreference(PREF_KEY_SEARCH_MAX);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        prefSearchRadius.setOnPreferenceChangeListener(this);
        prefSearchRadius.setSummary(settings.getString(PREF_KEY_SEARCH_RADIUS, Integer.toString(R.string.search_radius)));
        prefSearchResultMax.setOnPreferenceChangeListener(this);
        prefSearchResultMax.setSummary(settings.getString(PREF_KEY_SEARCH_MAX, Integer.toString(R.string.search_result_max)));

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        Log.d("999", "onPreferenceChange: " + preference.getKey());
        // Search Radius Change
        if (preference.getKey().equals(PREF_KEY_SEARCH_RADIUS)) {
            // check whether is valid integer
            // Range of radius: 0 - 20000

            int newValue = -1;
            try {
                newValue = Integer.parseInt((String)value);
            } catch (NumberFormatException e) {
                Log.d("999", Integer.toString(newValue));
                if (newValue < 0 || newValue > 20000) {
                    // invalid input, set back to default
                    preference.setDefaultValue(getString(R.string.search_radius));
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .edit().putString(PREF_KEY_SEARCH_RADIUS, getString(R.string.search_radius)).apply();
                    preference.setSummary(R.string.search_radius);
                }
            }


        }

        // Search Result Change
        if (preference.getKey().equals(PREF_KEY_SEARCH_MAX)) {
            // Range of radius: 0 - 20
            int newValue = -1;
            try {
                newValue = Integer.parseInt((String)value);
            } catch (NumberFormatException e) {
                if (newValue < 0 || newValue > 20) {
                    // invalid input, set back to default
                    preference.setDefaultValue(getString(R.string.search_result_max));
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .edit().putString(PREF_KEY_SEARCH_MAX, getString(R.string.search_result_max)).apply();
                    preference.setSummary(R.string.search_result_max);
                }
            }
        }

        return true;
    }


}