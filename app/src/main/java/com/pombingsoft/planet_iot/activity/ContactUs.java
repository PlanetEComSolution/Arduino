package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactUs extends AppCompatActivity {
    Context myContext;
    EditText et_mobile, et_password;
    UTIL utill;

    AlertDialog alertDialog;
   TextView tv_timing,tv_compName,tv_title_timing, tv_title_compName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        myContext = ContactUs.this;
        utill = new UTIL(myContext);
        tv_timing=findViewById(R.id.tv_timing);
        tv_compName=findViewById(R.id.tv_compName);
        tv_title_timing=findViewById(R.id.tv_title_timing);
        tv_title_compName=findViewById(R.id.tv_title_compName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {

            getContactInfoAPICall();

        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    void getContactInfoAPICall() {
        String tag_string_req = "req_login";
        utill.showProgressDialog("");

        String URL_LOGIN = null;

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetContact_API;
            URL_LOGIN = URL_LOGIN.replaceAll(" ", "%20");
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
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String Title = jsonObject.getString("Title");
                    String Description = jsonObject.getString("Description");

                    JSONObject jsonObject_1 = jsonArray.getJSONObject(1);
                    String Title_1 = jsonObject_1.getString("Title");
                    String Description_1 = jsonObject_1.getString("Description");


                    tv_title_compName.setText(Title);
                    tv_compName.setText(Description);
                    tv_title_timing.setText(Title_1);
                    tv_timing.setText(Description_1);

                } catch (JSONException e) {
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
}
