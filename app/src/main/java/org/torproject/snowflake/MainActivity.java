package org.torproject.snowflake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.torproject.snowflake.constants.ForegroundServiceConstants;
import org.torproject.snowflake.constants.FragmentConstants;
import org.torproject.snowflake.fragments.AppSettingsFragment;
import org.torproject.snowflake.fragments.MainFragment;
import org.torproject.snowflake.interfaces.MainFragmentCallback;
import org.torproject.snowflake.mvp.MainActivityMVPContract;
import org.torproject.snowflake.presenters.MainActivityPresenter;

/**
 * MainActivity is the main UI of the application.
 */
public class MainActivity extends AppCompatActivity implements MainFragmentCallback, MainActivityMVPContract.View {
    private static final String TAG = "MainActivity";
    int currentFragment;
    MainActivityMVPContract.Presenter presenter;
    //Indicates if model finished checking the date and reset served count if need be.
    boolean isCheckDateFinished;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        settingsButton = findViewById(R.id.settings_button);
        presenter = new MainActivityPresenter(this);
        isCheckDateFinished = false;

        //Checks date asynchronously and sets or re-sets it and the users served.
        // After checking presenter calls the update count.
        presenter.checkDate();

        //Creating notification channel if app is being run for the first time
        if (presenter.getInitialRunBoolean()) {
            createNotificationChannel();
            //Setting initial run to false.
            presenter.setInitialRunBoolean(false);
        }

        settingsButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
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
        if (fragment instanceof MainFragment) {
            currentFragment = FragmentConstants.MAIN_FRAGMENT;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            settingsButton.setVisibility(View.VISIBLE);
        } else {
            currentFragment = FragmentConstants.APP_SETTINGS_FRAGMENT;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            settingsButton.setVisibility(View.GONE);
        }

        Log.d(TAG, "startFragment: " + currentFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fragment, Integer.toString(currentFragment)).commit();
    }

    @Override
    public boolean isServiceRunning() {
        return presenter.isServiceRunning();
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
        //Detach
        presenter.detach();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        updateCountInFragment(presenter.getServedCount());
        super.onResume();
    }

    /**
     * Updates the users served count in the text-view of MainFragment if it's in the foreground or else It'll ignore.
     */
    @Override
    public void updateCountInFragment(int servedCount) {
        Log.d(TAG, "updateCountInFragment: Updating count");

        isCheckDateFinished = true;

        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(FragmentConstants.MAIN_FRAGMENT));
        //If the fragment is in foreground update the count. Or else ignore.
        if (mainFragment != null) {
            ((MainFragment) mainFragment).showServed(servedCount);
        }
    }

    @Override
    public int getServed() {
        if (isCheckDateFinished)
            return presenter.getServedCount();
        else
            return 0;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
