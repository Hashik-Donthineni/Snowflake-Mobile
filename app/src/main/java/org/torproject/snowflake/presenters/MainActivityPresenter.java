package org.torproject.snowflake.presenters;

import android.util.Log;

import org.torproject.snowflake.MainActivity;
import org.torproject.snowflake.R;
import org.torproject.snowflake.models.MainActivityModel;

/**
 * Presenter for MainActivity.
 */
public class MainActivityPresenter {
    private static final String TAG = "MainActivityPresenter";
    View view;
    MainActivityModel model;

    public MainActivityPresenter(View view) {
        //Attaching
        this.view = view;
        model = MainActivityModel.getInstance(this);
    }

    /**
     * Cleaning
     */
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        //Calling on Destroy on model
        model.onDestroy();
        //Detaching
        view = null;
    }

    public int getServedCount() {
        Log.d(TAG, "getServedCount: ");
        if (view != null) {
            return model.getServedCount(((MainActivity) view).getString(R.string.users_served_key));
        }
        return 0;
    }

    public boolean getInitialRunBoolean() {
        Log.d(TAG, "getInitialRunBoolean: ");
        if (view != null) {
            return model.getInitialRunBool(((MainActivity) view).getString(R.string.initial_run_boolean_key));
        }
        return false;
    }

    /**
     * Setting the initial run boolean.
     *
     * @param val Set to False/True
     */
    public void setInitialRunBoolean(boolean val) {
        Log.d(TAG, "setInitialRunBoolean: ");
        if (view != null) {
            model.setInitialRunBool(((MainActivity) view).getString(R.string.initial_run_boolean_key), val);
        }
    }

    /**
     * Test to see if the MyPersistentService is running or not.
     *
     * @return boolean whether the service is running or not.
     */
    public boolean isServiceRunning() {
        Log.d(TAG, "isServiceRunning: ");
        if (view != null) {
            return model.isServiceRunning(((MainActivity) view).getString(R.string.is_service_running_bool_key));
        }
        return true;
    }

    public void updateServedCount(int count) {
        Log.d(TAG, "updateServedCount: ");
        if (view != null) {
            view.updateCountInFragment(count);
        }
    }

    /**
     * Getting the served date.
     */
    public String getDate() {
        return model.getDate(((MainActivity) view).getString(R.string.served_date_key));
    }

    public void checkDate() {
        model.checkDateAsync();
    }

    public void setListenerForCount() {
    }

    /**
     * View for the MainActivity
     */
    public interface View {
        void updateCountInFragment(int i);
    }
}
