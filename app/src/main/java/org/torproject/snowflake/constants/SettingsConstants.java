package org.torproject.snowflake.constants;

import java.util.HashMap;
import java.util.Map;

public class SettingsConstants {
    //Switches
    public static final String STUN_SWITCH = "stun_switch";
    public static final String BROKER_SWITCH = "broker_switch";
    public static final String RELAY_SWITCH = "relay_switch";

    //Edit Texts
    public static final String STUN_ET = "stun_edit_text";
    public static final String BROKER_ET = "broker_edit_text";
    public static final String RELAY_ET = "relay_edit_text";

    public static final String DEFAULT = "Using Default";

    public static Map<String, String> getSettingMap() {
        return new HashMap<String, String>() {{
            put(STUN_SWITCH, STUN_ET);
            put(BROKER_SWITCH, BROKER_ET);
            put(RELAY_SWITCH, RELAY_ET);
        }};
    }
}
