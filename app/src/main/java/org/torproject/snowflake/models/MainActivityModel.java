package org.torproject.snowflake.models;

import android.content.SharedPreferences;
import android.util.Log;

import org.torproject.snowflake.GlobalApplication;
import org.torproject.snowflake.constants.AppPreferenceConstants;
import org.torproject.snowflake.mvp.MainActivityMVPContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Model for MainActivity to handle network calls, Shared preferences.
 */
public class MainActivityModel implements MainActivityMVPContract.Model {
    private static final String TAG = "MainActivityModel";
    private static MainActivityModel instance = null;
    private SharedPreferences sharedPreferences;
    private MainActivityMVPContract.Presenter presenter;
    private int servedCount;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    private MainActivityModel(MainActivityMVPContract.Presenter presenter) {
        sharedPreferences = GlobalApplication.getAppPreferences();
        this.presenter = presenter;
        servedCount = 0;
    }

    public static MainActivityModel getInstance(MainActivityMVPContract.Presenter presenter) {
        if (instance == null) {
            synchronized (MainActivityModel.class) {
                instance = new MainActivityModel(presenter);
            }
        }
        return instance;
    }

    public int getServedCount() {
        return sharedPreferences.getInt(AppPreferenceConstants.USER_SERVED_KEY, 0);
    }

    public boolean getInitialRunBool() {
        return sharedPreferences.getBoolean(AppPreferenceConstants.INITIAL_RUN_KEY, true);
    }

    public void setInitialRunBool(boolean val) {
        sharedPreferences.edit().putBoolean(AppPreferenceConstants.INITIAL_RUN_KEY, val).apply();
    }

    public boolean isServiceRunning() {
        return sharedPreferences.getBoolean(AppPreferenceConstants.IS_SERVICE_RUNNING_KEY, false);
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

            if (key.equals(AppPreferenceConstants.USER_SERVED_KEY)) {
                servedCount = sharedPreferences.getInt(key, 0);
                if (presenter != null)
                    presenter.updateServedCount(servedCount);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public String getDate() {
        return sharedPreferences.getString(AppPreferenceConstants.DATE_KEY, "");
    }

    /**
     * Setting the users served date and value.
     */
    public void setDateAndServed(String date, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppPreferenceConstants.DATE_KEY, date);
        editor.putInt(AppPreferenceConstants.USER_SERVED_KEY, value);
        editor.apply();
    }

    /**
     * Function to check and update the date and users served.
     * Resets served count if the past served date is greater than 24hrs.
     *
     * @return True if the date parsing is done right without errors.
     */
    private boolean checkServedDate() {
        Log.d(TAG, "checkServedDate: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            String stringCurrentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
            String stringRecordedDate = getDate();

            //No value for key. Set the date value to current date and users served to 0.
            if (stringRecordedDate.equals("")) {
                setDateAndServed(stringCurrentDate, 0);
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
                    setDateAndServed(simpleDateFormat.format(currentDate), 0);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "checkServedDate: Invalid Date Parsing");
            return false;
        }
        return true;
    }

    public void checkDateAsync() {
        //Launching another thread to check, reset served date if need be.
        if (presenter != null) {
            //By this point the servedCount must be reset or left as is after checking the dates.
            Single.fromCallable(this::checkServedDate)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((status) -> { //Runs on main thread
                        //By this point the servedCount must be reset or left as is after checking the dates.
                        presenter.updateServedCount(getServedCount());
                        setListenerForCount();
                    });
        }
    }
}
