package com.pombingsoft.planet_iot.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.air_quality_monitor.UpdateDevice_C_Activity;
//import com.pombingsoft.planet_iot.activity.security_monitor.DeviceD_RunningSchedulerActivity;
import com.pombingsoft.planet_iot.activity.security_monitor.DataLogActivity;
import com.pombingsoft.planet_iot.activity.security_monitor.UpdateDevice_D_Activity;
import com.pombingsoft.planet_iot.activity.switch_controller.DeviceDetailActivity;
import com.pombingsoft.planet_iot.activity.switch_controller.SchedulerActivity;
import com.pombingsoft.planet_iot.activity.switch_controller.UpdateDevice_A_Activity;
import com.pombingsoft.planet_iot.activity.water_level_controller.Device_RunningSchedulerActivity;
import com.pombingsoft.planet_iot.activity.water_level_controller.Update_Device_B_Activity;
import com.pombingsoft.planet_iot.model.Switch;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;
import com.warkiz.widget.IndicatorSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Device_HomeActivity extends AppCompatActivity {
    String UserId, DeviceId, DeviceName;
    TextView tv_deviceName;
    Context myContext;
    UTIL utill;


    AlertDialog alertDialog;
    String RoleId, DeviceLockStatus, Device_Type, RegisteredBy;

    String availability = UTIL.OFFLINE;

    ListView listview_switch;

    String status, TimeZone, Device_Password, Dimmer_1, Dimmer_2, Dimmer_3;
    String Status_Dimmer_1, Status_Dimmer_2, Status_Dimmer_3;

    long TotalelapsedSeconds;
    LinearLayout ll_device_C, ll_device_B, ll_device_A, ll_device_D;


    //DeviceNodeMcu B

    TextView tv_overhead_tank_level, tv_lower_tank_level, tv_pump_status, tv_Consumption_Today, tv_Average_Consumption, tv_Pump_flow_Rate;

    View action_DeviceStatus, action_Device_Scheduler, action_DeviceDataLog;


    //DeviceNodeMcu C

    TextView tv_temp,
            tv_humidity,
            tv_LPG_Con,
            tv_CO_Cen,
            tv_Gen_Toxin_Concen,
            tv_Dust_Particle_Con;

    LinearLayout ll_temp, ll_humidity, ll_LPG_Con, ll_CO_Cen, ll_Gen_Toxin_Concen, ll_Dust_Particle_Con;
    ImageView img_notification_normal, img_notification_sign;
    LinearLayout ll_notification;

    String Device_Active_Inactive_status = "0";
    SwitchCompat switchButton_buzzer;

    boolean switch_set_manullay = false;
    boolean buzzer_switch_set_manullay = false;


    //  DeviceNodeMcu D
    TextView tv_temp_D,
            tv_humidity_D,
            tv_FireAlarm,
            tv_Security_Breach,
            tv_Flood_Level;

    SwitchCompat switchButton_buzzer_D;

    LinearLayout ll_temp_D, ll_humidity_D, ll_FireAlarm, ll_SecurityBreach, ll_Flood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_home_main);
        tv_deviceName = (TextView) findViewById(R.id.deviceName);
        myContext = Device_HomeActivity.this;
        utill = new UTIL(myContext);
        init();
        setFAB();
    }

    private void init() {
        listview_switch = findViewById(R.id.listview_switch);

        ll_device_A = findViewById(R.id.ll_device_A);
        ll_device_B = findViewById(R.id.ll_device_B);
        ll_device_C = findViewById(R.id.ll_device_C);
        ll_device_D = findViewById(R.id.ll_device_D);

        //notification
        img_notification_normal = findViewById(R.id.img_notification_normal);
        img_notification_sign = findViewById(R.id.img_notification_sign);
        ll_notification = findViewById(R.id.ll_notification);
        //DeviceNodeMcu B
        tv_overhead_tank_level = findViewById(R.id.tv_overhead_tank_level);
        tv_lower_tank_level = findViewById(R.id.tv_lower_tank_level);
        tv_pump_status = findViewById(R.id.tv_pump_status);
        tv_Consumption_Today = findViewById(R.id.tv_Consumption_Today);
        tv_Average_Consumption = findViewById(R.id.tv_Average_Consumption);
        tv_Pump_flow_Rate = findViewById(R.id.tv_Pump_flow_Rate);
        //DeviceNodeMcu B


        //DeviceNodeMcu C
        tv_temp = findViewById(R.id.tv_temp);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_LPG_Con = findViewById(R.id.tv_LPG_Con);
        tv_CO_Cen = findViewById(R.id.tv_CO_Cen);
        tv_Gen_Toxin_Concen = findViewById(R.id.tv_Gen_Toxin_Concen);
        tv_Dust_Particle_Con = findViewById(R.id.tv_Dust_Particle_Con);
        ll_temp = findViewById(R.id.ll_temp);
        ll_humidity = findViewById(R.id.ll_humidity);
        ll_LPG_Con = findViewById(R.id.ll_LPG_Con);
        ll_CO_Cen = findViewById(R.id.ll_CO_Cen);
        ll_Gen_Toxin_Concen = findViewById(R.id.ll_Gen_Toxin_Concen);
        ll_Dust_Particle_Con = findViewById(R.id.ll_Dust_Particle_Con);
        switchButton_buzzer =
                findViewById(R.id.switchButton_buzzer);
        //DeviceNodeMcu C


        //DeviceNodeMcu D
        tv_temp_D = findViewById(R.id.tv_temp_D);
        tv_humidity_D = findViewById(R.id.tv_humidity_D);
        tv_FireAlarm = findViewById(R.id.tv_FireAlarm);
        tv_Security_Breach = findViewById(R.id.tv_Security_Breach);
        tv_Flood_Level = findViewById(R.id.tv_Flood_Level);

        switchButton_buzzer_D = findViewById(R.id.switchButton_buzzer_D);

        ll_temp_D = findViewById(R.id.ll_temp_D);
        ll_humidity_D = findViewById(R.id.ll_humidity_D);
        ll_FireAlarm = findViewById(R.id.ll_FireAlarm);
        ll_SecurityBreach = findViewById(R.id.ll_SecurityBreach);
        ll_Flood = findViewById(R.id.ll_Flood);
        //DeviceNodeMcu D


        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            DeviceLockStatus = i.getStringExtra("LockStatus");
            Device_Type = i.getStringExtra("DeviceType");
        } catch (Exception e) {
            e.getMessage();
        }


        //


        //

        switchButton_buzzer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buzzer_switch_set_manullay = true;
                return false;
            }
        });

        switchButton_buzzer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (availability.equalsIgnoreCase(UTIL.ONLINE)) {

                    if (buzzer_switch_set_manullay) {
                        String buzzer_val;
                        if (isChecked) {
                            buzzer_val = "1";
                        } else {
                            buzzer_val = "0";
                        }
                        SetBuzzer_Device_C(DeviceId, buzzer_val);
                    }
                    buzzer_switch_set_manullay = false;


                } else {
                    onResume();
                }


            }


        });


        switchButton_buzzer_D.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buzzer_switch_set_manullay = true;
                return false;
            }
        });

        switchButton_buzzer_D.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (availability.equalsIgnoreCase(UTIL.ONLINE)) {
                    if (buzzer_switch_set_manullay) {
                        String buzzer_val;
                        if (isChecked) {
                            buzzer_val = "1";
                        } else {
                            buzzer_val = "0";
                        }
                        SetBuzzer_Device_D(DeviceId, buzzer_val);
                    }
                    buzzer_switch_set_manullay = false;

                } else {
                    // Show_DeviceAvailability("DeviceNodeMcu is Offline!", "Please try after some time!");
                    onResume();
                }


            }


        });


        img_notification_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartNotificationActivity();

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceDetail();

    }

    void ON_OFF_PIN(String Pin_Number, String value) {

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            String tag_string_req = "req_login";

/*
            final ProgressDialog pDialog = new ProgressDialog(myContext);
            pDialog.setCancelable(false);
            pDialog.setMessage("Kindly Wait...");
            if (!pDialog.isShowing())
                pDialog.show();*/

            //   utill.showProgressDialog("Kindly Wait...");


            String URL_LOGIN = null;
            try {

                URL_LOGIN = UTIL.Domain_Arduino + UTIL.ControlDeviceA_API + "DeviceId=" + DeviceId + "&WhatPinName=" + Pin_Number + "&Val_Pin=" + value;

                Log.e("Api URL:", URL_LOGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        ///{"errormsg":"UpdateControlDevice","Status":"1","UserId":null}
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("Status");
                        if (status.equals("1")) {//request updated at server


                        } else {

                            //  pDialog.dismiss();

                            Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        //  pDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(Device_HomeActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //  refresh();


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    // pDialog.dismiss();
                    Toast.makeText(Device_HomeActivity.this, "Volly Error", Toast.LENGTH_SHORT).show();

                }
            }) {


            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }


    }


    private void refresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                    getDeviceDetail();
                } else {
                    Toast.makeText(myContext,
                            UTIL.NoInternet, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }, UTIL.Time_Recall);   //5 seconds*/
    }



    private void openDialog_DeleteDevice() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Do you want to delete this DeviceNodeMcu?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                CallApi_DeleteDevice();


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

    private void CallApi_DeleteDevice() {
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            String tag_string_req = "req_login";
            //   utill.showProgressDialog("Kindly Wait...");


      /*      final ProgressDialog pDialog = new ProgressDialog(myContext);
            pDialog.setCancelable(false);
            pDialog.setMessage("Kindly Wait...");
            if (!pDialog.isShowing())
                pDialog.show();
*/
            utill.showProgressDialog("Deleting device...");

            String URL_LOGIN = null;

            //http://ohmax.in/api/Test_Arduino/DeleteDeviceee?DeviceIdd=flqcFpklr9
            try {
                URL_LOGIN = UTIL.Domain_Arduino + UTIL.DeleteDevice_API_New + "DeviceId=" + DeviceId + "&DeviceType=" + Device_Type;

                Log.e("Api URL:", URL_LOGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }


            StringRequest strReq = new StringRequest(Request.Method.GET,
                    URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    utill.hideProgressDialog();

                    try {
                        ///{"errormsg":"Your DeviceNodeMcu is Deleted","Status":"1","UserId":"00000002"}
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("Status");
                        if (status.equals("1")) {//request updated at server

                            Toast.makeText(Device_HomeActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            //   utill.hideProgressDialog();
                            Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                        }

                        //


                    } catch (Exception e) {
                        //  utill.hideProgressDialog();
                        e.printStackTrace();
                        Toast.makeText(Device_HomeActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    utill.hideProgressDialog();

                    // utill.hideProgressDialog();
                    Toast.makeText(Device_HomeActivity.this, "Volly Error", Toast.LENGTH_SHORT).show();

                }
            }) {


            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }


    }

    private void setFAB() {
        final LinearLayout main_bg = findViewById(R.id.main_bg);
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);


        menuMultipleActions.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                main_bg.setAlpha(0.4f);

            }

            @Override
            public void onMenuCollapsed() {

                main_bg.setAlpha(01f);
            }
        });


        action_Device_Scheduler = findViewById(R.id.action_DeviceB_Scheduler);
        action_Device_Scheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();

                // if (true)
                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_General))//means current user of this device is admin,he originally added the device
                {
                    Toast.makeText(myContext, "Sorry! You can not view schedules of this device.", Toast.LENGTH_SHORT).show();

                } else {

                    //    if(Device_Type.equals("B")) {

                    Intent intent = (new Intent(myContext, Device_RunningSchedulerActivity.class));
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("DeviceId", DeviceId);
                    intent.putExtra("Device_Type", Device_Type);
                    intent.putExtra("DeviceName", DeviceName);
                    intent.putExtra("RoleId", RoleId);

                    startActivity(intent);
                    //  }
                   /* else  if(Device_Type.equals("D1") || Device_Type.equals("D2") ||
                            Device_Type.equals("D3") ||Device_Type.equals("D4") ||
                            Device_Type.equals("D5") ) {

                        Intent intent = (new Intent(myContext, DeviceD_RunningSchedulerActivity.class));
                        intent.putExtra("UserId", UserId);
                        intent.putExtra("DeviceId", DeviceId);
                        intent.putExtra("Device_Type", Device_Type);
                        intent.putExtra("DeviceName", DeviceName);
                        intent.putExtra("RoleId", RoleId);

                        startActivity(intent);
                    }*/
                }

            }
        });


        final View actionA = findViewById(R.id.action_edit);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();
                //  if (true)//means user of this device is admin,he originally added the device


                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_General) || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Manager))//means current user of this device is admin,he originally added the device
                {
                    Toast.makeText(myContext, "Sorry! You can not edit this device info.", Toast.LENGTH_SHORT).show();

                } else {


                    ArrayList<String> arrayList_device_type_Switch =
                            new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
                    ArrayList<String> arrayList_device_type_WaterLevelController =
                            new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
                    ArrayList<String> arrayList_device_type_AirQualityMonitor =
                            new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));

                    ArrayList<String> arrayList_device_type_SecurityMonitor =
                            new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));


                    if (arrayList_device_type_Switch.contains(Device_Type)) {

                        Intent i = (new Intent(myContext, UpdateDevice_A_Activity.class));
                        i.putExtra("DeviceId", DeviceId);
                        i.putExtra("UserId", UserId);
                        i.putExtra("Device_Type", Device_Type);
                        i.putExtra("RoleId", RoleId);
                        startActivity(i);


                    } else if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {
                        Intent i = (new Intent(myContext, Update_Device_B_Activity.class));
                        i.putExtra("DeviceId", DeviceId);
                        i.putExtra("UserId", UserId);
                        i.putExtra("Device_Type", Device_Type);
                        i.putExtra("RoleId", RoleId);
                        startActivity(i);

                    } else if (arrayList_device_type_AirQualityMonitor.contains(Device_Type)) {

                        Intent i = (new Intent(myContext, UpdateDevice_C_Activity.class));
                        i.putExtra("DeviceId", DeviceId);
                        i.putExtra("UserId", UserId);
                        i.putExtra("Device_Type", Device_Type);
                        i.putExtra("RoleId", RoleId);
                        startActivity(i);

                    } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {

                        Intent i = (new Intent(myContext, UpdateDevice_D_Activity.class));
                        i.putExtra("DeviceId", DeviceId);
                        i.putExtra("UserId", UserId);
                        i.putExtra("Device_Type", Device_Type);
                        i.putExtra("RoleId", RoleId);
                        startActivity(i);

                    }

                }


            }
        });


        final View actionC = findViewById(R.id.action_delete_device);
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();

                // if (true)
                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin) || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin))//means user of this device is admin,he originally added the device
                {
                    openDialog_DeleteDevice();
                } else {
                    Toast.makeText(myContext, "Sorry! You can not delete this device.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        action_DeviceDataLog = findViewById(R.id.action_DeviceDataLog);
        action_DeviceDataLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();

                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin)
                        || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin))//means user of this device is admin,he originally added the device
                {

                    Intent i = new Intent(myContext, DataLogActivity.class);
                    i.putExtra("DeviceName", DeviceName);
                    i.putExtra("DeviceId", DeviceId);

                    startActivity(i);

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view this device status.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        action_DeviceStatus = findViewById(R.id.action_DeviceStatus);
        action_DeviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();

                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin)
                        || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin))//means user of this device is admin,he originally added the device
                {

                    showDeviceStatus();

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view this device status.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        final View action_deviceDetails = findViewById(R.id.action_deviceDetails);
        action_deviceDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();

                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin) || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin)) {//means user of this device is admin,he originally added the device

                    // showDeviceDetailsDialog();
                    Intent i = new Intent(myContext, DeviceDetailActivity.class);
                    i.putExtra("DeviceName", DeviceName);
                    i.putExtra("DeviceId", DeviceId);
                    i.putExtra("Device_Password", Device_Password);
                    i.putExtra("Current_User_Role", RoleId);
                    startActivity(i);

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view this device details.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        final View action_DeviceLockStatus = findViewById(R.id.action_DeviceLockStatus);
        action_DeviceLockStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();
                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin) || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin)) {//means user of this device is admin,he originally added the device

                    showDeviceLockStatus();

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view device lock status.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void showDeviceLockStatus() {


        String msg = "";
        String title_msg = "";
        final String lock;
        if (DeviceLockStatus.equalsIgnoreCase("0")) {
            msg = "DeviceNodeMcu is UNLOCKED";
            title_msg = "Do you want to LOCK device?";
            lock = "1";
        } else {
            msg = "DeviceNodeMcu is LOCKED";
            title_msg = "Do you want to UNLOCK device?";
            lock = "0";
        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);

        title.setText(msg);
        message.setText(title_msg);
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin))//means user of this device is admin,he originally added the device
                {
                    Lock_UnlockDevice(lock);
                } else {
                    Toast.makeText(myContext, "Sorry! You can not lock/unlock this device.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    public void showDeviceStatus() {

        String msg = "";
        String title_msg = "";
        final String status;


        if (Device_Active_Inactive_status.equals("1")) {
            msg = "DeviceNodeMcu is ACTIVE";
            title_msg = "Do you want to INACTIVE device?";
            status = "0";
        } else if (Device_Active_Inactive_status == null || Device_Active_Inactive_status.equalsIgnoreCase("0")) {
            msg = "DeviceNodeMcu is INACTIVE";
            title_msg = "Do you want to ACTIVE device?";
            status = "1";
        } else {
            msg = "DeviceNodeMcu is INACTIVE";
            title_msg = "Do you want to ACTIVE device?";
            status = "1";
        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);

        title.setText(msg);
        message.setText(title_msg);
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin))//means user of this device is admin,he originally added the device
                {
                    /*if(Device_Type.equals("C1") ||Device_Type.equals("C2") ||Device_Type.equals("C3") ||Device_Type.equals("C4") )
                    {  SetActive_inactive_Device_C(DeviceId, status);}
                else if(Device_Type.equals("D1") ||Device_Type.equals("D2") ||Device_Type.equals("D3") ||Device_Type.equals("D4")||Device_Type.equals("D5") )
                    {  SetActive_inactive_Device_D(DeviceId, status);}
*/


                    SetDeviceActive_inactive(DeviceId, Device_Type, status);

                } else {
                    Toast.makeText(myContext, "Sorry! You can not active/inactive this device.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    private void Lock_UnlockDevice(String lock) {
        String tag_string_req = "req_login";
        //  utill.showProgressDialog("Kindly wait...");
       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting devices...");
        if (!pDialog.isShowing())
            pDialog.show();
*/
        utill.showProgressDialog("Changing lock status...");


        String URL = null;

        HashMap<String, String> params = new HashMap<>();
        params.put("DeviceType", Device_Type);
        params.put("DeviceId", DeviceId);
        params.put("LockStatus", lock);


        try {
            URL = UTIL.Domain_Arduino + UTIL.Lock_Unlock_API;
            URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            utill.hideProgressDialog();
                            //  utill.hideProgressDialog();
                            JSONObject jsonObject = response;
                            String status = jsonObject.getString("Status");
                            if (status.equals("1")) {//request updated at server
                                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                                //finish();
                            } else {

                                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            // utill.hideProgressDialog();
                            e.printStackTrace();
                            Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                            //getDeviceDetailsA_ApiCall();
                            getDeviceDetail();
                        } else {
                            Toast.makeText(myContext,
                                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   utill.hideProgressDialog();
                utill.hideProgressDialog();

                Toast.makeText(myContext, "Volly error: ", Toast.LENGTH_SHORT).show();

            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }

    private void Show_DeviceAvailability(String title_str, String msg_str) {

        if (alertDialog != null && alertDialog.isShowing()) return;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);

        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    alertDialog.dismiss();
                }
                catch (Exception e){e.getCause();}
                // finish();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    alertDialog.dismiss();
                }
                catch (Exception e){e.getCause();}
            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        try {
            alertDialog.show();
        } catch (Exception e) {
            e.getCause();
        }
    }

    private void CheckDeviceOnline() {

        if (TotalelapsedSeconds == UTIL.Error_val) {
            availability = UTIL.ERROR;
            Show_DeviceAvailability("Please restart device!", "Try after few moments!");
        } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
            availability = UTIL.ONLINE;
        } else {
            availability = UTIL.OFFLINE;
            Show_DeviceAvailability("DeviceNodeMcu is Offline!", "Please try after some time!");
        }


    }

    private void SetDimmer(String DimmerType,
                           String DimmerValue,
                           String DeviceId) {


        String tag_string_req = "req_login";
        // utill.showProgressDialog("Kindly wait...");


     //   utill.showProgressDialog("");


        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("DeviceId", DeviceId);
        params.put("DimmerType", DimmerType);
        params.put("DimmerValue", DimmerValue);


        URL = UTIL.Domain_Arduino + UTIL.SetDimmer_API;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                      //  utill.hideProgressDialog();


                        try {
                            JSONObject jsonObject = response;
                            String status = jsonObject.getString("Status");
                            if (status.equals("1")) {//request updated at server
                                //   Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                            } else {
                                // utill.hideProgressDialog();
                                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // utill.hideProgressDialog();
                            e.printStackTrace();
                            Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                       /* Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // yourMethod();
                                // utill.hideProgressDialog();
                                //    pDialog.dismiss();

                                if (new ConnectionDetector(myContext).isConnectingToInternet()) {

                                    getDeviceDetail();
                                } else {
                                    Toast.makeText(myContext,
                                            UTIL.NoInternet, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }, UTIL.Time_Recall);   //5 seconds*/


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   utill.hideProgressDialog();


                Toast.makeText(myContext, "Failed!", Toast.LENGTH_SHORT).show();

            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }



    void getDeviceDetailsA_ApiCall() {
        String tag_string_req = "req_login";

    /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsA_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                // pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String Id = jsonObject.getString("ID");
                        String UserId = jsonObject.getString("UserId");
                        String InsertDate = jsonObject.getString("InsertDate");
                        String DeviceId = jsonObject.getString("DeviceId");
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");

                        String Pin_D0 = jsonObject.getString("Pin_D0");
                        String Pin_D1 = jsonObject.getString("Pin_D1");
                        String Pin_D2 = jsonObject.getString("Pin_D2");
                        String Pin_D3 = jsonObject.getString("Pin_D3");
                        String Pin_D4 = jsonObject.getString("Pin_D4");
                        String Pin_D5 = jsonObject.getString("Pin_D5");
                        String Pin_D6 = jsonObject.getString("Pin_D6");
                        String Pin_D7 = jsonObject.getString("Pin_D7");
                        String Pin_D8 = jsonObject.getString("Pin_D8");
                        String Pin_D9 = jsonObject.getString("Pin_D9");


                        String Status_Pin_d0 = jsonObject.getString("Status_Pin_d0");
                        String Status_Pin_d1 = jsonObject.getString("Status_Pin_d1");
                        String Status_Pin_d2 = jsonObject.getString("Status_Pin_d2");
                        String Status_Pin_d3 = jsonObject.getString("Status_Pin_d3");
                        String Status_Pin_d4 = jsonObject.getString("Status_Pin_d4");
                        String Status_Pin_d5 = jsonObject.getString("Status_Pin_d5");
                        String Status_Pin_d6 = jsonObject.getString("Status_Pin_d6");
                        String Status_Pin_d7 = jsonObject.getString("Status_Pin_d7");
                        String Status_Pin_d8 = jsonObject.getString("Status_Pin_d8");
                        String Status_Pin_d9 = jsonObject.getString("Status_Pin_d9");

                        String Val_Pin_d0 = jsonObject.getString("Val_Pin_d0");
                        String Val_Pin_d1 = jsonObject.getString("Val_Pin_d1");
                        String Val_Pin_d2 = jsonObject.getString("Val_Pin_d2");
                        String Val_Pin_d3 = jsonObject.getString("Val_Pin_d3");
                        String Val_Pin_d4 = jsonObject.getString("Val_Pin_d4");
                        String Val_Pin_d5 = jsonObject.getString("Val_Pin_d5");
                        String Val_Pin_d6 = jsonObject.getString("Val_Pin_d6");
                        String Val_Pin_d7 = jsonObject.getString("Val_Pin_d7");
                        String Val_Pin_d8 = jsonObject.getString("Val_Pin_d8");
                        String Val_Pin_d9 = jsonObject.getString("Val_Pin_d9");

                        String Pin_d0_Scheduler_1 = jsonObject.getString("Pin_d0_Scheduler_1");
                        String Pin_d0_Scheduler_2 = jsonObject.getString("Pin_d0_Scheduler_2");
                        String Pin_d0_Scheduler_3 = jsonObject.getString("Pin_d0_Scheduler_3");
                        String Pin_d1_Scheduler_1 = jsonObject.getString("Pin_d1_Scheduler_1");
                        String Pin_d1_Scheduler_2 = jsonObject.getString("Pin_d1_Scheduler_2");
                        String Pin_d1_Scheduler_3 = jsonObject.getString("Pin_d1_Scheduler_3");
                        String Pin_d2_Scheduler_1 = jsonObject.getString("Pin_d2_Scheduler_1");
                        String Pin_d2_Scheduler_2 = jsonObject.getString("Pin_d2_Scheduler_2");
                        String Pin_d2_Scheduler_3 = jsonObject.getString("Pin_d2_Scheduler_3");
                        String Pin_d3_Scheduler_1 = jsonObject.getString("Pin_d3_Scheduler_1");
                        String Pin_d3_Scheduler_2 = jsonObject.getString("Pin_d3_Scheduler_2");
                        String Pin_d3_Scheduler_3 = jsonObject.getString("Pin_d3_Scheduler_3");
                        String Pin_d4_Scheduler_1 = jsonObject.getString("Pin_d4_Scheduler_1");
                        String Pin_d4_Scheduler_2 = jsonObject.getString("Pin_d4_Scheduler_2");
                        String Pin_d4_Scheduler_3 = jsonObject.getString("Pin_d4_Scheduler_3");
                        String Pin_d5_Scheduler_1 = jsonObject.getString("Pin_d5_Scheduler_1");
                        String Pin_d5_Scheduler_2 = jsonObject.getString("Pin_d5_Scheduler_2");
                        String Pin_d5_Scheduler_3 = jsonObject.getString("Pin_d5_Scheduler_3");
                        String Pin_d6_Scheduler_1 = jsonObject.getString("Pin_d6_Scheduler_1");
                        String Pin_d6_Scheduler_2 = jsonObject.getString("Pin_d6_Scheduler_2");
                        String Pin_d6_Scheduler_3 = jsonObject.getString("Pin_d6_Scheduler_3");
                        String Pin_d7_Scheduler_1 = jsonObject.getString("Pin_d7_Scheduler_1");
                        String Pin_d7_Scheduler_2 = jsonObject.getString("Pin_d7_Scheduler_2");
                        String Pin_d7_Scheduler_3 = jsonObject.getString("Pin_d7_Scheduler_3");
                        String Pin_d8_Scheduler_1 = jsonObject.getString("Pin_d8_Scheduler_1");
                        String Pin_d8_Scheduler_2 = jsonObject.getString("Pin_d8_Scheduler_2");
                        String Pin_d8_Scheduler_3 = jsonObject.getString("Pin_d8_Scheduler_3");
                        String Pin_d9_Scheduler_1 = jsonObject.getString("Pin_d9_Scheduler_1");
                        String Pin_d9_Scheduler_2 = jsonObject.getString("Pin_d9_Scheduler_2");
                        String Pin_d9_Scheduler_3 = jsonObject.getString("Pin_d9_Scheduler_3");

                        TimeZone = jsonObject.getString("TimeZone");
                        String Timer1 = jsonObject.getString("Timer1");
                        String Off_Hours = jsonObject.getString("Off_Hours");
                        String Actual_Input = jsonObject.getString("Actual_Input");
                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");

                        String DeviceType = jsonObject.getString("DeviceType");
                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        Dimmer_1 = jsonObject.getString("Dimmer_1");
                        Dimmer_2 = jsonObject.getString("Dimmer_2");
                        Dimmer_3 = jsonObject.getString("Dimmer_3");


                        Status_Dimmer_1 = jsonObject.getString("Status_Dimmer_1");
                        Status_Dimmer_2 = jsonObject.getString("Status_Dimmer_2");
                        Status_Dimmer_3 = jsonObject.getString("Status_Dimmer_3");


                        if (Status_Dimmer_1 == null || Status_Dimmer_1.equalsIgnoreCase("null")) {
                            Status_Dimmer_1 = "0";
                        }
                        if (Status_Dimmer_2 == null || Status_Dimmer_2.equalsIgnoreCase("null")) {
                            Status_Dimmer_2 = "0";
                        }
                        if (Status_Dimmer_3 == null || Status_Dimmer_3.equalsIgnoreCase("null")) {
                            Status_Dimmer_3 = "0";
                        }

                        tv_deviceName.setText(DeviceName);
                        // setPinNames(jsonObject);
                        //  setPinStatus(jsonObject);
                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");


                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);

                        // CheckDeviceOnline();

                        ///for listview
                        ArrayList<Switch> list_switch = new ArrayList<>();
                        if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A1)) {
                            list_switch.add(new Switch(Pin_D1, "PIND1", Status_Pin_d1, Val_Pin_d1, Pin_d1_Scheduler_1, Pin_d1_Scheduler_2, Pin_d1_Scheduler_3, true, "1", Status_Dimmer_1));

                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A2)) {
                            list_switch.add(new Switch(Pin_D1, "PIND1", Status_Pin_d1, Val_Pin_d1, Pin_d1_Scheduler_1, Pin_d1_Scheduler_2, Pin_d1_Scheduler_3, true, "1", Status_Dimmer_1));
                            list_switch.add(new Switch(Pin_D2, "PIND2", Status_Pin_d2, Val_Pin_d2, Pin_d2_Scheduler_1, Pin_d2_Scheduler_2, Pin_d2_Scheduler_3, false, "2", "0"));

                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A4)) {
                            list_switch.add(new Switch(Pin_D1, "PIND1", Status_Pin_d1, Val_Pin_d1, Pin_d1_Scheduler_1, Pin_d1_Scheduler_2, Pin_d1_Scheduler_3, true, "1", Status_Dimmer_1));
                            list_switch.add(new Switch(Pin_D2, "PIND2", Status_Pin_d2, Val_Pin_d2, Pin_d2_Scheduler_1, Pin_d2_Scheduler_2, Pin_d2_Scheduler_3, false, "2", "0"));
                            list_switch.add(new Switch(Pin_D3, "PIND3", Status_Pin_d3, Val_Pin_d3, Pin_d3_Scheduler_1, Pin_d3_Scheduler_2, Pin_d3_Scheduler_3, false, "3", "0"));
                            list_switch.add(new Switch(Pin_D4, "PIND4", Status_Pin_d4, Val_Pin_d4, Pin_d4_Scheduler_1, Pin_d4_Scheduler_2, Pin_d4_Scheduler_3, false, "0", "0"));

                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A8)) {
                            //  list_switch.add(new Switch(Pin_D0,Status_Pin_d0,Val_Pin_d0,Pin_d0_Scheduler_1,Pin_d0_Scheduler_2,Pin_d0_Scheduler_3));
                            list_switch.add(new Switch(Pin_D1, "PIND1", Status_Pin_d1, Val_Pin_d1, Pin_d1_Scheduler_1, Pin_d1_Scheduler_2, Pin_d1_Scheduler_3, true, "1", Status_Dimmer_1));
                            list_switch.add(new Switch(Pin_D2, "PIND2", Status_Pin_d2, Val_Pin_d2, Pin_d2_Scheduler_1, Pin_d2_Scheduler_2, Pin_d2_Scheduler_3, true, "2", Status_Dimmer_2));
                            list_switch.add(new Switch(Pin_D3, "PIND3", Status_Pin_d3, Val_Pin_d3, Pin_d3_Scheduler_1, Pin_d3_Scheduler_2, Pin_d3_Scheduler_3, true, "3", Status_Dimmer_3));
                            list_switch.add(new Switch(Pin_D4, "PIND4", Status_Pin_d4, Val_Pin_d4, Pin_d4_Scheduler_1, Pin_d4_Scheduler_2, Pin_d4_Scheduler_3, false, "0", "0"));
                            list_switch.add(new Switch(Pin_D5, "PIND5", Status_Pin_d5, Val_Pin_d5, Pin_d5_Scheduler_1, Pin_d5_Scheduler_2, Pin_d5_Scheduler_3, false, "0", "0"));
                            list_switch.add(new Switch(Pin_D6, "PIND6", Status_Pin_d6, Val_Pin_d6, Pin_d6_Scheduler_1, Pin_d6_Scheduler_2, Pin_d6_Scheduler_3, false, "0", "0"));
                            list_switch.add(new Switch(Pin_D7, "PIND7", Status_Pin_d7, Val_Pin_d7, Pin_d7_Scheduler_1, Pin_d7_Scheduler_2, Pin_d7_Scheduler_3, false, "0", "0"));
                            list_switch.add(new Switch(Pin_D8, "PIND8", Status_Pin_d8, Val_Pin_d8, Pin_d8_Scheduler_1, Pin_d8_Scheduler_2, Pin_d8_Scheduler_3, false, "0", "0"));
                            //  list_switch.add(new Switch(Pin_D8,Status_Pin_d8,Val_Pin_d8,Pin_d8_Scheduler_1,Pin_d8_Scheduler_2,Pin_d8_Scheduler_3));


                        }


                        Switch_list_adapter adapter = new Switch_list_adapter(myContext, list_switch);
                        listview_switch.setAdapter(adapter);
                        /////


                        CheckDeviceOnline();
                    } else {
                        Toast.makeText(myContext,
                                "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "No data received!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();

                Toast.makeText(myContext,
                        "Error!", Toast.LENGTH_SHORT).show();
                Log.e("Volley Error!", error.toString());


            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
    void getDeviceDetailsB_ApiCall() {
        String tag_string_req = "req_login";

      /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsB_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //utill.hideProgressDialog();
                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String Id = jsonObject.getString("ID");
                        String UserId = jsonObject.getString("UserId");
                        String InsertDate = jsonObject.getString("InsertDate");
                        String DeviceId = jsonObject.getString("DeviceId");
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");


                        String Schedule_1 = jsonObject.getString("Schedule_1");
                        String Schedule_2 = jsonObject.getString("Schedule_2");
                        String Schedule_3 = jsonObject.getString("Schedule_3");


                        String Upper_Depth = jsonObject.getString("Upper_Depth");
                        String Upper_Capacity = jsonObject.getString("Upper_Capacity");

                        String Pump_Mode = jsonObject.getString("Pump_Mode");//same
                        String Tank_type = jsonObject.getString("Tank_type");//same

                        String Overhead_tank_level = jsonObject.getString("Overhead_tank_level");
                        String Lower_Tank_Level = jsonObject.getString("Lower_Tank_Level");

                        String Pump_Status = jsonObject.getString("Pump_Status");
                        String Consumption_Today = jsonObject.getString("Consumption_Today");
                        String Avg_Consumption = jsonObject.getString("Avg_Consumption");
                        String Pump_Flow_Rate = jsonObject.getString("Pump_Flow_Rate");
                        String IsError = jsonObject.getString("IsError");
                        String ErrorMessage = jsonObject.getString("ErrorMessage");

                        String Tank_function_state = jsonObject.getString("Tank_function_state");
                        String Pump_on_time_max = jsonObject.getString("Pump_on_time_max");
                        String Pump_cool_time_min = jsonObject.getString("Pump_cool_time_min");

                        TimeZone = jsonObject.getString("TimeZone");
                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");
                        String DeviceType = jsonObject.getString("DeviceType");
                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");
                        tv_deviceName.setText(DeviceName);
                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);
                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");

                        if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }

                        if (Overhead_tank_level == null || Overhead_tank_level.equals("null") || Overhead_tank_level.equals("")) {
                            tv_overhead_tank_level.setText(UTIL.Key_NoData);
                        } else {
                            tv_overhead_tank_level.setText(Overhead_tank_level);
                        }

                        if (Lower_Tank_Level == null || Lower_Tank_Level.equals("null") || Lower_Tank_Level.equals("")) {
                            tv_lower_tank_level.setText(UTIL.Key_NoData);
                        } else {
                            tv_lower_tank_level.setText(Lower_Tank_Level);
                        }

                        if (Pump_Status == null || Pump_Status.equals("null") || Pump_Status.equals("")) {
                            tv_pump_status.setText(UTIL.Key_NoData);
                        } else {
                            tv_pump_status.setText(Pump_Status);
                        }
                        if (Consumption_Today == null || Consumption_Today.equals("null") || Consumption_Today.equals("")) {
                            tv_Consumption_Today.setText(UTIL.Key_NoData);
                        } else {
                            tv_Consumption_Today.setText(Consumption_Today);
                        }
                        if (Avg_Consumption == null || Avg_Consumption.equals("null") || Avg_Consumption.equals("")) {
                            tv_Average_Consumption.setText(UTIL.Key_NoData);
                        } else {
                            tv_Average_Consumption.setText(Avg_Consumption);
                        }
                        if (Pump_Flow_Rate == null || Pump_Flow_Rate.equals("null") || Pump_Flow_Rate.equals("")) {
                            tv_Pump_flow_Rate.setText(UTIL.Key_NoData);
                        } else {
                            tv_Pump_flow_Rate.setText(Pump_Flow_Rate);
                        }


                        CheckDeviceOnline();


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // pDialog.dismiss();
                //utill.hideProgressDialog();
                Toast.makeText(myContext,
                        "Error!", Toast.LENGTH_SHORT).show();
                Log.e("Volley Error!", error.toString());


            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetailsC_ApiCall() {
        String tag_string_req = "req_login";

       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsC_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String Id = jsonObject.getString("ID");
                        String UserId = jsonObject.getString("UserId");
                        String InsertDate = jsonObject.getString("InsertDate");
                        String DeviceId = jsonObject.getString("DeviceId");
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");
                        TimeZone = jsonObject.getString("TimeZone");

                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");

                        String DeviceType = jsonObject.getString("DeviceType");
                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        String Temperature = jsonObject.getString("Temperature");
                        String Humidity = jsonObject.getString("Humidity");
                        String LPG_Concentration = jsonObject.getString("LPG_Concentration");
                        String CO_Concentration = jsonObject.getString("CO_Concentration");
                        String Gen_Toxin_Concentration = jsonObject.getString("Gen_Toxin_Concentration");
                        String Dust_Particle_Concentration = jsonObject.getString("Dust_Particle_Concentration");
                        String Notification_Alert = jsonObject.getString("Notification_Alert");
                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");
                        String Buzzer_Reset = jsonObject.getString("Buzzer_Reset");
                        String IsError = jsonObject.getString("IsError");
                        String ErrorMessage = jsonObject.getString("ErrorMessage");
                        String Buzzer_State = jsonObject.getString("Buzzer_State");


                        if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }


                        if (Buzzer_State != null && Buzzer_State.equals("1")) {
                            switchButton_buzzer.setChecked(true);
                        } else {
                            switchButton_buzzer.setChecked(false);
                        }


                        if (Temperature == null || Temperature.equals("null") || Temperature.equals("")) {

                            tv_temp.setText(UTIL.Key_NoData);
                        } else {
                            tv_temp.setText(Temperature);
                        }

                        if (Humidity == null || Humidity.equals("null") || Humidity.equals("")) {
                            tv_humidity.setText(UTIL.Key_NoData);
                        } else {
                            tv_humidity.setText(Humidity);
                        }

                        if (LPG_Concentration == null || LPG_Concentration.equals("null") || LPG_Concentration.equals("")) {
                            tv_LPG_Con.setText(UTIL.Key_NoData);
                        } else {
                            tv_LPG_Con.setText(LPG_Concentration);
                        }
                        if (CO_Concentration == null || CO_Concentration.equals("null") || CO_Concentration.equals("")) {
                            tv_CO_Cen.setText(UTIL.Key_NoData);
                        } else {
                            tv_CO_Cen.setText(CO_Concentration);
                        }
                        if (Gen_Toxin_Concentration == null || Gen_Toxin_Concentration.equals("null") || Gen_Toxin_Concentration.equals("")) {
                            tv_Gen_Toxin_Concen.setText(UTIL.Key_NoData);
                        } else {
                            tv_Gen_Toxin_Concen.setText(Gen_Toxin_Concentration);
                        }
                        if (Dust_Particle_Concentration == null || Dust_Particle_Concentration.equals("null") || Dust_Particle_Concentration.equals("")) {
                            tv_Dust_Particle_Con.setText(UTIL.Key_NoData);
                        } else {
                            tv_Dust_Particle_Con.setText(Dust_Particle_Concentration);
                        }


                        tv_deviceName.setText(DeviceName);
                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);
                        CheckDeviceOnline();

                        /////


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  pDialog.dismiss();
                //utill.hideProgressDialog();
                Toast.makeText(myContext,
                        "Error!", Toast.LENGTH_SHORT).show();
                Log.e("Volley Error!", error.toString());


            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetailsD_ApiCall() {
        String tag_string_req = "req_login";

       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsD_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String Id = jsonObject.getString("ID");
                        String UserId = jsonObject.getString("UserId");
                        String InsertDate = jsonObject.getString("InsertDate");
                        String DeviceId = jsonObject.getString("DeviceId");
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");
                        TimeZone = jsonObject.getString("TimeZone");

                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");

                        String DeviceType = jsonObject.getString("DeviceType");
                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        String Temperature = jsonObject.getString("Temperature");
                        String Humidity = jsonObject.getString("Humidity");
                        String Secuirity_Breach = jsonObject.getString("Secuirity_Breach");
                        String Flood_Level = jsonObject.getString("Flood_Level");
                        String Fire_Alarm = jsonObject.getString("Fire_Alarm");
                        String Notification_Alert = jsonObject.getString("Notification_Alert");
                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");
                        String Buzzer_Reset = jsonObject.getString("Buzzer_Reset");
                        String IsError = jsonObject.getString("IsError");
                        String ErrorMessage = jsonObject.getString("ErrorMessage");
                        String Buzzer_State = jsonObject.getString("Buzzer_State");
                        if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }


                        if (Buzzer_State != null && Buzzer_State.equals("1")) {
                            switchButton_buzzer_D.setChecked(true);
                        } else {
                            switchButton_buzzer_D.setChecked(false);
                        }


                        if (Temperature == null || Temperature.equals("null") || Temperature.equals("")) {

                            tv_temp_D.setText(UTIL.Key_NoData);
                        } else {
                            tv_temp_D.setText(Temperature);
                        }

                        if (Humidity == null || Humidity.equals("null") || Humidity.equals("")) {
                            tv_humidity_D.setText(UTIL.Key_NoData);
                        } else {
                            tv_humidity_D.setText(Humidity);
                        }

                        if (Secuirity_Breach == null || Secuirity_Breach.equals("null") || Secuirity_Breach.equals("")) {
                            tv_Security_Breach.setText(UTIL.Key_NoData);
                        } else {
                            tv_Security_Breach.setText(Secuirity_Breach);
                        }
                        if (Flood_Level == null || Flood_Level.equals("null") || Flood_Level.equals("")) {
                            tv_Flood_Level.setText(UTIL.Key_NoData);
                        } else {
                            tv_Flood_Level.setText(Flood_Level);
                        }

                        tv_deviceName.setText(DeviceName);
                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);
                        CheckDeviceOnline();

                        /////


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  pDialog.dismiss();
                //utill.hideProgressDialog();
                Toast.makeText(myContext,
                        "Error!", Toast.LENGTH_SHORT).show();
                Log.e("Volley Error!", error.toString());


            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetail() {
        ArrayList<String> arrayList_device_type_Switch =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
        ArrayList<String> arrayList_device_type_WaterLevelController =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
        ArrayList<String> arrayList_device_type_AirQualityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));
        ArrayList<String> arrayList_device_type_SecurityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));


        if (arrayList_device_type_Switch.contains(Device_Type)) {
            ll_device_A.setVisibility(View.VISIBLE);
            ll_device_B.setVisibility(View.GONE);
            ll_device_C.setVisibility(View.GONE);
            ll_device_D.setVisibility(View.GONE);
            ll_notification.setVisibility(View.GONE);
            action_DeviceStatus.setVisibility(View.VISIBLE);
            action_Device_Scheduler.setVisibility(View.GONE);
            action_DeviceDataLog.setVisibility(View.GONE);
            if (new ConnectionDetector(myContext).isConnectingToInternet()) {

                getDeviceDetailsA_ApiCall();

            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }


        } else if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {
            ll_device_A.setVisibility(View.GONE);
            ll_device_B.setVisibility(View.VISIBLE);
            ll_device_C.setVisibility(View.GONE);
            ll_device_D.setVisibility(View.GONE);
            ll_notification.setVisibility(View.VISIBLE);
            action_DeviceStatus.setVisibility(View.VISIBLE);
            action_Device_Scheduler.setVisibility(View.VISIBLE);
            action_DeviceDataLog.setVisibility(View.GONE);
            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsB_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (arrayList_device_type_AirQualityMonitor.contains(Device_Type)) {

            ll_device_A.setVisibility(View.GONE);
            ll_device_B.setVisibility(View.GONE);
            ll_device_D.setVisibility(View.GONE);
            ll_device_C.setVisibility(View.VISIBLE);
            ll_notification.setVisibility(View.VISIBLE);
            action_DeviceStatus.setVisibility(View.VISIBLE);
            action_Device_Scheduler.setVisibility(View.GONE);
            action_DeviceDataLog.setVisibility(View.GONE);

            if (Device_Type.equals("C1")) {
                ll_temp.setVisibility(View.VISIBLE);
                ll_humidity.setVisibility(View.VISIBLE);
                ll_LPG_Con.setVisibility(View.VISIBLE);
                ll_CO_Cen.setVisibility(View.VISIBLE);
                ll_Gen_Toxin_Concen.setVisibility(View.VISIBLE);
                ll_Dust_Particle_Con.setVisibility(View.VISIBLE);
            } else if (Device_Type.equals("C2")) {
                ll_temp.setVisibility(View.VISIBLE);
                ll_humidity.setVisibility(View.VISIBLE);
                ll_LPG_Con.setVisibility(View.VISIBLE);
                ll_CO_Cen.setVisibility(View.VISIBLE);
                ll_Gen_Toxin_Concen.setVisibility(View.GONE);
                ll_Dust_Particle_Con.setVisibility(View.GONE);
            } else if (Device_Type.equals("C3")) {
                ll_temp.setVisibility(View.VISIBLE);
                ll_humidity.setVisibility(View.VISIBLE);
                ll_LPG_Con.setVisibility(View.VISIBLE);
                ll_CO_Cen.setVisibility(View.GONE);
                ll_Gen_Toxin_Concen.setVisibility(View.GONE);
                ll_Dust_Particle_Con.setVisibility(View.GONE);
            } else if (Device_Type.equals("C4")) {
                ll_temp.setVisibility(View.VISIBLE);
                ll_humidity.setVisibility(View.VISIBLE);
                ll_LPG_Con.setVisibility(View.GONE);
                ll_CO_Cen.setVisibility(View.GONE);
                ll_Gen_Toxin_Concen.setVisibility(View.VISIBLE);
                ll_Dust_Particle_Con.setVisibility(View.VISIBLE);
            }


            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsC_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {

            ll_device_A.setVisibility(View.GONE);
            ll_device_B.setVisibility(View.GONE);
            ll_device_D.setVisibility(View.VISIBLE);
            ll_device_C.setVisibility(View.GONE);
            ll_notification.setVisibility(View.VISIBLE);
            action_DeviceStatus.setVisibility(View.VISIBLE);
            action_Device_Scheduler.setVisibility(View.VISIBLE);
            action_DeviceDataLog.setVisibility(View.GONE);

            if (Device_Type.equals("D1")) {
                ll_temp_D.setVisibility(View.VISIBLE);
                ll_humidity_D.setVisibility(View.VISIBLE);
                ll_FireAlarm.setVisibility(View.VISIBLE);
                ll_SecurityBreach.setVisibility(View.VISIBLE);
                ll_Flood.setVisibility(View.VISIBLE);

                action_DeviceDataLog.setVisibility(View.VISIBLE);


            } else if (Device_Type.equals("D2")) {
                ll_temp_D.setVisibility(View.GONE);
                ll_humidity_D.setVisibility(View.GONE);
                ll_FireAlarm.setVisibility(View.VISIBLE);
                ll_SecurityBreach.setVisibility(View.VISIBLE);
                ll_Flood.setVisibility(View.VISIBLE);

            } else if (Device_Type.equals("D3")) {
                ll_temp_D.setVisibility(View.VISIBLE);
                ll_humidity_D.setVisibility(View.VISIBLE);
                ll_FireAlarm.setVisibility(View.VISIBLE);
                ll_SecurityBreach.setVisibility(View.GONE);
                ll_Flood.setVisibility(View.GONE);

                action_DeviceDataLog.setVisibility(View.VISIBLE);


            } else if (Device_Type.equals("D4")) {
                ll_temp_D.setVisibility(View.GONE);
                ll_humidity_D.setVisibility(View.GONE);
                ll_FireAlarm.setVisibility(View.GONE);
                ll_SecurityBreach.setVisibility(View.VISIBLE);
                ll_Flood.setVisibility(View.GONE);

            } else if (Device_Type.equals("D5")) {
                ll_temp_D.setVisibility(View.GONE);
                ll_humidity_D.setVisibility(View.GONE);
                ll_FireAlarm.setVisibility(View.GONE);
                ll_SecurityBreach.setVisibility(View.GONE);
                ll_Flood.setVisibility(View.VISIBLE);

            }

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsD_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        }


    }

    private void SetBuzzer_Device_C(String DeviceId,
                                    String BuzzerVal) {

        String tag_string_req = "req_login";
       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        utill.showProgressDialog("Kindly wait...");


        String URL;
        URL = UTIL.Domain_Arduino + UTIL.SetBuzzer_Device_C_API + "deviceid=" + DeviceId + "&buzzer=" + BuzzerVal;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server

                        // Toast.makeText(Device_HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    refresh();


                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void SetActive_inactive_Device_C(String DeviceId,
                                             String status) {

        String tag_string_req = "req_login";
        final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();

        String URL;
        URL = UTIL.Domain_Arduino + UTIL.SetActive_Inactive_Device_C_API + "deviceid=" + DeviceId + "&status=" + status;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.cancel();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server

                        Toast.makeText(Device_HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    getDeviceDetail();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void SetBuzzer_Device_D(String DeviceId,
                                    String BuzzerVal) {

       /* String tag_string_req = "req_login";
        final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();*/


        utill.showProgressDialog("Kindly wait...");

        String URL;
        URL = UTIL.Domain_Arduino + UTIL.SetBuzzer_Device_D_API + "deviceid=" + DeviceId + "&buzzer=" + BuzzerVal;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server

                        // Toast.makeText(Device_HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    refresh();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, "");


    }

    private void SetActive_inactive_Device_D(String DeviceId,
                                             String status) {

        String tag_string_req = "req_login";
        final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();

        String URL;
        URL = UTIL.Domain_Arduino + UTIL.SetActive_Inactive_Device_D_API + "deviceid=" + DeviceId + "&status=" + status;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.cancel();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server

                        Toast.makeText(Device_HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    getDeviceDetail();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void SetDeviceActive_inactive(String DeviceId,
                                          String DeviceType, String status) {

        String tag_string_req = "req_login";
      /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        utill.showProgressDialog("Changing device state...");


        String URL;
        URL = UTIL.Domain_Arduino + UTIL.SetActive_Inactive_Device + "DeviceId=" + DeviceId + "&DeviceType=" + DeviceType + "&status=" + status;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server

                        Toast.makeText(Device_HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    getDeviceDetail();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(Device_HomeActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void StartNotificationActivity() {
        Intent i = new Intent(myContext, NotificationActivity.class);
        i.putExtra("DeviceId", DeviceId);
        i.putExtra("DeviceType", Device_Type);
        i.putExtra("UserId", UserId);


        startActivity(i);
    }

    public class Switch_list_adapter extends BaseAdapter {
        ArrayList<Switch> beanArrayList;
        Context context;


        Switch_list_adapter(Context context, ArrayList<Switch> beanArrayList) {
            this.context = context;
            this.beanArrayList = beanArrayList;

        }

        @Override
        public int getCount() {
            return beanArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertview, ViewGroup viewGroup) {

            final Holder holder;
            final String index1 = String.valueOf(i + 1);

            final String PinName = beanArrayList.get(i).getPinName();
            final String PinNum = beanArrayList.get(i).getPinNum();
            final String Status = beanArrayList.get(i).getStatus();
            final String Value = beanArrayList.get(i).getValue();
            final String Scheduler_1 = beanArrayList.get(i).getScheduler_1();
            final String Scheduler_2 = beanArrayList.get(i).getScheduler_2();
            final String Scheduler_3 = beanArrayList.get(i).getScheduler_3();
            final boolean hasDimmer = beanArrayList.get(i).gethasDimmer();
            final String Device_DimmerType = beanArrayList.get(i).getDimmerType();
            final String Current_DimmerProgress = beanArrayList.get(i).getCurrent_DimmerProgress();


            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_switch, null);
                holder.PIN_NAME = (TextView) convertview.findViewById(R.id.PIN_NAME);
                holder.PIN_Status = (TextView) convertview.findViewById(R.id.PIN_Status);
                holder.imgON_PIN = (ImageView) convertview.findViewById(R.id.imgON_PIN);
                holder.imgOFF_PIN = (ImageView) convertview.findViewById(R.id.imgOFF_PIN);
                holder.img_Settings_PIN = (ImageView) convertview.findViewById(R.id.img_Settings_PIN);
                holder.seekbar_Dimmer = convertview.findViewById(R.id.seekbar_Dimmer);
                holder.ll_seekbar_Dimmer = convertview.findViewById(R.id.ll_seekbar_Dimmer);
                holder.card_view_switch_control = convertview.findViewById(R.id.card_view_switch_control);
                holder.switch_btn = convertview.findViewById(R.id.switch_btn);


                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }


            holder.PIN_NAME.setText(PinName);
            if (Status.equals("1")) {
                holder.imgON_PIN.setVisibility(View.GONE);
                holder.imgOFF_PIN.setVisibility(View.VISIBLE);
                holder.PIN_Status.setText("Currently : ON\nClick to OFF");
                holder.switch_btn.setChecked(true);


            } else {

                holder.imgON_PIN.setVisibility(View.VISIBLE);
                holder.imgOFF_PIN.setVisibility(View.GONE);
                holder.PIN_Status.setText("Currently : OFF\nClick to ON");
                holder.switch_btn.setChecked(false);
            }

            if (hasDimmer) {
                holder.ll_seekbar_Dimmer.setVisibility(View.VISIBLE);
                holder.imgON_PIN.setVisibility(View.GONE);
                holder.imgOFF_PIN.setVisibility(View.GONE);
                holder.switch_btn.setVisibility(View.GONE);

                int current_progres = 0;
                try {
                    current_progres = Integer.parseInt(Current_DimmerProgress);
                } catch (Exception e) {
                    e.getMessage();
                }

                holder.seekbar_Dimmer.setProgress(current_progres);
            } else {
                holder.ll_seekbar_Dimmer.setVisibility(View.GONE);
                holder.imgON_PIN.setVisibility(View.GONE);
                holder.imgOFF_PIN.setVisibility(View.GONE);
                holder.switch_btn.setVisibility(View.VISIBLE);
            }
            holder.seekbar_Dimmer.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(IndicatorSeekBar seekBar, int DimmerType, float progressFloat, boolean fromUserTouch) {

                    //  SetDimmer(Device_DimmerType,String.valueOf(progressFloat),DeviceId);

                }

                @Override
                public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {
                    //only callback on discrete series SeekBar type.
                    //  Toast.makeText(getApplicationContext(),"onSectionChanged",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
                    //   Toast.makeText(getApplicationContext(),"onStartTrackingTouch",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                    //    Toast.makeText(getApplicationContext(),"onStopTrackingTouch",Toast.LENGTH_SHORT).show();

                    if (availability.equalsIgnoreCase(UTIL.ONLINE)) {
                        SetDimmer(Device_DimmerType, String.valueOf(seekBar.getProgress()), DeviceId);
                    } else {
                        // CheckDeviceOnline();
                        onResume();
                    }
                }
            });


            holder.switch_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch_set_manullay = true;
                    return false;
                }
            });

            holder.switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (switch_set_manullay) {
                        String val;
                        if (isChecked) {
                            val = "1";
                        } else {
                            val = "0";
                        }
                        if (availability.equalsIgnoreCase(UTIL.ONLINE)) {
                            ON_OFF_PIN(PinNum, val);
                        } else {
                            // Show_DeviceAvailability("DeviceNodeMcu is Offline!", "Please try after some time!");
                            onResume();
                        }
                    }
                    switch_set_manullay = false;

                }
            });

            holder.imgON_PIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (availability.equalsIgnoreCase(UTIL.ONLINE)) {
                        ON_OFF_PIN(PinNum, "1");
                    } else {
                        CheckDeviceOnline();
                    }

                }
            });
            holder.imgOFF_PIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (availability.equalsIgnoreCase(UTIL.ONLINE)) {
                        ON_OFF_PIN(PinNum, "0");
                    } else {
                        CheckDeviceOnline();
                    }

                }
            });


            holder.img_Settings_PIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (availability.equalsIgnoreCase(UTIL.ONLINE)) {
                        Intent i = new Intent(myContext, SchedulerActivity.class);
                        i.putExtra("DeviceId", DeviceId);
                        i.putExtra("DeviceName", DeviceName);
                        i.putExtra("UserId", UserId);
                        i.putExtra("Device_Type", Device_Type);
                        i.putExtra("PinNum", PinNum);
                        startActivity(i);
                    } else {
                        CheckDeviceOnline();
                    }
                }
            });
            return convertview;
        }


        class Holder {
            TextView PIN_NAME;
            TextView PIN_Status;
            ImageView imgON_PIN;
            ImageView imgOFF_PIN;
            ImageView img_Settings_PIN;

            IndicatorSeekBar seekbar_Dimmer;
            LinearLayout ll_seekbar_Dimmer;
            CardView card_view_switch_control;

            SwitchCompat switch_btn;

        }


    }


}
