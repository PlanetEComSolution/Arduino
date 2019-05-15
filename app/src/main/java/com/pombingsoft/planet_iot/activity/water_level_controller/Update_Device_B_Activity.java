package com.pombingsoft.planet_iot.activity.water_level_controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.air_quality_monitor.UpdateDevice_C_Activity;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.Myspinner;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Update_Device_B_Activity extends AppCompatActivity {
    Context myContext;
    UTIL utill;
    Button btnUpdate, btnCancel;
    EditText et_DeviceName, et_DevicePass;
    String DeviceId, UserId, Device_Type;
    Spinner spinner_tank_setting, spinner_tankType;

    EditText et_Upper_level_depth;
    EditText et_Upper_level_water_capacity;
    EditText et_pump_on_time_max;
    EditText et_pump_cool_time_min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device_b);
        myContext = Update_Device_B_Activity.this;
        utill = new UTIL(myContext);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        et_DeviceName = findViewById(R.id.et_DeviceName);
        et_DevicePass = findViewById(R.id.et_DevicePass);

        spinner_tank_setting = findViewById(R.id.spinner_tank_setting);
        spinner_tankType = findViewById(R.id.spinner_tankType);


        et_Upper_level_depth = findViewById(R.id.et_Upper_level_depth);
        et_Upper_level_water_capacity = findViewById(R.id.et_Upper_level_water_capacity);
        et_pump_on_time_max = findViewById(R.id.et_pump_on_time_max);
        et_pump_cool_time_min = findViewById(R.id.et_pump_cool_time_min);


        ArrayList<Myspinner> list_tankType = new ArrayList<>();
        list_tankType.add(new Myspinner("Single Tank", "0"));
        list_tankType.add(new Myspinner("Double Tank", "1"));

        ArrayAdapter<ArrayList<Myspinner>> aa = new ArrayAdapter(myContext, android.R.layout.simple_spinner_dropdown_item, list_tankType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tank_setting.setAdapter(aa);


        ArrayList<Myspinner> tank_func_state = new ArrayList<>();
        tank_func_state.add(new Myspinner("Manual", "0"));
        tank_func_state.add(new Myspinner("Fully Automatic", "1"));
        tank_func_state.add(new Myspinner("Semi Automatic", "2"));

        ArrayAdapter<ArrayList<Myspinner>> aa1 = new ArrayAdapter(myContext, android.R.layout.simple_spinner_dropdown_item, tank_func_state);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tankType.setAdapter(aa1);



        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            Device_Type = i.getStringExtra("Device_Type");
        } catch (Exception e) {
            e.getMessage();
        }

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getDeviceDetailsB_ApiCall();

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

                String Upper_level_depth = et_Upper_level_depth.getText().toString().trim();
                String Upper_level_water_capacity = et_Upper_level_water_capacity.getText().toString().trim();
                String pump_on_time_max = et_pump_on_time_max.getText().toString().trim();
                String pump_cool_time_min = et_pump_cool_time_min.getText().toString().trim();

                if (DeviceName.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter device Name!", Toast.LENGTH_SHORT)
                            .show();
                } else if (DevicePass.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter device Password!", Toast.LENGTH_SHORT)
                            .show();
                } else if (Upper_level_depth.equals("") || Upper_level_depth.equals("0")) {
                    Toast.makeText(myContext, "Please enter upper level depth.", Toast.LENGTH_SHORT).show();

                } else if (Upper_level_water_capacity.equals("") || Upper_level_water_capacity.equals("0")) {
                    Toast.makeText(myContext, "Please enter upper level water capacity.", Toast.LENGTH_SHORT).show();

                } else if (pump_on_time_max.equals("") || pump_on_time_max.equals("0")) {
                    Toast.makeText(myContext, "Please enter max pump ON time.", Toast.LENGTH_SHORT).show();

                } else if (pump_cool_time_min.equals("") || pump_cool_time_min.equals("0")) {
                    Toast.makeText(myContext, "Please enter min pump cool time.", Toast.LENGTH_SHORT).show();

                } else {

                    Myspinner myspinner = (Myspinner) spinner_tank_setting.getSelectedItem();
                    String tank_type = myspinner.getspinnerVal();

                    Myspinner myspinner1 = (Myspinner) spinner_tankType.getSelectedItem();
                    String tank_function_state = myspinner1.getspinnerVal();


                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        UpdateDeviceB(DeviceId, DeviceName, DevicePass,
                                tank_type,tank_function_state,tank_function_state,Upper_level_depth,
                                Upper_level_water_capacity,pump_on_time_max,pump_cool_time_min);

                    } else {
                        Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();
                    }


                }


            }
        });


    }

    private void UpdateDeviceB(
            String DeviceId,
            String DeviceName,
            String Device_Password,
            String Tank_type,
            String Tank_function_state,
            String Pump_Mode,
            String Upper_Depth,
            String Upper_Capacity,
            String Pump_on_time_max,
            String Pump_cool_time_min


    ) {

        String tag_string_req = "req_login";
        utill.showProgressDialog("Updating device details...");
        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("Device_Password", Device_Password);

        params.put("Tank_type", Tank_type);
        params.put("Tank_function_state", Tank_function_state);
        params.put("Pump_Mode", Pump_Mode);
        params.put("Upper_level_depth", Upper_Depth);
        params.put("Upper_level_water_capacity", Upper_Capacity);
        params.put("Pump_on_time_max", Pump_on_time_max);
        params.put("Pump_cool_time_min", Pump_cool_time_min);


        URL = UTIL.Domain_Arduino + UTIL.UpdateDeviceDetails_B_API+"DeviceId="+DeviceId;//+ "DeviceId=" + DeviceId;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        JSONObject jsonObject=new JSONObject(params);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
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
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }
    void getDeviceDetailsB_ApiCall() {
        String tag_string_req = "req_login";

       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        String URL_LOGIN = null;

      //  String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsB_API + "DeviceId=" + DeviceId + "&UserId=" + UserId;

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
                        String DeviceId = jsonObject.getString("DeviceId");
                        String DeviceName = jsonObject.getString("DeviceName");
                        String Device_Password = jsonObject.getString("Device_Password");


                        String Tank_setting_type = jsonObject.getString("Tank_type");

                        String Tank_function_state = jsonObject.getString("Tank_function_state");
                        String Pump_Mode = jsonObject.getString("Pump_Mode");

                        String Upper_Depth = jsonObject.getString("Upper_Depth");
                        String Upper_Capacity = jsonObject.getString("Upper_Capacity");


                        String Pump_on_time_max = jsonObject.getString("Pump_on_time_max");
                        String Pump_cool_time_min = jsonObject.getString("Pump_cool_time_min");







                       // et_TILDeviceName.setHint("Device Name");
                     //   et_TILDevicePass.setHint("Device Password");
                        et_DeviceName.setText(DeviceName);
                        et_DevicePass.setText(Device_Password);

                       if(Tank_setting_type.equals("0")){
                           spinner_tank_setting.setSelection(0);
                       }
                       else  if(Tank_setting_type.equals("1")){

                           spinner_tank_setting.setSelection(1);
                       }

                       //////
                        if(Tank_function_state.equals("0")){
                            spinner_tankType.setSelection(0);
                        }
                        else  if(Tank_function_state.equals("1")){

                            spinner_tankType.setSelection(1);
                        }
                        else  if(Tank_function_state.equals("2")){

                            spinner_tankType.setSelection(2);
                        }

                       //////

                        et_Upper_level_depth.setText(Upper_Depth);
                        et_Upper_level_water_capacity.setText(Upper_Capacity);
                        et_pump_on_time_max .setText(Pump_on_time_max);
                        et_pump_cool_time_min.setText(Pump_cool_time_min);




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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
