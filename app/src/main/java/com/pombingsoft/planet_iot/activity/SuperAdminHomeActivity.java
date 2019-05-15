package com.pombingsoft.planet_iot.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.air_quality_monitor.SuperAdminActivity;
import com.pombingsoft.planet_iot.util.UTIL;

public class SuperAdminHomeActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_home);


        LinearLayout ll_message = (LinearLayout) findViewById(R.id.ll_message);
         LinearLayout ll_Logout = (LinearLayout) findViewById(R.id.ll_Logout);



        ll_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SuperAdminHomeActivity.this,SuperAdminActivity.class));
            }
        });

        ll_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_LOGOUT();

            }
        });







    }
    @Override
    public void onBackPressed() {


            //  super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();

                return;
            }


            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit!",
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 2000);



    }
    private void dialog_LOGOUT() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title =  dialogView.findViewById(R.id.title);
        final TextView message =  dialogView.findViewById(R.id.message);
        final Button positiveBtn =  dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn =  dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Do you want to logout?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                UTIL.clearPref(SuperAdminHomeActivity.this);
                Intent abc = new Intent(SuperAdminHomeActivity.this, LoginActivity.class);
                startActivity(abc);
                Toast.makeText(SuperAdminHomeActivity.this, "Logout successfully", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }
}
