package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class MyProfileActivity extends AppCompatActivity {
    Button btnUpdate, btnChangePassword;
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    TextView Text_Name, Text_Mobile, Text_Gender, Text_Password, Text_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        myContext = MyProfileActivity.this;
        utill = new UTIL(myContext);


        init_views();
        set_listener();

    }

    private void init_views() {

        Text_Name = findViewById(R.id.Text_Name);
        Text_Mobile = findViewById(R.id.Text_Mobile);
        Text_Gender = findViewById(R.id.Text_Gender);
        Text_Password = findViewById(R.id.Text_Password);
        Text_email = findViewById(R.id.Text_email);


        btnUpdate = findViewById(R.id.btnUpdate);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }
    private void  set_listener(){
        btnUpdate .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(myContext,UpdateProfileActivity.class));

            }
        });
        btnChangePassword .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(myContext,ChangePasswordActivity.class));

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


/*


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(UTIL.NoInternet);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //yes
                        alertDialog.dismiss();
                        finish();


                    }
                });


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();*/

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
                //{"errormsg":"Successfully Registered","Status":"1",,"Status":"0","UserId":null,"UserName":null}
                //{"errormsg":"You have this device already.","Status":"0","UserId":null,"UserName":null}

                try {
                    JSONArray jsonArray=new JSONArray(response);

                    JSONObject jObj = jsonArray.getJSONObject(0);
                    String Username = jObj.getString("Username");
                    String Password = jObj.getString("Password");
                    String Mobile = jObj.getString("Mobile");
                    String Gender = jObj.getString("Gender");
                    String EmailId = jObj.getString("EmailId");

                    if (Username != null) {  //means new notification received
                        Text_Name.setText(Username);
                    } else {
                        Text_Name.setText("N/A");
                    }

                    if (Password != null) {  //means new notification received
                        Text_Password.setText(Password);
                    } else {
                        Text_Password.setText("N/A");
                    }
                    if (Mobile != null) {  //means new notification received
                        Text_Mobile.setText(Mobile);
                    } else {
                        Text_Mobile.setText("N/A");
                    }
                    if (Gender != null) {  //means new notification received
                        Text_Gender.setText(Gender);
                    } else {
                        Text_Gender.setText("N/A");
                    }
                    if (EmailId != null) {  //means new notification received
                        Text_email.setText(EmailId);
                    } else {
                        Text_email.setText("N/A");
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


}
