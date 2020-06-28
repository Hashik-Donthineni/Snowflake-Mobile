package org.torproject.snowflake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.torproject.snowflake.constants.ForegroundServiceConstants;
import org.torproject.snowflake.interfaces.MainFragmentCallback;

/**
 * MainActivity is the main UI of the application.
 */
public class MainActivity extends AppCompatActivity implements MainFragmentCallback {
    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        settingsButton = findViewById(R.id.settings_button);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreference_file), MODE_PRIVATE);

        //Creating notification channel if app is being run for the first time
        if (sharedPreferences.getBoolean(getString(R.string.initial_run_boolean), true)) {
            createNotificationChannel();
            //Setting initial run to false.
            sharedPreferences.edit().putBoolean(getString(R.string.initial_run_boolean), false).apply();
        }

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting Settings Activity.
                startFragment(AppSettingsFragment.newInstance());
            }
        });

        //Starting the MainFragment.
        startFragment(MainFragment.newInstance());
    }

    /**
     * Used to  replace the fragment in the "fragment_container"
     *
     * @param fragment New Fragment that is to be placed in the container.
     */
    private void startFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fragment).commit();
    }

    /**
     * Turn service on/off.
     *
     * @param action An Action from ForegroundServiceConstants.
     */
    public void serviceToggle(String action) {
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
    public boolean isServiceRunning() {
        return sharedPreferences.getBoolean(getString(R.string.is_service_running_bool), false);
    }

    /**
     * Used to create a new notification channel if app is started for the first time on a device.
     */
    private void createNotificationChannel() {
        //Versions after Android Oreo mandates the use of notification channels.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(ForegroundServiceConstants.NOTIFICATION_CHANNEL_ID,
                    getString(R.string.not_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.not_channel_desc));
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
