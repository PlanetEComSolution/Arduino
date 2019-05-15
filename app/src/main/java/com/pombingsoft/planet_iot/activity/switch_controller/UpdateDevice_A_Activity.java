package com.pombingsoft.planet_iot.activity.switch_controller;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class UpdateDevice_A_Activity extends AppCompatActivity {
    Button btnUpdate, btnCancel;

    EditText et_DeviceName, et_DevicePass,et_PIN_D1_Name, et_PIN_D2_Name, et_PIN_D3_Name,
            et_PIN_D4_Name,
            et_PIN_D5_Name,
            et_PIN_D6_Name,
            et_PIN_D7_Name,
            et_PIN_D8_Name;

    Context myContext;
    UTIL utill;
    String DeviceId, UserId, Device_Type,timeZoneName,Device_TimeZoneID,Pin_D0,Pin_D9;
    TextInputLayout et_TILDeviceName,et_TILDevicePass, et_TIL_PIN_D1_Name, et_TIL_PIN_D2_Name, et_TIL_PIN_D3_Name,
            et_TIL_PIN_D4_Name,
            et_TIL_PIN_D5_Name, et_TIL_PIN_D6_Name,
            et_TIL_PIN_D7_Name, et_TIL_PIN_D8_Name;


    LinearLayout ll_PinD1, ll_PinD2, ll_PinD3, ll_PinD4, ll_PinD5, ll_PinD6, ll_PinD7, ll_PinD8,
            ll_deviceName, ll_devicePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);


        myContext = UpdateDevice_A_Activity.this;
        utill = new UTIL(myContext);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        et_TILDeviceName = findViewById(R.id.et_TILDeviceName);
        et_TILDevicePass = findViewById(R.id.et_TILDevicePass);

        et_TIL_PIN_D1_Name = findViewById(R.id.et_TIL_PIN_D1_Name);
        et_TIL_PIN_D2_Name = findViewById(R.id.et_TIL_PIN_D2_Name);
        et_TIL_PIN_D3_Name = findViewById(R.id.et_TIL_PIN_D3_Name);
        et_TIL_PIN_D4_Name = findViewById(R.id.et_TIL_PIN_D4_Name);
        et_TIL_PIN_D5_Name = findViewById(R.id.et_TIL_PIN_D5_Name);
        et_TIL_PIN_D6_Name = findViewById(R.id.et_TIL_PIN_D6_Name);
        et_TIL_PIN_D7_Name = findViewById(R.id.et_TIL_PIN_D7_Name);
        et_TIL_PIN_D8_Name = findViewById(R.id.et_TIL_PIN_D8_Name);



       /* et_TIL_PIN_D1_Name.setVisibility(View.GONE);
        et_TIL_PIN_D2_Name.setVisibility(View.GONE);
        et_TIL_PIN_D3_Name.setVisibility(View.GONE);
        et_TIL_PIN_D4_Name.setVisibility(View.GONE);
        et_TIL_PIN_D5_Name.setVisibility(View.GONE);
        et_TIL_PIN_D6_Name.setVisibility(View.GONE);
        et_TIL_PIN_D7_Name.setVisibility(View.GONE);
        et_TIL_PIN_D8_Name.setVisibility(View.GONE);
*/

        et_DeviceName = findViewById(R.id.et_DeviceName);
        et_DevicePass = findViewById(R.id.et_DevicePass);

        et_PIN_D1_Name = findViewById(R.id.et_PIN_D1_Name);
        et_PIN_D2_Name = findViewById(R.id.et_PIN_D2_Name);
        et_PIN_D3_Name = findViewById(R.id.et_PIN_D3_Name);
        et_PIN_D4_Name = findViewById(R.id.et_PIN_D4_Name);
        et_PIN_D5_Name = findViewById(R.id.et_PIN_D5_Name);
        et_PIN_D6_Name = findViewById(R.id.et_PIN_D6_Name);
        et_PIN_D7_Name = findViewById(R.id.et_PIN_D7_Name);
        et_PIN_D8_Name = findViewById(R.id.et_PIN_D8_Name);

        ll_PinD1 = findViewById(R.id.ll_PinD1);
        ll_PinD2 = findViewById(R.id.ll_PinD2);
        ll_PinD3  = findViewById(R.id.ll_PinD3);
        ll_PinD4  = findViewById(R.id.ll_PinD4);
        ll_PinD5 = findViewById(R.id.ll_PinD5);
        ll_PinD6 = findViewById(R.id.ll_PinD6);
        ll_PinD7  = findViewById(R.id.ll_PinD7);
        ll_PinD8 = findViewById(R.id.ll_PinD8);

        ll_PinD1.setVisibility(View.GONE);
        ll_PinD2.setVisibility(View.GONE);
        ll_PinD3 .setVisibility(View.GONE);
        ll_PinD4 .setVisibility(View.GONE);
        ll_PinD5.setVisibility(View.GONE);
        ll_PinD6.setVisibility(View.GONE);
        ll_PinD7 .setVisibility(View.GONE);
        ll_PinD8.setVisibility(View.GONE);


        Intent i = getIntent();
        UserId = i.getStringExtra("UserId");
        DeviceId = i.getStringExtra("DeviceId");
        Device_Type = i.getStringExtra("Device_Type");

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {

                getDeviceDetailsA_ApiCall();

        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String DeviceName = et_DeviceName.getText().toString().trim();
                String DevicePass = et_DevicePass.getText().toString().trim();

                String PIN_D1_Name = et_PIN_D1_Name.getText().toString().trim();
                String PIN_D2_Name = et_PIN_D2_Name.getText().toString().trim();
                String PIN_D3_Name = et_PIN_D3_Name.getText().toString().trim();
                String PIN_D4_Name = et_PIN_D4_Name.getText().toString().trim();
                String PIN_D5_Name = et_PIN_D5_Name.getText().toString().trim();
                String PIN_D6_Name = et_PIN_D6_Name.getText().toString().trim();
                String PIN_D7_Name = et_PIN_D7_Name.getText().toString().trim();
                String PIN_D8_Name = et_PIN_D8_Name.getText().toString().trim();


                if (DeviceName.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter Device Name!", Toast.LENGTH_SHORT)
                            .show();
                }

                else if (DevicePass.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new device Password!", Toast.LENGTH_SHORT)
                            .show();
                }

                else if (PIN_D1_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 1", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D2_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 2", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D3_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 3", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D4_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 4", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D5_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 5", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D6_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 6", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D7_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 7", Toast.LENGTH_SHORT)
                            .show();
                } else if (PIN_D8_Name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter new name for Switch 8", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        UpdateDeviceA(DeviceId,DeviceName,Device_Type,DevicePass,
                                timeZoneName,Device_TimeZoneID,Pin_D0,
                                PIN_D1_Name,PIN_D2_Name,PIN_D3_Name,PIN_D4_Name,
                                PIN_D5_Name, PIN_D6_Name, PIN_D7_Name,
                                PIN_D8_Name,Pin_D9);

                    } else {
                        Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();
                    }

                }

            }
        });
    }




    private void UpdateDeviceA(String DeviceId,
                              String DeviceName,
                              String DeviceType,
                              String Device_Password,
                              String timezoneName,
                              String timezoneID,
                              String Pin_D0,
                              String Pin_D1,
                              String Pin_D2,
                              String Pin_D3,
                              String Pin_D4,
                              String Pin_D5,
                              String Pin_D6,
                              String Pin_D7,
                              String Pin_D8,
                              String Pin_D9) {
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
     //   TimeZone tz = TimeZone.getDefault();


        String tag_string_req = "req_login";
        utill.showProgressDialog("Updating device details...");
        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("UserId", Uid);
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("DeviceType", DeviceType);
        params.put("Device_Password", Device_Password);

        params.put("TimeZone", timezoneName);
        params.put("TimeZoneId", timezoneID);
        params.put("Pin_D0", Pin_D0);
        params.put("Pin_D1", Pin_D1);
        params.put("Pin_D2", Pin_D2);
        params.put("Pin_D3", Pin_D3);
        params.put("Pin_D4", Pin_D4);
        params.put("Pin_D5", Pin_D5);
        params.put("Pin_D6", Pin_D6);
        params.put("Pin_D7", Pin_D7);
        params.put("Pin_D8", Pin_D8);
        params.put("Pin_D9", Pin_D9);


        URL = UTIL.Domain_Arduino + UTIL.UpdateDeviceDetails_A_API+ "DeviceId=" + DeviceId;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject =response;
                            String status = jsonObject.getString("Status");
                            if (status.equals("1")) {//request updated at server
                                Toast.makeText(getApplicationContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                utill.hideProgressDialog();
                                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            utill.hideProgressDialog();
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


    void getDeviceDetailsA_ApiCall() {


        String tag_string_req = "req_login";
        utill.showProgressDialog("Getting device...");

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


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String Id = jsonObject.getString("ID");
                        String UserId = jsonObject.getString("UserId");
                        String InsertDate = jsonObject.getString("InsertDate");
                        String DeviceId = jsonObject.getString("DeviceId");
                        String DeviceName = jsonObject.getString("DeviceName");


                        String  Device_Password = jsonObject.getString("Device_Password");
                        String status = jsonObject.getString("status");

                        Pin_D0 = jsonObject.getString("Pin_D0");
                        String Pin_D1 = jsonObject.getString("Pin_D1");
                        String Pin_D2 = jsonObject.getString("Pin_D2");
                        String Pin_D3 = jsonObject.getString("Pin_D3");
                        String Pin_D4 = jsonObject.getString("Pin_D4");
                        String Pin_D5 = jsonObject.getString("Pin_D5");
                        String Pin_D6 = jsonObject.getString("Pin_D6");
                        String Pin_D7 = jsonObject.getString("Pin_D7");
                        String Pin_D8 = jsonObject.getString("Pin_D8");
                         Pin_D9 = jsonObject.getString("Pin_D9");


                        String DeviceType = jsonObject.getString("DeviceType");
                        String DeviceLockStatus = jsonObject.getString("LocakStatus");
                        String RegisteredBy = jsonObject.getString("RegisteredBy");
                        String Dimmer_1 = jsonObject.getString("Dimmer_1");
                        String Dimmer_2 = jsonObject.getString("Dimmer_2");
                        String Dimmer_3 = jsonObject.getString("Dimmer_3");

                        timeZoneName = jsonObject.getString("TimeZone");
                         Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                        et_DeviceName.setText(DeviceName);
                        et_DevicePass.setText(Device_Password);

                        et_PIN_D1_Name.setText(Pin_D1);
                        et_PIN_D2_Name.setText(Pin_D2);
                        et_PIN_D3_Name.setText(Pin_D3);
                        et_PIN_D4_Name.setText(Pin_D4);
                        et_PIN_D5_Name.setText(Pin_D5);
                        et_PIN_D6_Name.setText(Pin_D6);
                        et_PIN_D7_Name.setText(Pin_D7);
                        et_PIN_D8_Name.setText(Pin_D8);


                        et_TILDeviceName.setHint("Device Name");
                        et_TILDevicePass.setHint("Device Password");
                        et_TIL_PIN_D1_Name.setHint("Switch 1");
                        et_TIL_PIN_D2_Name.setHint("Switch 2");
                        et_TIL_PIN_D3_Name.setHint("Switch 3");
                        et_TIL_PIN_D4_Name.setHint("Switch 4");
                        et_TIL_PIN_D5_Name.setHint("Switch 5");
                        et_TIL_PIN_D6_Name.setHint("Switch 6");
                        et_TIL_PIN_D7_Name.setHint("Switch 7");
                        et_TIL_PIN_D8_Name.setHint("Switch 8");


                        if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A) ||DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A1)) {


                            ll_PinD1.setVisibility(View.VISIBLE);
                            ll_PinD2.setVisibility(View.GONE);
                            ll_PinD3 .setVisibility(View.GONE);
                            ll_PinD4 .setVisibility(View.GONE);
                            ll_PinD5.setVisibility(View.GONE);
                            ll_PinD6.setVisibility(View.GONE);
                            ll_PinD7 .setVisibility(View.GONE);
                            ll_PinD8.setVisibility(View.GONE);





                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A2)) {


                            ll_PinD1.setVisibility(View.VISIBLE);
                            ll_PinD2.setVisibility(View.VISIBLE);
                            ll_PinD3 .setVisibility(View.GONE);
                            ll_PinD4 .setVisibility(View.GONE);
                            ll_PinD5.setVisibility(View.GONE);
                            ll_PinD6.setVisibility(View.GONE);
                            ll_PinD7 .setVisibility(View.GONE);
                            ll_PinD8.setVisibility(View.GONE);










                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A4)) {



                            ll_PinD1.setVisibility(View.VISIBLE);
                            ll_PinD2.setVisibility(View.VISIBLE);
                            ll_PinD3 .setVisibility(View.VISIBLE);
                            ll_PinD4 .setVisibility(View.VISIBLE);
                            ll_PinD5.setVisibility(View.GONE);
                            ll_PinD6.setVisibility(View.GONE);
                            ll_PinD7 .setVisibility(View.GONE);
                            ll_PinD8.setVisibility(View.GONE);








                        } else if (DeviceType.equalsIgnoreCase(UTIL.Device_Switch_A8)) {





                            ll_PinD1.setVisibility(View.VISIBLE);
                            ll_PinD2.setVisibility(View.VISIBLE);
                            ll_PinD3 .setVisibility(View.VISIBLE);
                            ll_PinD4 .setVisibility(View.VISIBLE);
                            ll_PinD5.setVisibility(View.VISIBLE);
                            ll_PinD6.setVisibility(View.VISIBLE);
                            ll_PinD7 .setVisibility(View.VISIBLE);
                            ll_PinD8.setVisibility(View.VISIBLE);






                        }


                    } else {
                        Toast.makeText(myContext,
                                " Devices details not found!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(myContext,
                        "Volley Error!", Toast.LENGTH_SHORT).show();
                utill.hideProgressDialog();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
