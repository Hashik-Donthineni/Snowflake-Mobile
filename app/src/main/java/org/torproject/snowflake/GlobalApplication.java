package org.torproject.snowflake;

import android.app.Application;

public class GlobalApplication extends Application {
    private final static String BROKER_URL = "http://10.0.2.2:8080"; //10.0.2.2 is used to access computer's local host from Android Emulator.

    public static String getBrokerUrl() {
        return BROKER_URL;
    }
}
