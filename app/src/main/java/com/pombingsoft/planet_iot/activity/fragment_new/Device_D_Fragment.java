package com.pombingsoft.planet_iot.activity.fragment_new;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by Admin on 3/1/2018.
 */

public class Device_D_Fragment extends Fragment {
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus;


    TextView tv_temp_D,
            tv_humidity_D,
            tv_FireAlarm,
            tv_Security_Breach,
            tv_Flood_Level;

    SwitchCompat switchButton_buzzer_D;

    LinearLayout ll_temp_D, ll_humidity_D, ll_FireAlarm, ll_SecurityBreach, ll_Flood;


    String availability = UTIL.OFFLINE;
    //Device B

    ImageView imgvw_Flood;
    String TimeZone, Device_Password;

    String RoleId, RegisteredBy;
    long TotalelapsedSeconds;
    boolean switch_set_manullay = false;
    boolean buzzer_switch_set_manullay = false;
    ImageView imgvw;
    String Flood_state = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_device_security_monitor_1, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);


        //Device D
        tv_temp_D = rootView.findViewById(R.id.tv_temp_D);
        tv_humidity_D = rootView.findViewById(R.id.tv_humidity_D);
        tv_FireAlarm = rootView.findViewById(R.id.tv_FireAlarm);
        tv_Security_Breach = rootView.findViewById(R.id.tv_Security_Breach);
        tv_Flood_Level = rootView.findViewById(R.id.tv_Flood_Level);

        imgvw_Flood = rootView.findViewById(R.id.imgvw_Flood);

        switchButton_buzzer_D = rootView.findViewById(R.id.switchButton_buzzer_D);

        ll_temp_D = rootView.findViewById(R.id.ll_temp_D);
        ll_humidity_D = rootView.findViewById(R.id.ll_humidity_D);
        ll_FireAlarm = rootView.findViewById(R.id.ll_FireAlarm);
        ll_SecurityBreach = rootView.findViewById(R.id.ll_SecurityBreach);
        ll_Flood = rootView.findViewById(R.id.ll_Flood);
        imgvw = rootView.findViewById(R.id.imgvw_fire_siren_icon);
        //Device D


        try {

            Bundle i = getArguments();
            UserId = i.getString("UserId");
            DeviceId = i.getString("DeviceId");
            DeviceLockStatus = i.getString("DeviceLockStatus");
            Device_Type = i.getString("Device_Type");

        } catch (Exception e) {
            e.getMessage();
        }

        setVisibility();
        init();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {

            getDeviceDetailsD_ApiCall();
        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setVisibility() {

        if (Device_Type.equals("D1")) {
            ll_temp_D.setVisibility(View.VISIBLE);
            ll_humidity_D.setVisibility(View.VISIBLE);
            ll_FireAlarm.setVisibility(View.GONE);
            ll_SecurityBreach.setVisibility(View.VISIBLE);
            ll_Flood.setVisibility(View.VISIBLE);


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


    }

    private void init() {
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
                    // Show_DeviceAvailability("Device is Offline!", "Please try after some time!");
                    onResume();
                }


            }


        });


    }

    void getDeviceDetailsD_ApiCall() {
        String tag_string_req = "req_login";

       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        utill.showProgressDialog("");

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

                utill.hideProgressDialog();
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
                        //  status = jsonObject.getString("status");
                        TimeZone = jsonObject.getString("TimeZone");

                        RoleId = jsonObject.getString("RoleId").trim();
                        Device_Password = jsonObject.getString("Device_Password");

                        String DeviceType = jsonObject.getString("DeviceType");
                        DeviceLockStatus = jsonObject.getString("LocakStatus");
                        RegisteredBy = jsonObject.getString("RegisteredBy");

                        String Temperature = jsonObject.getString("Temperature");
                        String Humidity = jsonObject.getString("Humidity");
                        String Secuirity_Breach = jsonObject.getString("Secuirity_Breach");

                        Flood_state = jsonObject.getString("Flood_Level");
                        String Fire_state = jsonObject.getString("Fire_Alarm");

                        String Notification_Alert = jsonObject.getString("Notification_Alert");
                        //     Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");
                        //    String Buzzer_Reset = jsonObject.getString("Buzzer_Reset");

                        String IsError = jsonObject.getString("IsError");
                        String ErrorMessage = jsonObject.getString("ErrorMessage");
                        String Buzzer_State = jsonObject.getString("Buzzer_State");// it will be used for sensor_state


                        if (Fire_state != null && Fire_state.equals("1")) {
                            switchButton_buzzer_D.setChecked(true);
                        } else {
                            switchButton_buzzer_D.setChecked(false);
                        }


                        if (Temperature == null || Temperature.equals("null") || Temperature.equals("")) {

                            tv_temp_D.setText(UTIL.Key_NoData);
                        } else {
                            tv_temp_D.setText(Temperature + " °C");
                        }

                        if (Humidity == null || Humidity.equals("null") || Humidity.equals("")) {
                            tv_humidity_D.setText(UTIL.Key_NoData);
                        } else {
                            tv_humidity_D.setText(Humidity + " %");
                        }

                        if (Secuirity_Breach == null || Secuirity_Breach.equals("null") || Secuirity_Breach.equals("")) {
                            tv_Security_Breach.setText(UTIL.Key_NoData);
                        } else {
                            tv_Security_Breach.setText(Secuirity_Breach);
                        }


                        if (UTIL.is_Device_With_FLOOD_Sensor(Device_Type)) {

                            if (Flood_state.equalsIgnoreCase("1") || Flood_state.equalsIgnoreCase("2")) {
                                ll_Flood.setVisibility(View.VISIBLE);
                                tv_Flood_Level.setText("FLOOD WARNING");
                                tv_Flood_Level.setVisibility(View.VISIBLE);
                                //   imgvw_Flood.setImageResource(R.drawable.danger);
                            } else {
                                tv_Flood_Level.setText("ALL IS WELL");
                                // imgvw_Flood.setImageResource(R.drawable.okay);
                                ll_Flood.setVisibility(View.GONE);
                            }

                        }
                        if (Device_Type.equalsIgnoreCase("D5")) {

                            ll_Flood.setVisibility(View.VISIBLE);
                            if (Flood_state.equalsIgnoreCase("1") || Flood_state.equalsIgnoreCase("2")) {
                                tv_Flood_Level.setText("FLOOD WARNING");
                                // imgvw_Flood.setImageResource(R.drawable.danger);
                            } else {
                                tv_Flood_Level.setText("ALL IS WELL");
                                // imgvw_Flood.setImageResource(R.drawable.okay);
                            }


                        }


                        if (UTIL.is_Device_With_FIRE_Sensor(Device_Type)) {

                            if (Device_Type.equals("D2") || Device_Type.equals("D3")) {
                                if (Fire_state == null || Fire_state.equalsIgnoreCase("0") || Fire_state.equalsIgnoreCase("null")) {
                                    ll_FireAlarm.setVisibility(View.GONE);
                                } else {
                                    ll_FireAlarm.setVisibility(View.VISIBLE);
                                    tv_FireAlarm.setText("FIRE WARNING");
                                    // tv_FireAlarm.setVisibility(View.GONE);
                                }

                            }
                        }


                       /* if (Flood_Level == null || Flood_Level.equals("null") || Flood_Level.equals("")) {
                            tv_Flood_Level.setText(UTIL.Key_NoData);
                        } else {
                            tv_Flood_Level.setText(Flood_Level);
                        }*/


                        //tv_deviceName.setText(DeviceName);
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

    public void CheckDeviceOnline() {

        if (TotalelapsedSeconds == UTIL.Error_val) {
            availability = UTIL.ERROR;
            Show_DeviceAvailability("Please restart device!", "Try after few moments!");
        } else if (TotalelapsedSeconds <= UTIL.Second_Online_For_Device_D) {
            availability = UTIL.ONLINE;
            blink();
        } else {
            availability = UTIL.OFFLINE;
            Show_DeviceAvailability("Device is Offline!", "Please try after some time!");
            blink();
        }

    }

    public void Show_DeviceAvailability(String title_str, String msg_str) {

        if (alertDialog != null && alertDialog.isShowing()) return;
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
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
                    } catch (Exception e) {
                        e.getCause();
                    }
                    // finish();

                }
            });
            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        e.getCause();
                    }
                }
            });


            alertDialog = dialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);

            alertDialog.show();
        } catch (Exception e) {
            e.getCause();
        }
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

                        Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }

                    //  refresh();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, "");


    }

    public void blink() {

       /* final AnimationDrawable drawable = new AnimationDrawable();
        final Handler handler = new Handler();

        drawable.addFrame(new ColorDrawable(Color.RED), 400);
        drawable.addFrame(new ColorDrawable(Color.GREEN), 400);
        drawable.setOneShot(false);

        imgvw.setBackgroundDrawable(drawable);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.start();
            }
        }, 100);*/

        try {
            Glide.with(getActivity())
                    .load(R.drawable.fire_giphy)
                    .into(imgvw);
        } catch (Exception e) {
            e.getMessage();
        }


        if (Flood_state.equalsIgnoreCase("1") || Flood_state.equalsIgnoreCase("2")) {
            Glide.with(getActivity())
                    .load(R.drawable.flood_giphy)
                    .into(imgvw_Flood);
            tv_Flood_Level.setTextColor(getResources().getColor(R.color.red_1));
        } else {
            imgvw_Flood.setImageResource(R.drawable.okay);
            tv_Flood_Level.setTextColor(getResources().getColor(R.color.green_1));
        }


    }

}