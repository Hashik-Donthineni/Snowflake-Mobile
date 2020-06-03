package org.torproject.snowflake;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

//Main foreground service to handle network calls and to relay the data in the back ground.
public class MyPersistentService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
