package org.torproject.snowflake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class AppSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "AppSettingsFragment";

    public static AppSettingsFragment newInstance() {
        AppSettingsFragment fragment = new AppSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_settings, rootKey);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: Key:" + key);
        if (key.contains("switch")) {
            //It's a switch
            boolean val = sharedPreferences.getBoolean(key, false);
            String edit_text = key.replace("switch", "edit_text");
            Preference editTextPreference = findPreference(edit_text);
            editTextPreference.setEnabled(val);

            if (val) {
                findPreference(key).setSummary(""); //Summary of switch is null when turned on.

                String previousValue = sharedPreferences.getString(edit_text, "");
                if (!previousValue.equals(""))
                    editTextPreference.setSummary(previousValue); //When Switch is turned on set the summary to previously set Value.
                else
                    editTextPreference.setSummary("Using Default"); //If there is no previous value, then using null.
            } else {
                findPreference(key).setSummary("Using Default"); //Default is shown when switch is off.
            }
        } else {
            //It's an Edit Text
            String editValue = sharedPreferences.getString(key, "");
            if (!editValue.equals(""))
                findPreference(key).setSummary(editValue); //Setting Edit text to edited value
            else
                findPreference(key).setSummary("Using Default"); //Setting Edit text to Default because user left it empty.
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}