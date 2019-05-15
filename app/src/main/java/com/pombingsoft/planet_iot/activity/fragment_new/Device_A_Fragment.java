package com.pombingsoft.planet_iot.activity.fragment_new;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.switch_controller.SchedulerActivity;
import com.pombingsoft.planet_iot.model.Switch;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;
import com.warkiz.widget.IndicatorSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Admin on 3/1/2018.
 */

public class Device_A_Fragment extends Fragment {
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus, DimmerType, PinNum;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ListView listview_switch;

    String status, TimeZone, Device_Password, Dimmer_1, Dimmer_2, Dimmer_3;
    String Status_Dimmer_1, Status_Dimmer_2, Status_Dimmer_3;
    String RoleId, RegisteredBy;
    long TotalelapsedSeconds;
    String availability = UTIL.OFFLINE;
    boolean switch_set_manullay = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_device_switch, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);

        try {

            Bundle i = getArguments();
            UserId = i.getString("UserId");
            DeviceId = i.getString("DeviceId");
            DeviceLockStatus = i.getString("DeviceLockStatus");
            Device_Type = i.getString("Device_Type");

        } catch (Exception e) {
            e.getMessage();
        }
        listview_switch = rootView.findViewById(R.id.listview_switch);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            // getPinStatus();
            getDeviceDetailsA_ApiCall();
        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    void getDeviceDetailsA_ApiCall() {
        String tag_string_req = "req_login";

    /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        utill.showProgressDialog("");
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
                utill.hideProgressDialog();

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

                        //  tv_deviceName.setText(DeviceName);
                        // setPinNames(jsonObject);
                        //  setPinStatus(jsonObject);
                        //    Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");


                        String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                        String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        String currentTime = utill.getCurrentTime(TimeZone);
                        TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);

                        // CheckDeviceOnline();

                        ///for listview
                        ArrayList<Switch> list_switch = new ArrayList<>();

                        if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A)) {
                            list_switch.add(new Switch(Pin_D1, "PIND1", Status_Pin_d1, Val_Pin_d1, Pin_d1_Scheduler_1, Pin_d1_Scheduler_2, Pin_d1_Scheduler_3, false, "1", Status_Dimmer_1));

                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A1)) {
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

    private void SetDimmer(String DimmerType,
                           String DimmerValue,
                           String DeviceId) {


        String tag_string_req = "req_login";
        // utill.showProgressDialog("Kindly wait...");


        utill.showProgressDialog("");


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

                        utill.hideProgressDialog();


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


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();


                Toast.makeText(myContext, "Volly error: ", Toast.LENGTH_SHORT).show();

            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


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

                            Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        //  pDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //  refresh();


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    // pDialog.dismiss();
                    Toast.makeText(myContext, "Volly Error", Toast.LENGTH_SHORT).show();

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
                            // Show_DeviceAvailability("Device is Offline!", "Please try after some time!");
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