package org.torproject.snowflake;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import org.torproject.snowflake.constants.ForegroundServiceConstants;

/**
 * Main Snowflake implementation of foreground service to relay data in the background.
 */
public class MyPersistentService extends Service {
    private static final String TAG = "MyPersistentService";
    private SharedPreferences sharedPreferences;
    private boolean isServiceStarted;
    private PowerManager.WakeLock wakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: executed with startId: " + startId);
        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreference_file), MODE_PRIVATE);

        if (intent != null) {
            if (intent.getAction().equals(ForegroundServiceConstants.ACTION_START))
                startService();
            else
                stopService();
        } else {
            Log.d("onStartCommand:", "Null intent detected"); //Intent is null if system restarts the service.
            startService(); //Starting the service since it's a "restart" of the service.
        }

        //If the service is killed. OS will restart this service if it's START_STICKY.
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service Created");

        sharedPreferences = getSharedPreferences(getString(R.string.sharedpreference_file), MODE_PRIVATE); //Assigning the shared preferences
        sharedPreferencesHelper(ForegroundServiceConstants.SERVICE_RUNNING); //Editing the shared preferences
        Notification notification = createPersistentNotification(false, null);
        startForeground(ForegroundServiceConstants.DEF_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Service Destroyed");
        sharedPreferencesHelper(ForegroundServiceConstants.SERVICE_STOPPED);

        //TODO: Dispose all connections.

        super.onDestroy();
    }

    /**
     * Helper function to edit shared preference file.
     *
     * @param setState State from ForegroundServiceConstants
     */
    private void sharedPreferencesHelper(final int setState) {
        Log.d(TAG, "sharedPreferencesHelper: Setting Shared Preference Running To: " + setState);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (setState == ForegroundServiceConstants.SERVICE_RUNNING) {
            editor.putBoolean(getString(R.string.is_service_running_bool), true);
        } else {
            editor.putBoolean(getString(R.string.is_service_running_bool), false);
        }
        editor.apply();
    }

    /**
     * Create a new persistent notification
     *
     * @param isUpdate is this new notification an update to current one?
     * @param update   String that is to be updated will current. Send "null" if isUpdate is false.
     * @return New Notification with given parameters.
     */
    private Notification createPersistentNotification(final boolean isUpdate, final String update) {
        Intent persistentNotIntent = new Intent(this, MyPersistentService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, persistentNotIntent, 0);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new Notification.Builder(this, ForegroundServiceConstants.NOTIFICATION_CHANNEL_ID);
        else
            builder = new Notification.Builder(this);

        builder
                .setContentTitle("Snowflake Service")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH); // Android 26 and above needs priority.

        //If it's a notification update. Set the text to updated notification.
        if (isUpdate) {
            builder.setContentText(update)
                    .setTicker(update);
        } else {
            builder.setContentText("Snowflake Proxy Running")
                    .setTicker("Snowflake Proxy Running");
        }

        return builder.build();
    }

    //Starting and stopping service

    /**
     * Use to star/re-start the service
     */
    private void startService() {
        if (isServiceStarted) {
            Log.d(TAG, "startService: Service Already running.");
            return;
        }
        Log.d(TAG, "startService: Starting foreground service");
        isServiceStarted = true;

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(); //WakeLock acquired for unlimited amount of time.

        ///
        //TODO: Start WebRTC connection.
    }

    /**
     * Use to stop the service.
     */
    private void stopService() {
        Log.d(TAG, "stopService:Stopping the foreground service");
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
            stopForeground(true); //To remove the notification.
            stopSelf(); //Calls onDestroy to destroy the service and dispose all the connections
        } catch (Exception e) {
            Log.d(TAG, "stopService: Failed with: " + e.getMessage());
        }
        isServiceStarted = false;
    }

    /////////////////////////////////////
}
