package org.torproject.snowflake.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.torproject.snowflake.R;
import org.torproject.snowflake.constants.ForegroundServiceConstants;
import org.torproject.snowflake.interfaces.MainFragmentCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    MainFragmentCallback callback;
    TextView usersServedTV;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment main_fragment.
     */

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        usersServedTV = rootView.findViewById(R.id.users_served);
        Switch startButton = rootView.findViewById(R.id.snowflake_switch);

        startButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (callback.isServiceRunning() && !isChecked) { //Toggling the service.
                startButton.setText(getString(R.string.Snowflake_Off));
                callback.serviceToggle(ForegroundServiceConstants.ACTION_STOP);
            } else {
                startButton.setText(getString(R.string.Snowflake_On));
                callback.serviceToggle(ForegroundServiceConstants.ACTION_START);
            }
        });
        showServed(callback.getServed());

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (MainFragmentCallback) context;
    }

    public void showServed(int served) {
        Log.d(TAG, "showServed: " + served);

        if (served > 0) {
            usersServedTV.setVisibility(View.VISIBLE);
            usersServedTV.setText(getString(R.string.users_served_text) + served);
        }
    }
}