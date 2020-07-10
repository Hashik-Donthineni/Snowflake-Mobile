package org.torproject.snowflake;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import org.torproject.snowflake.constants.SettingsConstants;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class GlobalApplication extends Application {
    private final static String BROKER_URL = "http://10.0.2.2:8080"; //10.0.2.2 is used to access computer's local host from Android Emulator.
    private final static String WEBSOCKET_URL = "wss://snowflake.freehaven.net:443";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences appPreferences;

    public static String getBrokerUrl() {
        //Checking to see if the switch is turned on.
        if (sharedPreferences.getBoolean(SettingsConstants.BROKER_SWITCH, false))
            //Send the custom Broker URL, if the key is not found send default.
            return sharedPreferences.getString(SettingsConstants.BROKER_ET, BROKER_URL);
        return BROKER_URL;
    }

    public static String getWebSocketUrl() {
        //Checking to see if the switch is turned on.
        if (sharedPreferences.getBoolean(SettingsConstants.RELAY_SWITCH, false))
            //Send the custom Socket URL, if the key is not found send default.
            return sharedPreferences.getString(SettingsConstants.RELAY_ET, WEBSOCKET_URL);
        return WEBSOCKET_URL;
    }

    public static String getSTUN() {
        //Checking to see if the switch is turned on.
        if (sharedPreferences.getBoolean(SettingsConstants.STUN_SWITCH, false))
            return sharedPreferences.getString(SettingsConstants.STUN_ET, null); //Send the custom STUN if it's turned on.
        return null;
    }

    public static Map<String, String> getHeadersMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Content-type", "application/json");
        //http or https shouldn't be part of header or els broker will throw error.
        map.put("Host", getBrokerUrl().replace("http://", ""));
        return map;
    }

    public static SharedPreferences getAppPreferences() {
        return appPreferences;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        sharedPreferences = getDefaultSharedPreferences(this);
        appPreferences = getSharedPreferences(getString(R.string.sharedpreference_file), MODE_PRIVATE);
    }
}
