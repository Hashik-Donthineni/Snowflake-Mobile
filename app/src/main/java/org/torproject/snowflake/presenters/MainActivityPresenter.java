package org.torproject.snowflake.presenters;

import android.util.Log;

import org.torproject.snowflake.models.MainActivityModel;
import org.torproject.snowflake.mvp.MainActivityMVP;

/**
 * Presenter for MainActivity.
 */
public class MainActivityPresenter implements MainActivityMVP.Presenter {
    private static final String TAG = "MainActivityPresenter";
    MainActivityMVP.View view;
    MainActivityMVP.Model model;

    public MainActivityPresenter(MainActivityMVP.View view) {
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
            return model.getServedCount();
        }
        return 0;
    }

    public boolean getInitialRunBoolean() {
        Log.d(TAG, "getInitialRunBoolean: ");
        if (view != null) {
            return model.getInitialRunBool();
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
            model.setInitialRunBool(val);
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
