package com.pombingsoft.planet_iot.activity.switch_controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceDetailActivity extends AppCompatActivity {
    Context myContext;
    UTIL utill;

    String DeviceName, DeviceId, Device_Password;

    TextView Text_DeviceName, Text_DeviceID, Text_DevicePassword;
    ArrayList<HashMap<String, String>> list_DeviceUsers;
    ListView listview_DeviceUsers;
    String Uid, Current_User_Role;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        myContext = DeviceDetailActivity.this;
        utill = new UTIL(myContext);
        Text_DeviceName = findViewById(R.id.Text_DeviceName);
        Text_DeviceID = findViewById(R.id.Text_DeviceID);
        Text_DevicePassword = findViewById(R.id.Text_DevicePassword);

        listview_DeviceUsers = findViewById(R.id.listview_DeviceUsers);

        Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        try {
            Intent i = getIntent();
            DeviceName = i.getStringExtra("DeviceName");
            DeviceId = i.getStringExtra("DeviceId");
            Device_Password = i.getStringExtra("Device_Password");
            Current_User_Role = i.getStringExtra("Current_User_Role").trim();
            Text_DeviceName.setText(DeviceName);
            Text_DeviceID.setText(DeviceId);
            Text_DevicePassword.setText(Device_Password);


        } catch (Exception e) {
            e.getMessage();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            if (DeviceId == null || DeviceId.equalsIgnoreCase("null")) {
                Toast.makeText(myContext,
                        "Wrong device id!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                getDeviceUsersDetail_Api(DeviceId);
            }
        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }

    }

    void getDeviceUsersDetail_Api(String DeviceId) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Getting data ...");

        String URL_LOGIN = null;

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.DeviceUsersDetail_API + "DeviceId=" + DeviceId;
            URL_LOGIN = URL_LOGIN.replaceAll(" ", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                utill.hideProgressDialog();
               /*
               * [{"UserId":"00000002","UserName":"00000002","DeviceId":"bDfvnBCbw9","RegisteredBy":null,"RoleId":"1         ","DeviceType":"A4"},
               * {"UserId":"00000009","UserName":"Nksw","DeviceId":"bDfvnBCbw9","RegisteredBy":"00000002","RoleId":"2         ","DeviceType":"A4"}]
               *
                *  */

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() < 1) {
                        Toast.makeText(myContext,
                                "Some error occurred!", Toast.LENGTH_SHORT).show();

                    } else {

                        list_DeviceUsers = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String UserId = jsonObject.getString("UserId");
                            String UserName = jsonObject.getString("UserName");
                            String DeviceId = jsonObject.getString("DeviceId");
                            String RegisteredBy = jsonObject.getString("RegisteredBy");
                            String RoleId = jsonObject.getString("RoleId").trim();
                            String DeviceType = jsonObject.getString("DeviceType");


                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("UserId", UserId);
                            hashMap.put("UserName", UserName);
                            hashMap.put("DeviceId", DeviceId);
                            hashMap.put("RegisteredBy", RegisteredBy);
                            hashMap.put("RoleId", RoleId);
                            hashMap.put("DeviceType", DeviceType);
                            list_DeviceUsers.add(hashMap);

                        }
                        DeviceUsers_list_adapter adapter = new DeviceUsers_list_adapter(myContext, list_DeviceUsers);
                        listview_DeviceUsers.setAdapter(adapter);
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

    public class DeviceUsers_list_adapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> beanArrayList;
        Context context;


        public DeviceUsers_list_adapter(Context context, ArrayList<HashMap<String, String>> beanArrayList) {
            this.context = context;
            this.beanArrayList = beanArrayList;

        }

        @Override
        public int getCount() {
            return beanArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertview, ViewGroup viewGroup) {

            final Holder holder;
            final String index1 = String.valueOf(i + 1);

            final String UserId = beanArrayList.get(i).get("UserId");
            final String UserName = beanArrayList.get(i).get("UserName");
            final String DeviceId = beanArrayList.get(i).get("DeviceId");
            final String RegisteredBy = beanArrayList.get(i).get("RegisteredBy");
            final String RoleId = beanArrayList.get(i).get("RoleId");
            final String DeviceType = beanArrayList.get(i).get("DeviceType");


            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_device_users, null);
                holder.tv_UserName = (TextView) convertview.findViewById(R.id.tv_UserName);
                holder.tv_User_Count = (TextView) convertview.findViewById(R.id.tv_User_Count);
                holder.img_user_setting = (ImageView) convertview.findViewById(R.id.img_user_setting);


                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }


            holder.tv_User_Count.setText("User: " + index1);
            if (UserId.equals(Uid)) {
                holder.tv_UserName.setText("You");
            } else {
                holder.tv_UserName.setText(UserName);
            }


            holder.img_user_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Current_User_Role.equals(UTIL.Key_RoleId_Admin) ) {   //Admin
                        dialog_change_Role(UserId, UserName, RoleId, DeviceType);
                    } else {
                        Toast.makeText(myContext, "You can not perform this action!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            convertview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Current_User_Role.equals(UTIL.Key_RoleId_Admin) ) {   //Admin
                        dialog_change_Role(UserId, UserName, RoleId, DeviceType);
                    } else {
                        Toast.makeText(myContext, "You can not perform this action!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertview;
        }


        class Holder {


            TextView tv_UserName;
            TextView tv_User_Count;
            ImageView img_user_setting;


        }


    }

    private void dialog_change_Role(final String userId, String userName, final String roleId, final String DeviceType) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.change_role, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.user);
        final TextView message = dialogView.findViewById(R.id.user_Role);


        final Button changeRoleToManger = dialogView.findViewById(R.id.changeRoleGeneralToManger);
        final Button changeRoleToSub_Admin = dialogView.findViewById(R.id.changeRoleGeneralToSub_Admin);
        final Button changeRoleToGeneral = dialogView.findViewById(R.id.changeRoleMangerToGeneral);


        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText(userName);

        if (roleId.equals(UTIL.Key_RoleId_Admin)) {
            message.setText("Admin");
            changeRoleToManger.setVisibility(View.GONE);
            changeRoleToGeneral.setVisibility(View.GONE);
            changeRoleToSub_Admin.setVisibility(View.GONE);
        } else if (roleId.equals(UTIL.Key_RoleId_Sub_admin)) {
            message.setText("Sub-Admin");
            changeRoleToManger.setVisibility(View.VISIBLE);
            changeRoleToGeneral.setVisibility(View.VISIBLE);
            changeRoleToSub_Admin.setVisibility(View.GONE);
        } else if (roleId.equals(UTIL.Key_RoleId_Manager)) {
            message.setText("Manager");
            changeRoleToManger.setVisibility(View.GONE);
            changeRoleToGeneral.setVisibility(View.VISIBLE);
            changeRoleToSub_Admin.setVisibility(View.VISIBLE);

        } else if (roleId.equals(UTIL.Key_RoleId_General)) {
            message.setText("General");
            changeRoleToManger.setVisibility(View.VISIBLE);
            changeRoleToGeneral.setVisibility(View.GONE);
            changeRoleToSub_Admin.setVisibility(View.VISIBLE);
        }

        positiveBtn.setText("Cancel");

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();


            }
        });


        changeRoleToGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                try {

                    hashMap.put("DeviceId", DeviceId);
                    hashMap.put("Role", UTIL.Key_RoleId_General);
                    hashMap.put("UserId", userId);
                    hashMap.put("DeviceType", DeviceType);
                    alertDialog.dismiss();
                    ChangeUserRole_Api(hashMap);
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        });


        changeRoleToManger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                try {

                    hashMap.put("DeviceId", DeviceId);
                    hashMap.put("Role", UTIL.Key_RoleId_Manager);
                    hashMap.put("UserId", userId);
                    hashMap.put("DeviceType", DeviceType);
                    alertDialog.dismiss();
                    ChangeUserRole_Api(hashMap);
                } catch (Exception e) {
                    e.getMessage();
                }


            }
        });

        changeRoleToSub_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                try {

                    hashMap.put("DeviceId", DeviceId);
                    hashMap.put("Role", UTIL.Key_RoleId_Sub_admin);
                    hashMap.put("UserId", userId);
                    hashMap.put("DeviceType", DeviceType);
                    alertDialog.dismiss();
                    ChangeUserRole_Api(hashMap);
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        });





        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    void ChangeUserRole_Api(HashMap<String, String> params) {
        String tag_string_req = "req_login";
        utill.showProgressDialog("Updating Role ...");
        String URL_LOGIN = null;
        JSONObject js = new JSONObject(params);

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.UpdateUserRole_API;
            URL_LOGIN = URL_LOGIN.replaceAll(" ", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utill.hideProgressDialog();
                        try {
                            /*
                            {"errormsg": "Device is updated","Status": "1", "UserId": null, "UserName": null}
                           */


                            JSONObject jsonObject = response;
                            String status = jsonObject.getString("Status");
                            if (status.equals("1")) {//request updated at server
                                Toast.makeText(getApplicationContext(), "Role updated successfully!", Toast.LENGTH_SHORT).show();

                                //
                                if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                                    if (DeviceId == null || DeviceId.equalsIgnoreCase("null")) {
                                        Toast.makeText(myContext,
                                                "Wrong device id!", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getDeviceUsersDetail_Api(DeviceId);
                                    }
                                } else {
                                    Toast.makeText(myContext,
                                            UTIL.NoInternet, Toast.LENGTH_SHORT)
                                            .show();
                                }

                                //


                            } else {
                                utill.hideProgressDialog();
                                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            utill.hideProgressDialog();
                            e.printStackTrace();
                            Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(myContext, "Volly error: ", Toast.LENGTH_SHORT).show();

            }
        }) {
        };

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
