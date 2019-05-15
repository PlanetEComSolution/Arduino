package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.water_level_controller.SetUpActivity;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.Myspinner;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateProfileActivity extends AppCompatActivity {
    //Spinner spinner_gender;
    Context myContext;
    UTIL utill;
    Button btn_update_profile;
    AlertDialog alertDialog;
    EditText edt_Name, edt_email_id, edt_mobile_no, edt_pass, edt_confirmPass;
    RadioButton RBtn_female, RBtn_male;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        myContext = UpdateProfileActivity.this;
        utill = new UTIL(myContext);

        init_views();
        set_listener();


    }

    private void init_views() {
        //  spinner_gender= findViewById(R.id.spinner_gender);
        edt_Name = findViewById(R.id.edt_firstName);
        edt_email_id = findViewById(R.id.email_id);
        edt_mobile_no = findViewById(R.id.edt_mobile);
        edt_pass = findViewById(R.id.edt_pass);
        btn_update_profile = findViewById(R.id.btn_update_profile);
        RBtn_female = findViewById(R.id.RBtn_female);
        RBtn_male = findViewById(R.id.RBtn_male);

/*
        ArrayList<Myspinner> list_gender = new ArrayList<>();
        list_gender.add(new Myspinner("Male", "0"));
        list_gender.add(new Myspinner("Female", "1"));

        ArrayAdapter<ArrayList<Myspinner>> aa = new ArrayAdapter(myContext, android.R.layout.simple_spinner_dropdown_item, list_gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(aa);*/


    }

    private void set_listener() {


        edt_mobile_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateProfileActivity.this, "Mobile no. can not be changed!", Toast.LENGTH_SHORT).show();

            }
        });
        edt_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateProfileActivity.this, "Please use 'Change Password' \noption to change password!", Toast.LENGTH_SHORT).show();

            }
        });
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Gender = "";
                if (RBtn_female.isChecked()) {
                    Gender = "Female";
                } else {
                    Gender = "Male";
                }

                String name = edt_Name.getText().toString().trim();
                String email = edt_email_id.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter name!", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter email id!", Toast.LENGTH_SHORT).show();
                } else {
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        UpdateProfile_ApiCall(name, email, Gender);
                    } else {

                        DialogInternet();
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            GetProfile_API_ApiCall();
        } else {

            DialogInternet();
        }


    }

    void GetProfile_API_ApiCall() {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Kindly wait ...");

        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetProfile_API + "UserId=" + Uid;
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

                    JSONObject jObj = jsonArray.getJSONObject(0);
                    String Username = jObj.getString("Username");
                    String Password = jObj.getString("Password");
                    String Mobile = jObj.getString("Mobile");
                    String Gender = jObj.getString("Gender");
                    String EmailId = jObj.getString("EmailId");


                    if (Username != null) {  //means new notification received
                        edt_Name.setText(Username);
                    } else {
                        edt_Name.setText("");
                    }

                    if (Password != null) {  //means new notification received
                        edt_pass.setText(Password);
                    } else {
                        edt_pass.setText("");
                    }
                    if (EmailId != null) {  //means new notification received
                        edt_email_id.setText(EmailId);
                    } else {
                        edt_email_id.setText("");
                    }
                    if (Mobile != null) {  //means new notification received
                        edt_mobile_no.setText(Mobile);
                    } else {
                        edt_mobile_no.setText("");
                    }
                    if (Gender != null) {  //means new notification received
                        if (Gender.equalsIgnoreCase("male")) {
                            RBtn_male.setChecked(true);
                        } else {
                            RBtn_female.setChecked(true);
                        }
                    } else {
                        RBtn_male.setChecked(true);
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

    private void DialogInternet() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("No Internet Connection !");
        message.setText("Please connect to a working internet connection");
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();


    }


    void UpdateProfile_ApiCall(String Name, String Email, String Gender) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Saving ...");

        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.UpdateProfile_API + "UserId=" + Uid + "&Name=" + Name + "&Email=" + Email + "&Gender=" + Gender;
            URL_LOGIN = URL_LOGIN.replaceAll(" ", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                utill.hideProgressDialog();
                //{"errormsg":"Successfully Registered","Status":"1",,"Status":"0","UserId":null,"UserName":null}
                //{"errormsg":"You have this device already.","Status":"0","UserId":null,"UserName":null}

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("Status");

                    if (status.equalsIgnoreCase("1")) {
                        Toast.makeText(UpdateProfileActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
finish();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
