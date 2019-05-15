package com.pombingsoft.planet_iot.activity;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class NotificationActivity extends AppCompatActivity {
String DeviceId,UserId,Device_Type;
    Context myContext;
    UTIL utill;
    TextView tv_msg;
    ListView notification_listview;
    AlertDialog alertDialog;
    ArrayList<HashMap<String, String>> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        myContext = NotificationActivity.this;
        utill = new UTIL(myContext);




        try {
            Intent i = getIntent();
            DeviceId = i.getStringExtra("DeviceId");
            UserId = i.getStringExtra("UserId");
            Device_Type = i.getStringExtra("DeviceType");

        } catch (Exception e) {
            e.getMessage();
        }
        tv_msg=findViewById(R.id.tv_msg);
        notification_listview=findViewById(R.id.notification_listview);





    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getNotification_ApiCall();
        } else {

            DialogInternet();
        }
    }
    private void DialogInternet() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title =  dialogView.findViewById(R.id.title);
        final TextView message =  dialogView.findViewById(R.id.message);
        final Button positiveBtn =  dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn =  dialogView.findViewById(R.id.negativeBtn);


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
    void getNotification_ApiCall() {

        list.clear();
        String tag_string_req = "req_login";
      utill.showProgressDialog("Getting data...");

        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceErrorList_API + "DeviceId=" + DeviceId;
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
                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String ErrId = jsonObject.getString("ErrId");
                            String DeviceId = jsonObject.getString("DeviceId");
                            String Message = jsonObject.getString("Message");
                            String DateTime = jsonObject.getString("DateTime");
                            String DeviceType = jsonObject.getString("DeviceType");


                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("ErrId", ErrId);
                            hashMap.put("DeviceId", DeviceId);
                            hashMap.put("Message", Message);
                            hashMap.put("DateTime", DateTime);
                            hashMap.put("DeviceType", DeviceType);
                            list.add(hashMap);

                        }

                    } else {
                        Toast.makeText(myContext,
                                "No notification(s) found!", Toast.LENGTH_SHORT).show();
                    }
                    if (list.size() < 1) {
                        tv_msg.setVisibility(View.VISIBLE);
                    } else {
                        tv_msg.setVisibility(View.GONE);
                    }
                    device_list_adapter adapter = new device_list_adapter(myContext, list);
                    notification_listview.setAdapter(adapter);
                    setNotificationRead_ApiCall();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
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
            final String ErrId = beanArrayList.get(i).get("ErrId");
            final String DeviceId = beanArrayList.get(i).get("DeviceId");
            final String Message = beanArrayList.get(i).get("Message");
             String DateTime = beanArrayList.get(i).get("DateTime");
            final String DeviceType = beanArrayList.get(i).get("DeviceType");



            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_notification, null);


            //    holder.index_no = (Button) convertview.findViewById(R.id.serial_no);
                holder.tv_error_msg = (TextView) convertview.findViewById(R.id.tv_error_msg);
                holder.tv_date_time = (TextView) convertview.findViewById(R.id.tv_date_time);
                holder.tv_sensor_name = (TextView) convertview.findViewById(R.id.tv_sensor_name);



                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }

            try {
                if (DateTime.contains("T")) {
                    DateTime = DateTime.replace("T", " ");
                    DateTime = DateTime.substring(0, DateTime.indexOf("."));
                }
                if (Message.contains("-")) {
                    String notification[] = Message.split("-");
                    if(notification.length>0) {
                        holder.tv_error_msg.setText(notification[1]);
                    }
                    holder.tv_date_time.setText(DateTime);
                    holder.tv_sensor_name.setText(notification[0]);
                } else {
                    holder.tv_error_msg.setText(Message);
                    holder.tv_date_time.setText(DateTime);
                    holder.tv_sensor_name.setText("");
                }
            }
            catch (Exception e){
                e.getMessage();
            }

            return convertview;
        }


        class Holder {
            TextView tv_error_msg, tv_date_time, tv_sensor_name;

        }


    }
    void setNotificationRead_ApiCall() {

        Log.e("api ","setNotificationRead_ApiCall");
        String tag_string_req = "req_login";
        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.SetNotificationRead_API + "DeviceId=" + DeviceId+"&DeviceType="+Device_Type+"&UserId="+UserId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


                Log.e("api err","setNotificationRead_ApiCall");


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
