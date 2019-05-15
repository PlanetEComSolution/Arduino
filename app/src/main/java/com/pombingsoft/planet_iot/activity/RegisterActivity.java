
package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {


    Button btn_register;
    EditText edt_Name, edt_email_id, edt_mobile_no, edt_pass, edt_confirmPass;
    Context myContext;
    UTIL utill;
    RadioButton RBtn_female,RBtn_male;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        myContext = RegisterActivity.this;
        utill = new UTIL(myContext);

        edt_Name = findViewById(R.id.edt_firstName);
        edt_email_id = findViewById(R.id.email_id);
        edt_mobile_no = findViewById(R.id.edt_mobile);
        edt_pass = findViewById(R.id.edt_pass);
        edt_confirmPass = findViewById(R.id.edt_Confirm_pass);
        btn_register = findViewById(R.id.btn_register);

        RBtn_female = findViewById(R.id.RBtn_female);
         RBtn_male = findViewById(R.id.RBtn_male);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  Gender="";
                String user_name = edt_Name.getText().toString().trim();
                String user_email = edt_email_id.getText().toString().trim();
                String mobile_no = edt_mobile_no.getText().toString().trim();
                String pass = edt_pass.getText().toString().trim();
                String confirmPass = edt_confirmPass.getText().toString().trim();

                if(RBtn_female.isChecked()){
                    Gender ="Female";
                }
                else {
                    Gender   ="Male";
                }
                if (user_name.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter name!", Toast.LENGTH_SHORT)
                            .show();
                } else if (user_email.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter email!", Toast.LENGTH_SHORT)
                            .show();
                } else if (mobile_no.isEmpty() || mobile_no.length() != 10) {
                    Toast.makeText(myContext,
                            "Please enter 10 digit mobile no.!", Toast.LENGTH_SHORT)
                            .show();
                } else if (pass.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter password!", Toast.LENGTH_SHORT)
                            .show();
                } else if (pass.length() < 5) {
                    Toast.makeText(myContext,
                            "Password length should be at least 5 digit!", Toast.LENGTH_SHORT)
                            .show();
                } else if (confirmPass.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter confirm password!", Toast.LENGTH_SHORT)
                            .show();
                } else if (!(pass.equals(confirmPass))) {
                    Toast.makeText(myContext,
                            "Password and confirm password does not match!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        RegisterApiCall(user_name, user_email, mobile_no, pass,Gender);
                    } else {
                        Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();
                    }

                }


            }
        });
    }

    void RegisterApiCall(String user_name, String user_email, String mobile_no, String pass, String gender) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Registering ...");

        String URL_LOGIN = null;

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.Register_User_API + "Username=" + user_name + "&Password=" + pass + "&Mobile=" + mobile_no + "&EmailId=" + user_email+"&Gender="+gender;
            URL_LOGIN =  URL_LOGIN.replaceAll(" ","%20");
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                utill.hideProgressDialog();
                //{"errormsg":"Successfully Registered","Status":"1","Key_UserId":"00000012"}
                //{"errormsg":"Mobile Already Exist","Status":"2","Key_UserId":""}
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("Status");
                    String msg = jObj.getString("errormsg");
                    String UserId = jObj.getString("UserId");
                    String UserName = jObj.getString("UserName");

                    UTIL.setPref(myContext, UTIL.Key_USERNAME, UserName);


                    if (status.equals("1")) {
                        Toast.makeText(myContext,
                                "Registered successfully!", Toast.LENGTH_SHORT).show();

                        UTIL.setPref(myContext, UTIL.Key_UserId, UserId);
                     //   startActivity(new Intent(myContext, HomeActivity.class));
                        startActivity(new Intent(myContext, ActivityWithNavigationMenu.class));

                        finish();

                    } else if (status.equals("2")) {

                        Toast.makeText(myContext,
                                "Mobile number already registered!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(myContext,
                                "Some error occurred!", Toast.LENGTH_SHORT).show();

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
    public void onBackPressed() {
          super.onBackPressed();

        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();


    }
}
