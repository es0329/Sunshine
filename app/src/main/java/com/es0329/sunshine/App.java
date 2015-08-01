package com.es0329.sunshine;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    public static boolean isConnected;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    public class NetworkStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
    }
}
