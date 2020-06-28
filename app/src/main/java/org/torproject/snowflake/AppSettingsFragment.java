package org.torproject.snowflake;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class AppSettingsFragment extends PreferenceFragmentCompat {

    public static AppSettingsFragment newInstance() {
        AppSettingsFragment fragment = new AppSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_settings, rootKey);
    }
}
