package com.valorain.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.valorain.playtogether.View.MainActivity;
import com.valorain.playtogether.utility.Common;
import com.valorain.playtogether.utility.NetworkChangeList;
import com.valorain.playtogether.utility.connection_detector;

public class SplashScreen extends Activity {

    connection_detector connectionDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        connectionDetector = new connection_detector(this);

        if (connectionDetector.isConnected()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }else

            Toast.makeText(SplashScreen.this,"İnternet Bağlantınızı Kontrol Edin!",Toast.LENGTH_LONG).show();

    }

}