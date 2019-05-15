package com.pombingsoft.planet_iot.activity.switch_controller;

import android.content.Context;
import android.content.Intent;
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
import com.pombingsoft.planet_iot.activity.DeviceListActivity;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONException;
import org.json.JSONObject;

public class AddExistingDeviceActivity extends AppCompatActivity {

    EditText edt_DeviceId, edt_device_password;
    Button btn_add_device;
    Context myContext;
    UTIL utill;
    String Uid;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_existing_device);
        myContext = AddExistingDeviceActivity.this;
        utill = new UTIL(myContext);
        //check device should not be already added in the list
         Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        edt_DeviceId = findViewById(R.id.edt_DeviceId);
        edt_device_password = findViewById(R.id.edt_device_password);
        btn_add_device = findViewById(R.id.btn_add_device);
        btn_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String DeviceId = edt_DeviceId.getText().toString().trim();
                String DevicePass = edt_device_password.getText().toString().trim();

                if (DeviceId.isEmpty()) {
                    Toast.makeText(myContext, "Device ID can not be empty!", Toast.LENGTH_SHORT).show();
                } else if (DevicePass.isEmpty()) {
                    Toast.makeText(myContext, "Device Password can not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    AddExistingDevice_ApiCall(DeviceId,Uid,DevicePass);
                }
            }
        });


    }






    void AddExistingDevice_ApiCall(String DeviceId,String UserId,String DevicePassword) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Registering device...");

        String URL_LOGIN = null;

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.AddExistingDevice_API + "DeviceId=" + DeviceId + "&UserId=" + UserId + "&DevicePassword=" + DevicePassword;
            URL_LOGIN =  URL_LOGIN.replaceAll(" ","%20");
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


                    if (status.equals("1")) {
                       /* Toast.makeText(myContext,
                                "Device added successfully!", Toast.LENGTH_SHORT).show();*/

                        dialog_msg("Device added successfully!");


                    } else if (status.equals("0")) {

                        String msg = jObj.getString("errormsg");
                        dialog_msg(msg);

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



    private void dialog_msg(String msg) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title =  dialogView.findViewById(R.id.title);
        final TextView message =  dialogView.findViewById(R.id.message);
        final Button positiveBtn =  dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn =  dialogView.findViewById(R.id.negativeBtn);



        title.setText(msg);
       // message.setText("Are you sure?");
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                startActivity(new Intent(myContext,DeviceListActivity.class));
                finish();

            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

}
