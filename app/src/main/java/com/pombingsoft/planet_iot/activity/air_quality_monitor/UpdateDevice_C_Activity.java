package com.pombingsoft.planet_iot.activity.air_quality_monitor;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class UpdateDevice_C_Activity extends AppCompatActivity {
    Context myContext;
    UTIL utill;
    Button btnUpdate, btnCancel;
    EditText et_DeviceName, et_DevicePass;
    String DeviceId, UserId, Device_Type;
    TextInputLayout et_TILDeviceName,et_TILDevicePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device_c);
        myContext = UpdateDevice_C_Activity.this;
        utill = new UTIL(myContext);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        et_DeviceName = findViewById(R.id.et_DeviceName);
        et_DevicePass = findViewById(R.id.et_DevicePass);

        et_TILDeviceName = findViewById(R.id.et_TILDeviceName);
        et_TILDevicePass = findViewById(R.id.et_TILDevicePass);
        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            Device_Type = i.getStringExtra("Device_Type");
        } catch (Exception e) {
            e.getMessage();
        }

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getDeviceDetailsC_ApiCall();

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

                if (DeviceName.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter device Name!", Toast.LENGTH_SHORT)
                            .show();
                }

                else if (DevicePass.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter device Password!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        UpdateDeviceC(DeviceId,DeviceName,DevicePass);

                    } else {
                        Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();
                    }

                }

            }
        });

    }
    void getDeviceDetailsC_ApiCall() {
        String tag_string_req = "req_login";

        utill.showProgressDialog("Fetching device details...");
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

utill.hideProgressDialog();
                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        String DeviceName = jsonObject.getString("DeviceName");
                        String Device_Password = jsonObject.getString("Device_Password");



                        et_TILDeviceName.setHint("Device Name");
                        et_TILDevicePass.setHint("Device Password");
                        et_DeviceName.setText(DeviceName);
                        et_DevicePass.setText(Device_Password);


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
    private void UpdateDeviceC(String DeviceId, String DeviceName, String Device_Password) {

        String tag_string_req = "req_login";
        utill.showProgressDialog("Updating device details...");
        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("Device_Password", Device_Password);
        URL = UTIL.Domain_Arduino + UTIL.UpdateDeviceDetails_C_API;//+ "DeviceId=" + DeviceId;
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

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
