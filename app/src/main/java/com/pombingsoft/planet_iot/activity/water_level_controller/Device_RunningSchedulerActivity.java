package com.pombingsoft.planet_iot.activity.water_level_controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Device_RunningSchedulerActivity extends AppCompatActivity {
    Context myContext;
    UTIL utill;

    String UserId, DeviceId, DeviceName, Device_Type;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ListView scheduler_listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rnning_scheduler);

        myContext = Device_RunningSchedulerActivity.this;
        utill = new UTIL(myContext);
        scheduler_listview = findViewById(R.id.scheduler_listview);


        try {
            Intent i = getIntent();
            DeviceId = i.getStringExtra("DeviceId");
            DeviceName = i.getStringExtra("DeviceName");
            UserId = i.getStringExtra("UserId");
            Device_Type = i.getStringExtra("Device_Type");

        } catch (Exception e) {
            e.getMessage();
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        ArrayList<String> arrayList_device_type_Switch =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
        ArrayList<String> arrayList_device_type_WaterLevelController =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
        ArrayList<String> arrayList_device_type_AirQualityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));
        ArrayList<String> arrayList_device_type_SecurityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {

                getDeviceDetailsB_ApiCall();

            } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {

                getDeviceDetailsD_ApiCall();

            }


        } else {
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }


    }


    void getDeviceDetailsB_ApiCall() {
        list.clear();
        String tag_string_req = "req_login";

        utill.showProgressDialog("Getting scheduler data...");

       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        String URL_LOGIN = null;

        //  String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsB_API + "DeviceId=" + DeviceId + "&UserId=" + UserId;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
utill.hideProgressDialog();
                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                      //  String DeviceId = jsonObject.getString("DeviceId");
                      //  String DeviceName = jsonObject.getString("DeviceName");
                      /*  String Device_Password = jsonObject.getString("Device_Password");


                        String Tank_setting_type = jsonObject.getString("Tank_type");

                        String Tank_function_state = jsonObject.getString("Tank_function_state");
                        String Pump_Mode = jsonObject.getString("Pump_Mode");

                        String Upper_Depth = jsonObject.getString("Upper_Depth");
                        String Upper_Capacity = jsonObject.getString("Upper_Capacity");


                        String Pump_on_time_max = jsonObject.getString("Pump_on_time_max");
                        String Pump_cool_time_min = jsonObject.getString("Pump_cool_time_min");
*/

                        String Schedule_1 = jsonObject.getString("Schedule_1");
                        String Schedule_2 = jsonObject.getString("Schedule_2");
                        String Schedule_3 = jsonObject.getString("Schedule_3");


                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("DateTime", Schedule_1);
                        hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Server);
                        list.add(hashMap1);

                        HashMap<String, String> hashMap2 = new HashMap<>();
                        hashMap2.put("DateTime", Schedule_2);
                        hashMap2.put(UTIL.Key_Scheduler_SavedAt, UTIL.Server);
                        list.add(hashMap2);

                        HashMap<String, String> hashMap3 = new HashMap<>();
                        hashMap3.put("DateTime", Schedule_3);
                        hashMap3.put(UTIL.Key_Scheduler_SavedAt, UTIL.Server);
                        list.add(hashMap3);


                        // et_TILDeviceName.setHint("Device Name");
                        //   et_TILDevicePass.setHint("Device Password");
                        //     et_DeviceName.setText(DeviceName);
                        //    et_DevicePass.setText(Device_Password);


                        device_list_adapter adapter = new device_list_adapter(myContext, list);
                        scheduler_listview.setAdapter(adapter);


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
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
                //  pDialog.dismiss();
                //utill.hideProgressDialog();
                utill.hideProgressDialog();
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

    void getDeviceDetailsD_ApiCall() {
        list.clear();
        String tag_string_req = "req_login";
        utill.showProgressDialog("Getting scheduler data...");
       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsD_API + "DeviceId=" + DeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();

                //  pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        String DeviceName = jsonObject.getString("DeviceName");
                      /*  String Device_Password = jsonObject.getString("Device_Password");

                        String Tank_setting_type = jsonObject.getString("Tank_type");

                        String Tank_function_state = jsonObject.getString("Tank_function_state");
                        String Pump_Mode = jsonObject.getString("Pump_Mode");

                        String Upper_Depth = jsonObject.getString("Upper_Depth");
                        String Upper_Capacity = jsonObject.getString("Upper_Capacity");

                        String Pump_on_time_max = jsonObject.getString("Pump_on_time_max");
                        String Pump_cool_time_min = jsonObject.getString("Pump_cool_time_min");
*/

                        String Schedule_1 = jsonObject.getString("Schedule_1");
                        String Schedule_2 = jsonObject.getString("Schedule_2");
                        String Schedule_3 = jsonObject.getString("Schedule_3");


                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("DateTime", Schedule_1);
                        hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Server);
                        list.add(hashMap1);

                        HashMap<String, String> hashMap2 = new HashMap<>();
                        hashMap2.put("DateTime", Schedule_2);
                        hashMap2.put(UTIL.Key_Scheduler_SavedAt, UTIL.Server);
                        list.add(hashMap2);

                        HashMap<String, String> hashMap3 = new HashMap<>();
                        hashMap3.put("DateTime", Schedule_3);
                        hashMap3.put(UTIL.Key_Scheduler_SavedAt, UTIL.Server);
                        list.add(hashMap3);


                        // et_TILDeviceName.setHint("Device Name");
                        //   et_TILDevicePass.setHint("Device Password");
                        //     et_DeviceName.setText(DeviceName);
                        //    et_DevicePass.setText(Device_Password);


                        device_list_adapter adapter = new device_list_adapter(myContext, list);
                        scheduler_listview.setAdapter(adapter);


                    } else {
                        Toast.makeText(myContext,
                                "No data received!", Toast.LENGTH_SHORT).show();
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
                //  pDialog.dismiss();
                //utill.hideProgressDialog();
                utill.hideProgressDialog();
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
            if (DateTime == null || DateTime.equals("null") || DateTime.equals("0") || DateTime.equals("-1") || DateTime.equals("")) {

                // holder.textvw_scheduler.setText(UTIL.Text_Scheduler_Not_Set);

                holder.textvw_scheduler_startDate.setText(UTIL.Text_Scheduler_Not_Set);
                  holder.textvw_scheduler_endDate.setText(UTIL.Text_Scheduler_Not_Set);
                  holder.textvw_scheduler_freq.setText(UTIL.Text_Scheduler_Not_Set);


            } else {
                //holder.textvw_scheduler.setText(DateTime);

                String date_time = DateTime;
                date_time = date_time.replaceAll("_", " ");
                String[] dt = date_time.split(",");
                String freq = "";
                String startDate = "";
                String endDate= "";

                try{
                    startDate = dt[0];
                }
                catch (Exception e){
                    e.getMessage();
                }
                try{
                    endDate=dt[1];
                }
                catch (Exception e){
                    e.getMessage();
                }

                try {
                    if (dt[2].equalsIgnoreCase("o")) {
                        freq = "Once";
                    } else if (dt[2].equalsIgnoreCase("d")) {
                        freq = "Daily";
                    } else if (dt[2].equalsIgnoreCase("w")) {
                        freq = "Weekly";
                    } else if (dt[2].equalsIgnoreCase("m")) {
                        freq = "Monthly";
                    }
                }
                catch (Exception e){
                    e.getMessage();
                }
                holder.textvw_scheduler_startDate.setText(startDate);
                holder.textvw_scheduler_endDate.setText(endDate);
                holder.textvw_scheduler_freq.setText(freq);

/*
                holder.img_add_Scheduler.setVisibility(View.GONE);
                holder.img_update_Scheduler.setVisibility(View.VISIBLE);
                holder.img_delete_Scheduler.setVisibility(View.VISIBLE);
         */   }


            convertview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = (new Intent(myContext, AddUpdateSchedule_Device_B_D_Activity.class));
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("DeviceId", DeviceId);
                    intent.putExtra("DateTime", DateTime);
                    intent.putExtra("Device_Type", Device_Type);
                    intent.putExtra("SchedulerNum", String.valueOf(i + 1));

                    intent.putExtra(UTIL.Key_Scheduler_SavedAt, Scheduler_SavedAt);


                    startActivity(intent);
                }
            });


            return convertview;
        }


        class Holder {

            TextView textvw_scheduler_count, textvw_scheduler_startDate, textvw_scheduler_endDate, textvw_scheduler_freq;

            //   TextView   textvw_scheduler_endDate, textvw_scheduler_freq;
            //nks


        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
