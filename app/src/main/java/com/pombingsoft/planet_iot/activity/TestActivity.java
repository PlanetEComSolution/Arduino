package com.pombingsoft.planet_iot.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;


import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.util.UTIL;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final ProgressDialog progressDialog = ProgressDialog.show(TestActivity.this, null, "vcvv", true);
        progressDialog.setContentView(R.layout.elemento_progress);

        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressDialog.show();

        progressDialog.setCancelable(true);
        final TextView tv = progressDialog.getWindow().findViewById(R.id.textView6);

        tv.setText("Disconnecting from device...");

        try {


            //now wait
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    tv.setText("Connecting to your Wi-Fi ...");


                }
            }, 5000);   //5 seconds

        } catch (Exception e) {
            e.getCause();
        }

    }


}
