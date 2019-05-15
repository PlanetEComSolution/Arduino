package com.pombingsoft.planet_iot.activity.air_quality_monitor;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SuperAdminActivity extends AppCompatActivity {
    Context myContext;
    UTIL utill;

    Button btnSubmit;
    EditText et_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);
        myContext = SuperAdminActivity.this;
        utill = new UTIL(myContext);
        et_msg = findViewById(R.id.et_msg);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = et_msg.getText().toString().trim();
                if (msg.isEmpty()) {
                    Toast.makeText(myContext, "Text field can not be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    SendTextToDeviceC(msg);
                }
            }
        });

    }


    private void SendTextToDeviceC(String msg) {
        String tag_string_req = "req_login";
        String URL;
       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Kindly wait...");
        if (!pDialog.isShowing())
            pDialog.show();*/


        utill.showProgressDialog("Sending data...");

        Map<String, String> params = new HashMap<>();
        params.put("Message", msg);


        URL = UTIL.Domain_Arduino + UTIL.SendTextDeviceC_API;


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    utill.hideProgressDialog();
                        //{"errormsg":"Device Registered","Status":"1","UserId":null}

                        try {
                            JSONObject jObj = response;
                            String status = jObj.getString("Status");


                            if (status.equals("1")) {


                                Toast.makeText(myContext,
                                        "Done!", Toast.LENGTH_SHORT).show();

                                finish();
                            } else if (status.equals("2")) {
                                Toast.makeText(myContext,
                                        "Failed!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();

            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }
}
