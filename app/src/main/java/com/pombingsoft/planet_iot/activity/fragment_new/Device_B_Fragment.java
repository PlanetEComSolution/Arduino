package com.pombingsoft.planet_iot.activity.fragment_new;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 3/1/2018.
 */

public  class Device_B_Fragment extends Fragment {
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus;
    TextView tv_overhead_tank_level, tv_lower_tank_level, tv_pump_status, tv_Consumption_Today, tv_Average_Consumption, tv_Pump_flow_Rate;

    String availability = UTIL.OFFLINE;
    //Device B


    String TimeZone, Device_Password;

    String RoleId, RegisteredBy;
    long TotalelapsedSeconds;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     //   View rootView = inflater.inflate(R.layout.layout_device_water_level_controller, container, false);

        View rootView = inflater.inflate(R.layout.layout_device_water_level_controller_1, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);


        tv_overhead_tank_level = rootView.findViewById(R.id.tv_overhead_tank_level);
        tv_lower_tank_level = rootView.findViewById(R.id.tv_lower_tank_level);
        tv_pump_status = rootView.findViewById(R.id.tv_pump_status);
        tv_Consumption_Today = rootView.findViewById(R.id.tv_Consumption_Today);
        tv_Average_Consumption =rootView. findViewById(R.id.tv_Average_Consumption);
        tv_Pump_flow_Rate = rootView.findViewById(R.id.tv_Pump_flow_Rate);

        try {

            Bundle i = getArguments();
            UserId = i.getString("UserId");
            DeviceId = i.getString("DeviceId");
            DeviceLockStatus = i.getString("DeviceLockStatus");
            Device_Type = i.getString("Device_Type");

        } catch (Exception e) {
            e.getMessage();
        }


         return rootView;
    }






    @Override
    public void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            // getPinStatus();
            getDeviceDetailsB_ApiCall();
        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    void getDeviceDetailsB_ApiCall() {
        String tag_string_req = "req_login";
        utill.showProgressDialog("");
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
                utill.hideProgressDialog();
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
                       // status = jsonObject.getString("status");


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
                       // tv_deviceName.setText(DeviceName);
                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);
                       // Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");

                       /* if (IsError != null && IsError.equals("1")) {  //means new notification received
                            img_notification_sign.setVisibility(View.VISIBLE);
                        } else {
                            img_notification_sign.setVisibility(View.INVISIBLE);
                        }*/

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
        } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
            availability = UTIL.ONLINE;
        } else {
            availability = UTIL.OFFLINE;
            Show_DeviceAvailability("Device is Offline!", "Please try after some time!");
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

            alertDialog.show();
        } catch (Exception e) {
            e.getCause();
        }
    }


}