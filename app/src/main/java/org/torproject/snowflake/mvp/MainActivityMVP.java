package org.torproject.snowflake.mvp;

/**
 * MVP contract Interface
 */
public interface MainActivityMVP {
    interface View {
        void updateCountInFragment(int i);
    }

    interface Model {
        boolean getInitialRunBool();

        void setInitialRunBool(boolean val);

        boolean isServiceRunning();

        void checkDateAsync();

        int getServedCount();
    }

    interface Presenter {
        int getServedCount();

        boolean getInitialRunBoolean();

        void setInitialRunBoolean(boolean val);

        boolean isServiceRunning();

        void updateServedCount(int count);

        void checkDate();
    }
}
