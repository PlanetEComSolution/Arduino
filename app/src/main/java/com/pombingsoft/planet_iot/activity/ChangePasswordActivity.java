package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePasswordActivity extends AppCompatActivity {
    Context myContext;
    UTIL utill;
    EditText edt_old_pass, edt_new_pass, edt_Confirm_new_pass;

    Button btnUpdate, btnCancel;

    AlertDialog alertDialog;
    String old_Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        myContext = ChangePasswordActivity.this;
        utill = new UTIL(myContext);
        edt_old_pass = findViewById(R.id.edt_old_pass);
        edt_new_pass = findViewById(R.id.edt_new_pass);
        edt_Confirm_new_pass = findViewById(R.id.edt_Confirm_new_pass);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old_pass = edt_old_pass.getText().toString().trim();
                String new_pass = edt_new_pass.getText().toString().trim();
                String Confirm_new_pass = edt_Confirm_new_pass.getText().toString().trim();


                if (old_pass.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Old password can not be empty!", Toast.LENGTH_SHORT).show();
                }else if (!(old_pass.equals(old_Password))) {
                    Toast.makeText(ChangePasswordActivity.this, "Old password is wrong!", Toast.LENGTH_SHORT).show();
                } else if (new_pass.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "New password can not be empty!", Toast.LENGTH_SHORT).show();
                }else if (new_pass.length() < 5) {
                    Toast.makeText(myContext,
                            "New password length should be at least 5 digit!", Toast.LENGTH_SHORT)
                            .show();
                } else if (Confirm_new_pass.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter confirm password!", Toast.LENGTH_SHORT)
                            .show();
                } else if (!(new_pass.equals(Confirm_new_pass))) {
                    Toast.makeText(myContext,
                            "Password and confirm password does not match!", Toast.LENGTH_SHORT).show();
                }
                else if ((old_pass.equals(new_pass))) {
                    Toast.makeText(myContext,
                            "Old password and new password can not be same!", Toast.LENGTH_SHORT).show();
                }

                else{
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        UpdatePassword_ApiCall(old_pass,new_pass);
                    } else {

                        DialogInternet();
                    }
                }



            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
    void UpdatePassword_ApiCall(String OldPassword,String NewPassword) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Saving ...");

        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.ChangePassword_API + "UserId=" + Uid+ "&OldPassword=" + OldPassword+ "&NewPassword=" + NewPassword;
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
                        Toast.makeText(ChangePasswordActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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


        // dialogBuilder.setTitle("Device Details");
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

                    old_Password=Password;

                  /*  if (Username != null) {  //means new notification received
                        edt_old_pass.setText(Username);
                    } else {
                        edt_old_pass.setText("");
                    }
*/



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
