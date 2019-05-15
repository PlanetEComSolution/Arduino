package com.pombingsoft.planet_iot.activity.fragment_new;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Admin on 3/1/2018.
 */

public class Device_C_Fragment extends Fragment {
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus, DimmerType, PinNum;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    TextView tv_temp,
            tv_humidity,
            tv_LPG_Con,
            tv_CO_Cen,
            tv_Gen_Toxin_Concen,
            tv_Dust_Particle_Con;

    LinearLayout ll_temp, ll_humidity, ll_LPG_Con, ll_CO_Cen, ll_Gen_Toxin_Concen, ll_Dust_Particle_Con;
    SwitchCompat switchButton_buzzer;
    boolean switch_set_manullay = false;
    boolean buzzer_switch_set_manullay = false;
    String TimeZone, Device_Password;
    String RoleId, RegisteredBy;
    long TotalelapsedSeconds;
    String availability = UTIL.OFFLINE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_device_air_quality_monitor_1, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);


        //Device C
        tv_temp = rootView.findViewById(R.id.tv_temp);
        tv_humidity = rootView.findViewById(R.id.tv_humidity);
        tv_LPG_Con = rootView.findViewById(R.id.tv_LPG_Con);
        tv_CO_Cen = rootView.findViewById(R.id.tv_CO_Cen);
        tv_Gen_Toxin_Concen = rootView.findViewById(R.id.tv_Gen_Toxin_Concen);
        tv_Dust_Particle_Con = rootView.findViewById(R.id.tv_Dust_Particle_Con);
        ll_temp = rootView.findViewById(R.id.ll_temp);
        ll_humidity = rootView.findViewById(R.id.ll_humidity);
        ll_LPG_Con = rootView.findViewById(R.id.ll_LPG_Con);
        ll_CO_Cen = rootView.findViewById(R.id.ll_CO_Cen);
        ll_Gen_Toxin_Concen = rootView.findViewById(R.id.ll_Gen_Toxin_Concen);
        ll_Dust_Particle_Con = rootView.findViewById(R.id.ll_Dust_Particle_Con);
        switchButton_buzzer = rootView.findViewById(R.id.switchButton_buzzer);
        //Device C

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
            // getPinStatus();
            getDeviceDetailsC_ApiCall();
        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private void init(){
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

    }

    private void setVisibility() {


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


    }

    void getDeviceDetailsC_ApiCall() {
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
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsC_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

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
                        //    status = jsonObject.getString("status");
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
                        //  Device_Active_Inactive_status = jsonObject.getString("Device_Active_Inactive");
                        String Buzzer_Reset = jsonObject.getString("Buzzer_Reset");
                        String IsError = jsonObject.getString("IsError");
                        String ErrorMessage = jsonObject.getString("ErrorMessage");
                        String Buzzer_State = jsonObject.getString("Buzzer_State");


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


                        //  tv_deviceName.setText(DeviceName);
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

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

}