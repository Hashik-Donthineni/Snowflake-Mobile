package org.torproject.snowflake.models;

import android.content.SharedPreferences;
import android.util.Log;

import org.torproject.snowflake.GlobalApplication;
import org.torproject.snowflake.presenters.MainActivityPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Model for MainActivity to handle network calls, Shared preferences.
 */
public class MainActivityModel {
    private static final String TAG = "MainActivityModel";
    private static MainActivityModel instance = null;
    private SharedPreferences sharedPreferences;
    private MainActivityPresenter presenter;
    private int servedCount;
    private Disposable disposable;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    private MainActivityModel(MainActivityPresenter presenter) {
        sharedPreferences = GlobalApplication.getAppPreferences();
        this.presenter = presenter;
        servedCount = 0;
    }

    public static MainActivityModel getInstance(MainActivityPresenter presenter) {
        if (instance == null) {
            synchronized (MainActivityModel.class) {
                instance = new MainActivityModel(presenter);
            }
        }
        return instance;
    }

    public int getServedCount(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public boolean getInitialRunBool(String key) {
        return sharedPreferences.getBoolean(key, true);
    }

    public void setInitialRunBool(String key, boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public boolean isServiceRunning(String key) {
        return sharedPreferences.getBoolean(key, false);
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

            if (key.equals("users_served")) {
                servedCount = sharedPreferences.getInt(key, 0);
                if (presenter != null)
                    presenter.updateServedCount(servedCount);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        //Unregistering the listener.
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        //Disposing off call
        disposable.dispose();
        //Detaching presenter
        presenter = null;
    }

    public String getDate(String dateKey) {
        return sharedPreferences.getString(dateKey, "");
    }

    /**
     * Setting the users served date and value.
     */
    public void setDateAndServed(String dateKey, String valueKey, String date, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dateKey, date);
        editor.putInt(valueKey, value);
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
            String stringRecordedDate = presenter.getDate();

            //No value for key. Set the date value to current date and users served to 0.
            if (stringRecordedDate.equals("")) {
                setDateAndServed("date", "users_served", stringCurrentDate, 0);
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
                    setDateAndServed("date", "users_served", simpleDateFormat.format(currentDate), 0);
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
            disposable = Single.fromCallable(this::checkServedDate)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((status) -> { //Runs on main thread
                        //By this point the servedCount must be reset or left as is after checking the dates.
                        presenter.updateServedCount(getServedCount("users_served"));
                        setListenerForCount();
                    });
        }
    }
}
