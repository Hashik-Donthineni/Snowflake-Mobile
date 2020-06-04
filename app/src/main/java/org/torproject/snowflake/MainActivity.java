package org.torproject.snowflake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.torproject.snowflake.constants.ForegroundServiceConstants;

/**
 * MainActivity is the main UI of the application.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreference_file), MODE_PRIVATE);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            if (isServiceRunning()) //Toggling the service.
                serviceToggle(ForegroundServiceConstants.ACTION_STOP);
            else
                serviceToggle(ForegroundServiceConstants.ACTION_START);
        });
        if (BuildConfig.DEBUG)
            startButton.performClick(); //To perform an automatic click in testing environment.
    }

    /**
     * Turn service on/off.
     *
     * @param action An Action from ForegroundServiceConstants.
     */
    private void serviceToggle(String action) {
        Intent serviceIntent = new Intent(MainActivity.this, MyPersistentService.class);
        serviceIntent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    /**
     * Test to see if the MyPersistentService is running or not.
     *
     * @return boolean whether the service is running or not.
     */
    private boolean isServiceRunning() {
        return sharedPreferences.getBoolean(getString(R.string.is_service_running_bool), false);
    }
}
