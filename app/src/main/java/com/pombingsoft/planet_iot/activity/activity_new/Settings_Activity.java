package com.pombingsoft.planet_iot.activity.activity_new;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pombingsoft.planet_iot.activity.ActivityWithNavigationMenu;
import com.pombingsoft.planet_iot.activity.NotificationActivity;
import com.pombingsoft.planet_iot.activity.air_quality_monitor.UpdateDevice_C_Activity;
import com.pombingsoft.planet_iot.activity.fragment_new.Device_A_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.Device_B_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.Device_C_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.Device_D_Fragment;
import com.pombingsoft.planet_iot.activity.security_monitor.DataLogActivity;
import com.pombingsoft.planet_iot.activity.security_monitor.UpdateDevice_D_Activity;
import com.pombingsoft.planet_iot.activity.security_monitor.UpdateRangeActivity;
import com.pombingsoft.planet_iot.activity.switch_controller.DeviceDetailActivity;
import com.pombingsoft.planet_iot.activity.switch_controller.UpdateDevice_A_Activity;
import com.pombingsoft.planet_iot.activity.water_level_controller.Device_RunningSchedulerActivity;
import com.pombingsoft.planet_iot.activity.water_level_controller.Update_Device_B_Activity;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Settings_Activity extends AppCompatActivity {
    String UserId, DeviceId, DeviceName;

    Context myContext;
    UTIL utill;


    AlertDialog alertDialog;
    String RoleId, DeviceLockStatus, Device_Type, RegisteredBy;


    String status, TimeZone, Device_Password;
    String availability = UTIL.OFFLINE;

    long TotalelapsedSeconds;


    LinearLayout action_DeviceLockStatus, action_edit,
            action_DeviceStatus, action_deviceDetails, action_Update_Humidity_temp_Range;

    LinearLayout action_delete_device, action_DeviceDataLog, action_Device_Scheduler,
            action_DeviceWifi, action_DeviceTimeZone;

    LinearLayout main_ll_action_edit,
            main_ll_action_Update_Humidity_temp_Range,
            main_ll_action_DeviceLockStatus,
            main_ll_action_DeviceStatus,
            main_ll_deviceDetails,
            main_ll_action_DeviceDataLog,
            main_ll_action_Device_Scheduler,
            main_ll_action_delete_device,
            main_ll_action_DeviceWifi,
            main_ll_action_DeviceTimeZone;

    TextView tv_deviceName, tv_temp_Range, tv_DeviceLockStatus,
            tv_DeviceStatus, tv_Device_ID, tv_DeviceWifi, tv_DeviceTimeZone;



   /* ImageView img_notification_normal, img_notification_sign;
    LinearLayout ll_notification;*/

    String Device_Active_Inactive_status = "0";
    String TimeZone_Device = "";
    String Wifi_Device = "";

    String HumidityRange_Min;
    String HumidityRange_Max;
    String TempRange_Min;
    String TempRange_Max;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myContext = Settings_Activity.this;
        utill = new UTIL(myContext);


        init();
        setFAB();
        setVisibility();
        /*addFragment();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceDetail();

    }

    private void init() {

        main_ll_action_edit = findViewById(R.id.main_ll_action_edit);
        main_ll_action_Update_Humidity_temp_Range = findViewById(R.id.main_ll_action_Update_Humidity_temp_Range);
        main_ll_action_DeviceLockStatus = findViewById(R.id.main_ll_action_DeviceLockStatus);
        main_ll_action_DeviceStatus = findViewById(R.id.main_ll_action_DeviceStatus);
        main_ll_deviceDetails = findViewById(R.id.main_ll_deviceDetails);
        main_ll_action_DeviceDataLog = findViewById(R.id.main_ll_action_DeviceDataLog);
        main_ll_action_Device_Scheduler = findViewById(R.id.main_ll_action_DeviceB_Scheduler);
        main_ll_action_delete_device = findViewById(R.id.main_ll_action_delete_device);
        main_ll_action_DeviceWifi = findViewById(R.id.main_ll_action_DeviceWifi);
        main_ll_action_DeviceTimeZone = findViewById(R.id.main_ll_action_DeviceTimeZone);

        tv_deviceName = findViewById(R.id.deviceName);
        tv_temp_Range = findViewById(R.id.tv_temp_Range);
        tv_DeviceLockStatus = findViewById(R.id.tv_DeviceLockStatus);
        tv_DeviceStatus = findViewById(R.id.tv_DeviceStatus);
        tv_Device_ID = findViewById(R.id.tv_Device_ID);
        tv_DeviceWifi = findViewById(R.id.tv_DeviceWifi);
        tv_DeviceTimeZone = findViewById(R.id.tv_DeviceTimeZone);

        //notification
      /* img_notification_normal = findViewById(R.id.img_notification_normal);
        img_notification_sign = findViewById(R.id.img_notification_sign);
        ll_notification = findViewById(R.id.ll_notification);*/


        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");

            Device_Type = i.getStringExtra("DeviceType");
        } catch (Exception e) {
            e.getMessage();
        }
       /* String str_device_type = UTIL.getDeviceType(Device_Type);
        if (str_device_type.equalsIgnoreCase("A")) {
            ll_notification.setVisibility(View.GONE);
        } else  {
            ll_notification.setVisibility(View.VISIBLE);
        }*/


/*

        img_notification_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartNotificationActivity();

            }
        });
*/


    }

    /*  private void StartNotificationActivity() {
          Intent i = new Intent(myContext, NotificationActivity.class);
          i.putExtra("DeviceId", DeviceId);
          i.putExtra("DeviceType", Device_Type);
          i.putExtra("UserId", UserId);

          startActivity(i);
      }
  */
    private void setFAB() {

        main_ll_action_edit = findViewById(R.id.main_ll_action_edit);
        main_ll_action_Update_Humidity_temp_Range = findViewById(R.id.main_ll_action_Update_Humidity_temp_Range);
        main_ll_action_DeviceLockStatus = findViewById(R.id.main_ll_action_DeviceLockStatus);
        main_ll_action_DeviceStatus = findViewById(R.id.main_ll_action_DeviceStatus);
        main_ll_deviceDetails = findViewById(R.id.main_ll_deviceDetails);
        action_DeviceWifi = findViewById(R.id.action_DeviceWifi);
        action_DeviceTimeZone = findViewById(R.id.action_DeviceTimeZone);

        action_Device_Scheduler = findViewById(R.id.action_DeviceB_Scheduler);
        action_Device_Scheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                }

            }
        });


        action_edit = findViewById(R.id.action_edit);
        action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


        action_delete_device = findViewById(R.id.action_delete_device);
        action_delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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


                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin)
                        || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin))//means user of this device is admin,he originally added the device
                {

                    showDeviceStatus();

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view this device status.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        action_deviceDetails = findViewById(R.id.action_deviceDetails);
        action_deviceDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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


        action_DeviceLockStatus = findViewById(R.id.action_DeviceLockStatus);
        action_DeviceLockStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin) || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin)) {//means user of this device is admin,he originally added the device

                    showDeviceLockStatus();

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view device lock status.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        action_Update_Humidity_temp_Range = findViewById(R.id.action_Update_Humidity_temp_Range);
        action_Update_Humidity_temp_Range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Admin) || RoleId.equalsIgnoreCase(UTIL.Key_RoleId_Sub_admin)) {//means user of this device is admin,he originally added the device


                    // showDeviceDetailsDialog();
                    Intent i = new Intent(myContext, UpdateRangeActivity.class);
                    i.putExtra("UserId", UserId);
                    i.putExtra("DeviceId", DeviceId);
                    i.putExtra("Device_Type", Device_Type);

                    startActivity(i);

                } else {
                    Toast.makeText(myContext, "Sorry! You can not view device lock status.", Toast.LENGTH_SHORT).show();
                }


            }
        });
        action_DeviceWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Settings_Activity.this, ActivityWithNavigationMenu.class);
                    i.putExtra(UTIL.Key_Wifi, "Wifi");
                    startActivity(i);
                    finish();
                }catch (Exception e){
                    e.getCause();
                }
            }
        });
        action_DeviceTimeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Settings_Activity.this, ActivityWithNavigationMenu.class);
                    i.putExtra(UTIL.Key_TimeZone, "TimeZone");
                    startActivity(i);
                    finish();
                }catch (Exception e){
                    e.getCause();
                }
            }
        });
    }

  /*  private void addFragment() {
        Device_A_Fragment a = null;
        Device_B_Fragment b;
        Device_C_Fragment c;
        Device_D_Fragment d;


        // Create fragment and give it an argument for the selected article


        String str_device_type = UTIL.getDeviceType(Device_Type);
        if (str_device_type.equalsIgnoreCase("A")) {

            loadFragment(new Device_A_Fragment());
        } else if (str_device_type.equalsIgnoreCase("B")) {

            loadFragment(new Device_B_Fragment());
        } else if (str_device_type.equalsIgnoreCase("C")) {

            loadFragment(new Device_C_Fragment());
        } else if (str_device_type.equalsIgnoreCase("D")) {

            loadFragment(new Device_D_Fragment());
        }


    }*/

    private void setVisibility() {


        String str_device_type = UTIL.getDeviceType(Device_Type);
        if (str_device_type.equalsIgnoreCase("A")) {

            //  ll_notification.setVisibility(View.GONE);
            main_ll_action_DeviceStatus.setVisibility(View.VISIBLE);
            main_ll_action_Device_Scheduler.setVisibility(View.GONE);
            main_ll_action_DeviceDataLog.setVisibility(View.GONE);
            main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);
        } else if (str_device_type.equalsIgnoreCase("B")) {

            // ll_notification.setVisibility(View.VISIBLE);
            main_ll_action_DeviceStatus.setVisibility(View.VISIBLE);
            main_ll_action_Device_Scheduler.setVisibility(View.VISIBLE);
            main_ll_action_DeviceDataLog.setVisibility(View.GONE);
            main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);
        } else if (str_device_type.equalsIgnoreCase("C")) {

            //  ll_notification.setVisibility(View.VISIBLE);
            main_ll_action_DeviceStatus.setVisibility(View.VISIBLE);
            main_ll_action_Device_Scheduler.setVisibility(View.GONE);
            main_ll_action_DeviceDataLog.setVisibility(View.GONE);
            main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);
        } else if (str_device_type.equalsIgnoreCase("D")) {

            //  ll_notification.setVisibility(View.VISIBLE);
            main_ll_action_DeviceStatus.setVisibility(View.VISIBLE);
            main_ll_action_Device_Scheduler.setVisibility(View.VISIBLE);
            main_ll_action_DeviceDataLog.setVisibility(View.GONE);
            main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);

            if (Device_Type.equals("D1")) {

                main_ll_action_DeviceDataLog.setVisibility(View.VISIBLE);
                main_ll_action_Update_Humidity_temp_Range.setVisibility(View.VISIBLE);

            } else if (Device_Type.equals("D2")) {
                main_ll_action_DeviceDataLog.setVisibility(View.GONE);
                main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);
            } else if (Device_Type.equals("D3")) {


                main_ll_action_DeviceDataLog.setVisibility(View.VISIBLE);
                main_ll_action_Update_Humidity_temp_Range.setVisibility(View.VISIBLE);

            } else if (Device_Type.equals("D4")) {
                main_ll_action_DeviceDataLog.setVisibility(View.GONE);
                main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);
            } else if (Device_Type.equals("D5")) {
                main_ll_action_DeviceDataLog.setVisibility(View.GONE);
                main_ll_action_Update_Humidity_temp_Range.setVisibility(View.GONE);

            }


        }


    }

    /* private void loadFragment(Fragment fragment) {


         //set args
         Bundle args = new Bundle();
         args.putString("UserId", UserId);
         args.putString("DeviceId", DeviceId);
         args.putString("DeviceLockStatus", DeviceLockStatus);
         args.putString("Device_Type", Device_Type);
         fragment.setArguments(args);

         //replace fragment

         FragmentManager fm = getFragmentManager();
         FragmentTransaction fragmentTransaction = fm.beginTransaction();
         fragmentTransaction.replace(R.id.frgmnt_placehodler, fragment);
         fragmentTransaction.commit();
     }
 */
    private void openDialog_DeleteDevice() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Do you want to delete this device?");
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

    public void showDeviceLockStatus() {


        String msg = "";
        String title_msg = "";
        final String lock;
        if (DeviceLockStatus.equalsIgnoreCase("0")) {
            msg = "Device is UNLOCKED";
            title_msg = "Do you want to LOCK device?";
            lock = "1";
        } else {
            msg = "Device is LOCKED";
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
            msg = "Device is ACTIVE";
            title_msg = "Do you want to INACTIVE device?";
            status = "0";
        } else if (Device_Active_Inactive_status == null || Device_Active_Inactive_status.equalsIgnoreCase("0")) {
            msg = "Device is INACTIVE";
            title_msg = "Do you want to ACTIVE device?";
            status = "1";
        } else {
            msg = "Device is INACTIVE";
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
                        ///{"errormsg":"Your Device is Deleted","Status":"1","UserId":"00000002"}
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("Status");
                        if (status.equals("1")) {//request updated at server

                          /*  Toast.makeText(Settings_Activity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent());
                            finish();
*/

                          dialog_resetDevice();



                        } else {
                            //   utill.hideProgressDialog();
                            Toast.makeText(Settings_Activity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                        }

                        //


                    } catch (Exception e) {
                        //  utill.hideProgressDialog();
                        e.printStackTrace();
                        Toast.makeText(Settings_Activity.this, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    utill.hideProgressDialog();

                    // utill.hideProgressDialog();
                    Toast.makeText(Settings_Activity.this, "Volly Error", Toast.LENGTH_SHORT).show();

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

    void getDeviceDetail() {

        String str_device_type = UTIL.getDeviceType(Device_Type);
        if (str_device_type.equalsIgnoreCase("A")) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {

                getDeviceDetailsA_ApiCall();

            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (str_device_type.equalsIgnoreCase("B")) {


            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsB_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (str_device_type.equalsIgnoreCase("C")) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsC_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (str_device_type.equalsIgnoreCase("D")) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsD_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        }


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

                        Toast.makeText(Settings_Activity.this, "Done!", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(Settings_Activity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    getDeviceDetail();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(Settings_Activity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(Settings_Activity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetailsA_ApiCall() {
        String tag_string_req = "req_login";

        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        utill.showProgressDialog("");

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
                utill.hideProgressDialog();

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");


                        TimeZone = jsonObject.getString("TimeZone");
                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");


                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");


                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");


                        TimeZone_Device = jsonObject.getString("TimeZone_Device");
                        Wifi_Device = jsonObject.getString("Wifi_Device");
                        String TimeZone_Usr = jsonObject.getString("TimeZone");
                        String Wifi_Usr = jsonObject.getString("Wifi_Usr");


                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);


                    } else {
                        Toast.makeText(myContext,
                                "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "No data received!", Toast.LENGTH_SHORT).show();
                }
                setData();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();
                utill.hideProgressDialog();
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
        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsB_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        utill.showProgressDialog("");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                utill.hideProgressDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");


                        String IsError = jsonObject.getString("IsError");
                        TimeZone = jsonObject.getString("TimeZone");
                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");
                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        TimeZone_Device = jsonObject.getString("TimeZone_Device");
                        Wifi_Device = jsonObject.getString("Wifi_Device");
                        String TimeZone_Usr = jsonObject.getString("TimeZone");
                        String Wifi_Usr = jsonObject.getString("Wifi_Usr");


                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);
                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");

                        /*if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }*/


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }
                setData();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();

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


        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsC_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }
        utill.showProgressDialog("");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");
                        TimeZone = jsonObject.getString("TimeZone");


                        TimeZone_Device = jsonObject.getString("TimeZone_Device");
                        Wifi_Device = jsonObject.getString("Wifi_Device");
                        String TimeZone_Usr = jsonObject.getString("TimeZone");
                        String Wifi_Usr = jsonObject.getString("Wifi_Usr");


                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");

                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");
                        String IsError = jsonObject.getString("IsError");


                      /*  if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }
*/


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                setData();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
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


        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsD_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }

        utill.showProgressDialog("");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();

                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        DeviceName = jsonObject.getString("DeviceName");
                        status = jsonObject.getString("status");
                        TimeZone = jsonObject.getString("TimeZone");


                        TimeZone_Device = jsonObject.getString("TimeZone_Device");
                        Wifi_Device = jsonObject.getString("Wifi_Device");
                        String TimeZone_Usr = jsonObject.getString("TimeZone");
                        String Wifi_Usr = jsonObject.getString("Wifi_Usr");


                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");

                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");
                        String IsError = jsonObject.getString("IsError");
                       /* if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }
                        /////*/
                        HumidityRange_Min = jsonObject.getString("HumidityRange_Min");
                        HumidityRange_Max = jsonObject.getString("HumidityRange_Max");
                        TempRange_Min = jsonObject.getString("TempRange_Min");
                        TempRange_Max = jsonObject.getString("TempRange_Max");


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // JSON error

                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                setData();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  pDialog.dismiss();

                utill.hideProgressDialog();
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


    void setData() {
        tv_deviceName.setText(DeviceName);


        String temp_range = "Temp: " + TempRange_Min + "-" + TempRange_Max + " °C";
        String hum_range = "Humidity: " + HumidityRange_Min + "-" + HumidityRange_Max + " %";


        tv_temp_Range.setText(temp_range + "\n" + hum_range);

        String Device_status = "";
        if (Device_Active_Inactive_status.equals("1")) {
            Device_status = "ACTIVE";
        } else if (Device_Active_Inactive_status.equals("null") || Device_Active_Inactive_status == null || Device_Active_Inactive_status.equalsIgnoreCase("0")) {
            Device_status = "INACTIVE";
        }

        String Device_Lock_status = "";
        if (DeviceLockStatus.equalsIgnoreCase("0")) {
            Device_Lock_status = "UNLOCKED";
        } else {
            Device_Lock_status = "LOCKED";
        }

        tv_DeviceLockStatus.setText(Device_Lock_status);
        tv_DeviceStatus.setText(Device_status);
        tv_Device_ID.setText(DeviceId);
        if ((Wifi_Device != null) && (!Wifi_Device.equals("null"))) {
            tv_DeviceWifi.setText(Wifi_Device);
        }
        else {
            tv_DeviceWifi.setText("Not available");
        }
        if ((TimeZone_Device != null) && (!TimeZone_Device.equals("null"))) {
            tv_DeviceTimeZone.setText(TimeZone_Device);
        }
        else{
            tv_DeviceTimeZone.setText("Not available");
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void dialog_resetDevice() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings_Activity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Device deleted successfully!");
        message.setText("Please restart device!");
        positiveBtn.setText("Ok");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), ActivityWithNavigationMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

}
