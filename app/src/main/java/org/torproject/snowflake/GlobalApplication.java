package org.torproject.snowflake;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public class GlobalApplication extends Application {
    private final static String BROKER_URL = "http://10.0.2.2:8080"; //10.0.2.2 is used to access computer's local host from Android Emulator.
    private final static String WEBSOCKET_URL = "wss://snowflake.freehaven.net:443";

    public static String getBrokerUrl() {
        return BROKER_URL;
    }

    public static String getWebSocketUrl() {
        return WEBSOCKET_URL;
    }

    public static Map<String, String> getHeadersMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Content-type", "application/json");
        //http or https shouldn't be part of header or els broker will throw error.
        map.put("Host", getBrokerUrl().replace("http://", ""));
        return map;
    }
}
