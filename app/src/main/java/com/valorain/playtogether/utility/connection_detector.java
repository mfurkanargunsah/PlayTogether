package com.valorain.playtogether.utility;

import android.app.Service;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class connection_detector {
    Context context;

    public connection_detector(Context context) {
        this.context = context;
    }

    public boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                                    return true;
                }

            }

        }
        return false;
    }

}
