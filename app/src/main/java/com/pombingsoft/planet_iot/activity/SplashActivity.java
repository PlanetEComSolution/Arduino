package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.activity_new.Device_HomeActivity_new;
import com.pombingsoft.planet_iot.util.UTIL;

import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //  forgotAllDevice();
        StartLoginActivity();


    }

    private void StartLoginActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                String UserId = UTIL.getPref(SplashActivity.this, UTIL.Key_UserId);
                String mobile = UTIL.getPref(SplashActivity.this, UTIL.Key_Mobile);
                if (UserId == null || UserId.equals("")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                } else {
                    //  startActivity(new Intent(SplashActivity.this,HomeActivity.class));

                    if (mobile.equals("9810334191")) {
                        startActivity(new Intent(SplashActivity.this, SuperAdminHomeActivity.class));//for Super admin
                    } else {
                       // startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        startActivity(new Intent(SplashActivity.this, ActivityWithNavigationMenu.class));

                    }


                }
                finish();

            }
        }, 2000);   //5 seconds


    }

    void forgotAllDevice() {
        try {
            WifiManager wifiManager;
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                String ssid = i.SSID;
                if (ssid != null && ssid.startsWith("\"" + UTIL.DeviceNamePrefix)) {
                    wifiManager.removeNetwork(i.networkId);
                    wifiManager.saveConfiguration();

                }
            }
        } catch (Exception e) {
            e.getCause();
        }
    }

}
