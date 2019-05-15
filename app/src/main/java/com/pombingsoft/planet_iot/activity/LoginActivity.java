package com.pombingsoft.planet_iot.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    static final Integer LOCATION = 0x1;
    static final int RequestPermissionCode = 100;
    TextView forget_password;
    Button btnLogin, btn_register;
    Context myContext;
    EditText et_mobile, et_password;
    UTIL utill;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        myContext = LoginActivity.this;
        utill = new UTIL(myContext);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_password = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btn_register = (Button) findViewById(R.id.btn_register);


        if (CheckingPermissionIsEnabledOrNot()) {
            //   Toast.makeText(HomeActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        } else {
            RequestMultiplePermission();
        }

        //  askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_mobile = et_mobile.getText().toString().trim();
                String user_pwd = et_password.getText().toString().trim();
                if (user_mobile.isEmpty() || user_mobile.length() != 10) {
                    Toast.makeText(myContext,
                            "Please enter 10 digit mobile no.!", Toast.LENGTH_SHORT)
                            .show();
                } else if (user_pwd.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter password!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        LoginApiCall(user_mobile, user_pwd);
                    } else {
                        Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();
                    }

                }


            }

        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();

            }

        });

    }

    void LoginApiCall(final String mobile, String password) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Logging in...");

        String URL_LOGIN = null;

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.Login_API + "mobileno=" + mobile + "&pwd=" + password;
            URL_LOGIN = URL_LOGIN.replaceAll(" ", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                utill.hideProgressDialog();
                //{"errormsg":"Login Correct","Status":"1","Key_UserId":"00000012"}
                //{"errormsg":"Wrong","Status":"2","Key_UserId":""}
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("Status");
                    String msg = jObj.getString("errormsg");
                    String UserId = jObj.getString("UserId");
                    String UserName = jObj.getString("UserName");
                    String Gender = "MALE";

                    if (status.equals("1")) {
                        Toast.makeText(myContext,
                                "Logged-In successfully", Toast.LENGTH_SHORT).show();

                        UTIL.setPref(myContext, UTIL.Key_UserId, UserId);
                        UTIL.setPref(myContext, UTIL.Key_GENDER, Gender);
                        UTIL.setPref(myContext, UTIL.Key_USERNAME, UserName);
                        UTIL.setPref(myContext, UTIL.Key_USERNAME, UserName);
                        UTIL.setPref(myContext, UTIL.Key_Mobile, mobile);


                        if (mobile.equals("9810334191")) {
                            startActivity(new Intent(myContext, SuperAdminHomeActivity.class));//for Super admin
                        } else {
                            //  startActivity(new Intent(myContext, HomeActivity.class));
                            startActivity(new Intent(myContext, ActivityWithNavigationMenu.class));

                        }

                        finish();

                    } else if (status.equals("2")) {

                        Toast.makeText(myContext,
                                "Wrong mobile number or password", Toast.LENGTH_SHORT).show();

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
        //  super.onBackPressed();
        dialog_Exit();
/*

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure?\nDo you want to exit?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        alertDialog.dismiss();

                        finish();

                    }
                });


        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
*/


    }
   /* private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //  Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            //askForGPS();
        }
    }
*/

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            //   Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {

            //  Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void dialog_Exit() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Do you want to exit?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
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
        alertDialog.show();

    }

    private void RequestMultiplePermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,

                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean Location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExtrenalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (WriteExtrenalStorage && Location) {

                        //  Toast.makeText(HomeActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

}
