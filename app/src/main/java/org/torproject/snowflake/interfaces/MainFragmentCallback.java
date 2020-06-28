package org.torproject.snowflake.interfaces;

public interface MainFragmentCallback {
    boolean isServiceRunning();
    void serviceToggle(String action);
}
