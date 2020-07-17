package org.torproject.snowflake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.torproject.snowflake.constants.ForegroundServiceConstants;
import org.torproject.snowflake.constants.FragmentConstants;
import org.torproject.snowflake.fragments.AppSettingsFragment;
import org.torproject.snowflake.fragments.MainFragment;
import org.torproject.snowflake.interfaces.MainFragmentCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * MainActivity is the main UI of the application.
 */
public class MainActivity extends AppCompatActivity implements MainFragmentCallback {
    private static final String TAG = "MainActivity";
    public int servedCount;
    int currentFragment;
    private SharedPreferences sharedPreferences;
    private Button settingsButton;
    private Disposable disposable;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        settingsButton = findViewById(R.id.settings_button);
        sharedPreferences = GlobalApplication.getAppPreferences();
        servedCount = 0;

        //Launching another thread to check, reset served date if need be.
        disposable = Single.fromCallable(this::checkServedDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((status) -> { //Runs on main thread
                    //By this point the servedCount must be reset or left as is after checking the dates.
                    servedCount = sharedPreferences.getInt(getString(R.string.users_served), 0);

                    setListenerForCount();
                    updateCountInFragment();
                });

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
     * Updates the users served count in the text-view of MainFragment if it's in the foreground or else It'll ignore.
     */
    private void updateCountInFragment() {
        Log.d(TAG, "updateCountInFragment: Updating count");

        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(FragmentConstants.MAIN_FRAGMENT));
        //If the fragment is in foreground update the count. Or else ignore.
        if (mainFragment != null) {
            ((MainFragment) mainFragment).showServed();
        }
    }

    /**
     * Used to update the count without restarting the app to update the users served count.
     * Listener is set on the file to check for changes.
     */
    private void setListenerForCount() {
        Log.d(TAG, "setListenerForCount: Setting listener");

        // Do NOT make the variable local. SP listener listens on WeakHashMap.
        // It'll get garbage collected as soon as code leaves the scope. Hence listener won't work.
        listener = (prefs, key) -> {
            Log.d(TAG, "setListenerForCount: Listener: Key = " + key);

            if (key.equals(getString(R.string.users_served))) {
                servedCount = sharedPreferences.getInt(key, 0);
                updateCountInFragment();
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Used to  replace the fragment in the "fragment_container"
     *
     * @param fragment New Fragment that is to be placed in the container.
     */
    private void startFragment(Fragment fragment) {
        if (fragment instanceof MainFragment) {
            currentFragment = FragmentConstants.MAIN_FRAGMENT;
        } else {
            currentFragment = FragmentConstants.APP_SETTINGS_FRAGMENT;
        }

        Log.d(TAG, "startFragment: " + currentFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fragment, Integer.toString(currentFragment)).commit();
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
     * @return Total served users count in the past 24hrs.
     */
    @Override
    public int getServed() {
        //By default 0 is returned until the thread finishes executing checkServedDate function.
        return servedCount;
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

    /**
     * Function to check and update the date and users served.
     * Resets served count if the past served date is greater than 24hrs.
     *
     * @return True if the date parsing is done right without errors.
     */
    public boolean checkServedDate() {
        Log.d(TAG, "checkServedDate: ");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            String stringCurrentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
            String stringRecordedDate = sharedPreferences.getString(getString(R.string.served_date), "");

            //No value for key. Set the date value to current date and users served to 0.
            if (stringRecordedDate.equals("")) {
                editor.putString(getString(R.string.served_date), stringCurrentDate);
                editor.putInt(getString(R.string.users_served), 0);
            } else {
                //Check if the current system date is greater than recorded date, if so reset the "served" flag.
                Date recordedDate = simpleDateFormat.parse(stringRecordedDate);
                Date currentDate = simpleDateFormat.parse(stringCurrentDate);

                Log.d(TAG, "checkServedDate: Current Date:" + currentDate.toString() + "  Recorded Date:" + recordedDate.toString());
                int comparision = currentDate.compareTo(recordedDate);

                if (comparision == 0) {
                    //Current date is same as recordedDate no need to reset. Since it's less than 24hrs.
                    return true;
                } else {
                    //Current date is bigger than recorded date. Reset the values. i.e comparision > 0
                    editor.putString(getString(R.string.served_date), simpleDateFormat.format(currentDate));
                    editor.putInt(getString(R.string.users_served), 0);
                }
            }

            editor.apply();

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "checkServedDate: Invalid Date Parsing");
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        //If the back is pressed on AppSettingsFragment take it back to MainFragment.
        if (currentFragment == FragmentConstants.APP_SETTINGS_FRAGMENT)
            startFragment(MainFragment.newInstance());
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //Killing of thread
        disposable.dispose();
        //Unregistering the listener.
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }
}
