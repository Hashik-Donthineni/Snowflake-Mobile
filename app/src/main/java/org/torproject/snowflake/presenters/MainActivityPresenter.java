package org.torproject.snowflake.presenters;

import android.util.Log;

import org.torproject.snowflake.MainActivity;
import org.torproject.snowflake.R;
import org.torproject.snowflake.constants.AppPreferenceConstants;
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
    public void detach() {
        Log.d(TAG, "detach: ");
        //Detaching
        view = null;
    }

    public int getServedCount() {
        Log.d(TAG, "getServedCount: ");
        if (view != null) {
            return model.getServedCount(AppPreferenceConstants.USER_SERVED_KEY);
        }
        return 0;
    }

    public boolean getInitialRunBoolean() {
        Log.d(TAG, "getInitialRunBoolean: ");
        if (view != null) {
            return model.getInitialRunBool(AppPreferenceConstants.INITIAL_RUN_KEY);
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
            model.setInitialRunBool(AppPreferenceConstants.INITIAL_RUN_KEY, val);
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
            return model.isServiceRunning();
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
        return model.getDate();
    }

    public void checkDate() {
        model.checkDateAsync();
    }

    /**
     * View for the MainActivity
     */
    public interface View {
        void updateCountInFragment(int i);
    }
}
