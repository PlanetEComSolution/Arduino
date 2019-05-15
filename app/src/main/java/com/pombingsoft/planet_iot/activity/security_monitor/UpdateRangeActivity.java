package com.pombingsoft.planet_iot.activity.security_monitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.regex.Pattern;

public class UpdateRangeActivity extends AppCompatActivity {

    EditText et_min_temp, et_max_temp, et_min_humidity, et_max_humidity;
    Context myContext;
    UTIL utill;
    Button btn_setRange, btn_Cancel;
    String DeviceId, UserId, Device_Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_range);

        utill = new UTIL(UpdateRangeActivity.this);
        myContext = UpdateRangeActivity.this;

        et_min_temp = findViewById(R.id.et_min_temp);
        et_max_temp = findViewById(R.id.et_max_temp);
        et_min_humidity = findViewById(R.id.et_min_humidity);
        et_max_humidity = findViewById(R.id.et_max_humidity);
        btn_setRange = findViewById(R.id.btn_setRange);
        btn_Cancel = findViewById(R.id.btn_Cancel);

        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            Device_Type = i.getStringExtra("Device_Type");
        } catch (Exception e) {
            e.getMessage();
        }


        btn_setRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String min_temp = et_min_temp.getText().toString().trim();
                String max_temp = et_max_temp.getText().toString().trim();
                String min_humidity = et_min_humidity.getText().toString().trim();
                String max_humidity = et_max_humidity.getText().toString().trim();


                if (min_temp.equals("") || min_temp.equals("0")) {
                    Toast.makeText(myContext, "Please enter min temperature.", Toast.LENGTH_SHORT).show();

                } else if (max_temp.equals("") || max_temp.equals("0")) {
                    Toast.makeText(myContext, "Please enter max temperature.", Toast.LENGTH_SHORT).show();

                } else if (min_humidity.equals("") || min_humidity.equals("0")) {
                    Toast.makeText(myContext, "Please enter min humidity.", Toast.LENGTH_SHORT).show();

                } else if (max_humidity.equals("") || max_humidity.equals("0")) {
                    Toast.makeText(myContext, "Please enter max humidity.", Toast.LENGTH_SHORT).show();

                } else {

                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        UpdateRangeDeviceD(max_humidity, min_humidity, max_temp, min_temp, "1");

                    } else {
                        Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();
                    }

                }


            }
        });
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getDeviceDetailsD_ApiCall();

        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void getDeviceDetailsD_ApiCall() {
        String tag_string_req = "req_login";

       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        utill.showProgressDialog("Getting device data...");
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
                        String DeviceId = jsonObject.getString("DeviceId");
                        String DeviceName = jsonObject.getString("DeviceName");
                        String Device_Password = jsonObject.getString("Device_Password");

                        String HumidityRange_Min = jsonObject.getString("HumidityRange_Min");
                        String HumidityRange_Max = jsonObject.getString("HumidityRange_Max");
                        String TempRange_Min = jsonObject.getString("TempRange_Min");
                        String TempRange_Max = jsonObject.getString("TempRange_Max");
                        //TempHumState = jsonObject.getString("TempHumState");


                        //   et_TILDeviceName.setHint("Device Name");
                        //  et_TILDevicePass.setHint("Device Password");
                        //    et_DeviceName.setText(DeviceName);
                        //  et_DevicePass.setText(Device_Password);
                        /////

                        et_min_temp.setText(TempRange_Min);
                        et_max_temp.setText(TempRange_Max);
                        et_min_humidity.setText(HumidityRange_Min);
                        et_max_humidity.setText(HumidityRange_Max);


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

    private void UpdateRangeDeviceD(String hum_max, String hum_min, String max_temp, String min_temp, String TempHumState) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Updating device details...");
        String URL = null;

        Map<String, String> params = new HashMap<>();
        params.put("DeviceId", DeviceId);
        params.put("HumidityRange_Max", hum_max);
        params.put("HumidityRange_Min", hum_min);
        params.put("TempRange_Max", max_temp);
        params.put("TempRange_Min", min_temp);
        params.put("TempHumState", TempHumState);

        URL = UTIL.Domain_Arduino + UTIL.UpdateRangeDevice_D_API;//+ "DeviceId=" + DeviceId;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = response;
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
        });

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
