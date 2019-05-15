package com.pombingsoft.planet_iot.activity.switch_controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SwitchSettingActivity extends AppCompatActivity {
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;

    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus, DimmerType, PinNum;
    // ComboSeekBar seekbar_dimmer_1;

    ArrayList<HashMap<String, String>> list;
    /* TextView scheduler_1, scheduler_2, scheduler_3;
     ImageView img_add_Scheduler_1, img_update_Scheduler_1, img_delete_Scheduler_1;
     ImageView img_add_Scheduler_2, img_update_Scheduler_2, img_delete_Scheduler_2;
     ImageView img_add_Scheduler_3, img_update_Scheduler_3, img_delete_Scheduler_3;*/
    ListView scheduler_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_setting);
        myContext = SwitchSettingActivity.this;
        utill = new UTIL(myContext);

        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            DeviceLockStatus = i.getStringExtra("LockStatus");
            Device_Type = i.getStringExtra("DeviceType");
            DeviceName = i.getStringExtra("DeviceName");
           // DimmerType = i.getStringExtra("DimmerType");
            PinNum = i.getStringExtra("PinNum");

        } catch (Exception e) {
            e.getMessage();
        }


        scheduler_listview = findViewById(R.id.scheduler_listview);



    }


    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            // getPinStatus();
            getDeviceDetailsA_ApiCall();
        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    void getDeviceDetailsA_ApiCall() {
        String tag_string_req = "req_login";
        //  utill.showProgressDialog("Getting device...");
        final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();


        utill.showProgressDialog("Getting device details...");


        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsA_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //utill.hideProgressDialog();
                pDialog.dismiss();
                //1. //[]

                //2.
                list = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                       /* String Pin_d0_Scheduler_1 = jsonObject.getString("Pin_d0_Scheduler_1");
                        String Pin_d0_Scheduler_2 = jsonObject.getString("Pin_d0_Scheduler_2");
                        String Pin_d0_Scheduler_3 = jsonObject.getString("Pin_d0_Scheduler_3");*/
                        String Pin_d1_Scheduler_1 = jsonObject.getString("Pin_d1_Scheduler_1");
                        String Pin_d1_Scheduler_2 = jsonObject.getString("Pin_d1_Scheduler_2");
                        String Pin_d1_Scheduler_3 = jsonObject.getString("Pin_d1_Scheduler_3");
                        String Pin_d2_Scheduler_1 = jsonObject.getString("Pin_d2_Scheduler_1");
                        String Pin_d2_Scheduler_2 = jsonObject.getString("Pin_d2_Scheduler_2");
                        String Pin_d2_Scheduler_3 = jsonObject.getString("Pin_d2_Scheduler_3");
                        String Pin_d3_Scheduler_1 = jsonObject.getString("Pin_d3_Scheduler_1");
                        String Pin_d3_Scheduler_2 = jsonObject.getString("Pin_d3_Scheduler_2");
                        String Pin_d3_Scheduler_3 = jsonObject.getString("Pin_d3_Scheduler_3");
                        String Pin_d4_Scheduler_1 = jsonObject.getString("Pin_d4_Scheduler_1");
                        String Pin_d4_Scheduler_2 = jsonObject.getString("Pin_d4_Scheduler_2");
                        String Pin_d4_Scheduler_3 = jsonObject.getString("Pin_d4_Scheduler_3");
                        String Pin_d5_Scheduler_1 = jsonObject.getString("Pin_d5_Scheduler_1");
                        String Pin_d5_Scheduler_2 = jsonObject.getString("Pin_d5_Scheduler_2");
                        String Pin_d5_Scheduler_3 = jsonObject.getString("Pin_d5_Scheduler_3");
                        String Pin_d6_Scheduler_1 = jsonObject.getString("Pin_d6_Scheduler_1");
                        String Pin_d6_Scheduler_2 = jsonObject.getString("Pin_d6_Scheduler_2");
                        String Pin_d6_Scheduler_3 = jsonObject.getString("Pin_d6_Scheduler_3");
                        String Pin_d7_Scheduler_1 = jsonObject.getString("Pin_d7_Scheduler_1");
                        String Pin_d7_Scheduler_2 = jsonObject.getString("Pin_d7_Scheduler_2");
                        String Pin_d7_Scheduler_3 = jsonObject.getString("Pin_d7_Scheduler_3");
                        String Pin_d8_Scheduler_1 = jsonObject.getString("Pin_d8_Scheduler_1");
                        String Pin_d8_Scheduler_2 = jsonObject.getString("Pin_d8_Scheduler_2");
                        String Pin_d8_Scheduler_3 = jsonObject.getString("Pin_d8_Scheduler_3");
                        String Pin_d9_Scheduler_1 = jsonObject.getString("Pin_d9_Scheduler_1");
                        String Pin_d9_Scheduler_2 = jsonObject.getString("Pin_d9_Scheduler_2");
                        String Pin_d9_Scheduler_3 = jsonObject.getString("Pin_d9_Scheduler_3");


                        if (PinNum.equals("PIND1")) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d1_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d1_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d1_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);


                        } else if (PinNum.equals("PIND2")) {

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d2_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d2_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d2_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);



                        } else if (PinNum.equals("PIND3")) {


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d3_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d3_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d3_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);



                        } else if (PinNum.equals("PIND4")) {


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d4_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d4_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d4_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);



                        } else if (PinNum.equals("PIND5")) {


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d5_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d5_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d5_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);



                        } else if (PinNum.equals("PIND6")) {


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d6_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d6_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d6_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);




                        } else if (PinNum.equals("PIND7")) {


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d7_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d7_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d7_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);



                        } else if (PinNum.equals("PIND8")) {


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", Pin_d8_Scheduler_1);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap1);

                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("DateTime", Pin_d8_Scheduler_2);
                            hashMap2.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap2);

                            HashMap<String, String> hashMap3 = new HashMap<>();
                            hashMap3.put("DateTime", Pin_d8_Scheduler_3);
                            hashMap3.put(UTIL.Key_Scheduler_SavedAt,UTIL.Server);
                            list.add(hashMap3);


                        }
                        device_list_adapter adapter = new device_list_adapter(myContext, list);
                        scheduler_listview.setAdapter(adapter);


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
                pDialog.dismiss();
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



    public class device_list_adapter extends BaseAdapter {
        List<HashMap<String, String>> beanArrayList;
        Context context;
        int count = 1;

        public device_list_adapter(Context context, List<HashMap<String, String>> beanArrayList) {
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
            final String DateTime = beanArrayList.get(i).get("DateTime");
            final String Scheduler_SavedAt = beanArrayList.get(i).get(UTIL.Key_Scheduler_SavedAt);


            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_scheduler, null);


                holder.textvw_scheduler_count = (TextView) convertview.findViewById(R.id.textvw_scheduler_count);

                holder.textvw_scheduler_startDate = (TextView) convertview.findViewById(R.id.textvw_scheduler_startDate);
                holder.textvw_scheduler_endDate = (TextView) convertview.findViewById(R.id.textvw_scheduler_endDate);
                holder.textvw_scheduler_freq = (TextView) convertview.findViewById(R.id.textvw_scheduler_freq);


                holder.img_add_Scheduler = convertview.findViewById(R.id.img_add_Scheduler);
                holder.img_update_Scheduler = convertview.findViewById(R.id.img_update_Scheduler);
                holder.img_delete_Scheduler = convertview.findViewById(R.id.img_delete_Scheduler);


                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }


            if (i == 0) {
                holder.textvw_scheduler_count.setText(UTIL.Text_Scheduler_1);

            } else if (i == 1) {
                holder.textvw_scheduler_count.setText(UTIL.Text_Scheduler_2);

            } else if (i == 2) {
                holder.textvw_scheduler_count.setText(UTIL.Text_Scheduler_3);

            }

            // if (false) {
            if (DateTime == null || DateTime.equals("null") || DateTime.equals("0")|| DateTime.equals("-1")) {

                // holder.textvw_scheduler.setText(UTIL.Text_Scheduler_Not_Set);

                holder.textvw_scheduler_startDate.setText(UTIL.Text_Scheduler_Not_Set);
                holder.textvw_scheduler_endDate.setText(UTIL.Text_Scheduler_Not_Set);
                holder.textvw_scheduler_freq.setText(UTIL.Text_Scheduler_Not_Set);


                holder.img_add_Scheduler.setVisibility(View.VISIBLE);
                holder.img_update_Scheduler.setVisibility(View.GONE);
                holder.img_delete_Scheduler.setVisibility(View.GONE);
            } else {
                //holder.textvw_scheduler.setText(DateTime);

                String date_time = DateTime;
                date_time = date_time.replaceAll("_", " ");
                String[] dt = date_time.split(",");

                String freq = "";
                if (dt[2].equalsIgnoreCase("o")) {
                    freq = "Once";
                } else if (dt[2].equalsIgnoreCase("d")) {
                    freq = "Daily";
                } else if (dt[2].equalsIgnoreCase("w")) {
                    freq = "Weekly";
                } else if (dt[2].equalsIgnoreCase("m")) {
                    freq = "Monthly";
                }

                holder.textvw_scheduler_startDate.setText(dt[0]);
                holder.textvw_scheduler_endDate.setText(dt[1]);
                holder.textvw_scheduler_freq.setText(freq);


                holder.img_add_Scheduler.setVisibility(View.GONE);
                holder.img_update_Scheduler.setVisibility(View.VISIBLE);
                holder.img_delete_Scheduler.setVisibility(View.VISIBLE);
            }


            convertview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = (new Intent(myContext, Add_Update_Schedule_Device_A_Activity.class));
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("DeviceId", DeviceId);
                    intent.putExtra("PinNum", PinNum);
                    intent.putExtra("SchedulerNum", String.valueOf(i + 1));
                    intent.putExtra("DateTime", DateTime);
                    intent.putExtra(UTIL.Key_Scheduler_SavedAt, Scheduler_SavedAt);


                    startActivity(intent);
                }
            });

           /* holder.img_add_Scheduler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = (new Intent(myContext, Add_Update_Schedule_Device_A_Activity.class));
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("DeviceId", DeviceId);
                    intent.putExtra("PinNum", PinNum);
                    intent.putExtra("SchedulerNum", String.valueOf(i + 1));
                    intent.putExtra("DateTime", "0");
                    startActivity(intent);
                }
            });
            holder.img_update_Scheduler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = (new Intent(myContext, Add_Update_Schedule_Device_A_Activity.class));
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("DeviceId", DeviceId);
                    intent.putExtra("PinNum", PinNum);
                    intent.putExtra("SchedulerNum", String.valueOf(i + 1));
                    intent.putExtra("DateTime", DateTime);
                    startActivity(intent);
                }
            });
            holder.img_delete_Scheduler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/

            return convertview;
        }


        class Holder {
            TextView textvw_scheduler_count, textvw_scheduler_startDate, textvw_scheduler_endDate, textvw_scheduler_freq;

            //nks
            ImageView img_add_Scheduler, img_update_Scheduler, img_delete_Scheduler;


        }


    }

    private void check_local_scheduler(){
        //Now check if it exists in local shared preference
        String schedule = UTIL.getPref(myContext, UTIL.Key_Schedule);
        if (!schedule.equals("")) {
            try {
                JSONArray jsonArray_1 = new JSONArray(schedule);
                if (jsonArray_1.length() > 0) {

                    for (int i = 0; i < jsonArray_1.length(); i++) {
                        JSONObject jsonObject_1 = jsonArray_1.getJSONObject(i);
                        String str_deviceId = jsonObject_1.getString(UTIL.Key_DeviceId);
                        String str_pin_num = jsonObject_1.getString(UTIL.Key_Pin_Num);

                        if (str_deviceId.equals(DeviceId) && str_pin_num.equals(PinNum)) {
                            //means this scheduler is locally saved...
                            jsonObject_1.getString(UTIL.Key_Scheduler_Num);
                            jsonObject_1.getString(UTIL.Key_Scheduler_Time);
                            jsonObject_1.getString(UTIL.Key_Scheduler_SavedAt);






                        } else {
                            if (i == jsonArray_1.length() - 1) {
                                //this scheduler not exists locally



                            }

                        }
                    }
                } else {
                    // No scheduler exists in local

                }

            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            try {

            } catch (Exception e) {
                e.getMessage();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
