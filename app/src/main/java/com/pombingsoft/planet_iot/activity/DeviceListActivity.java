package com.pombingsoft.planet_iot.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.activity_new.Device_HomeActivity_new;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {
    ListView listview_devices;
    Context myContext;
    UTIL utill;
    TextView tv_msg;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    //  LinearLayout ll_AddDevice;
    AlertDialog alertDialog;
    String availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_1);
        myContext = DeviceListActivity.this;
        utill = new UTIL(myContext);
        listview_devices = findViewById(R.id.device_listview);
        tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);
        setFAB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isTimeAutomatic = UTIL.isTimeAutomatic(myContext);
        if (isTimeAutomatic) {
            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceList_ApiCall();
            } else {
                DialogInternet();
            }
        } else {
            dialog_AutoDateTimeSet();
        }
    }

    void getDeviceList_ApiCall() {
        list.clear();
        String tag_string_req = "req_login";
      /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting devices...");
        if (!pDialog.isShowing())
            pDialog.show();
       utill.showProgressDialog("Getting devices...");*/

        utill.showProgressDialog("Getting devices...");
        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // URL_LOGIN="http://ohmax.in/api/Arduino/DeviceSendStatusA?DeviceId=bukFEpkveI&Status_Pin_d0=0&Status_Pin_d1=0&Status_Pin_d2=0&Status_Pin_d3=0&Status_Pin_d4=0&Status_Pin_d5=0&Status_Pin_d6=0&Status_Pin_d7=0&Status_Pin_d8=0&Status_Pin_d9=0&error=0&errorMessage=-&Status_Dimmer_1=&Status_Dimmer_2=&Status_Dimmer_3=";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                list.clear();
                utill.hideProgressDialog();


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);


                            String UserId = jsonObject.getString("UserId");
                            String DeviceName = jsonObject.getString("DeviceName");
                            String DeviceId = jsonObject.getString("DeviceId");

                            String Device_LastSeen_dateTime = jsonObject.getString("LastSeen");
                            String LockStatus = jsonObject.getString("LockStatus");
                            String DeviceType = jsonObject.getString("DeviceType");
                            String Device_TimeZone = jsonObject.getString("TimeZone");

                            String IsError = jsonObject.getString("IsError");
                            String Device_TimeZoneID = jsonObject.getString("TimeZoneId");
                            String currentTime = utill.getCurrentTime(Device_TimeZone);
                            long TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);

                            Log.e("DeviceId", DeviceId);
                            Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            Log.e("currentTime", currentTime);
                            Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));

                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }


                            HashMap<String, String> hashMap = new HashMap<>();
                            //     hashMap.put("ID", ID);
                            hashMap.put("UserId", UserId);
                            //      hashMap.put("InsertDate", InsertDate);
                            hashMap.put("DeviceId", DeviceId);
                            hashMap.put("DeviceName", DeviceName);
                            hashMap.put("LockStatus", LockStatus);
                            hashMap.put("DeviceType", DeviceType);
                            hashMap.put("availability", availability);
                            hashMap.put("IsError", IsError);
                            //  hashMap.put("RoleId", RoleId);

                            list.add(hashMap);

                            //   }
                        }

                        if (list.size() < 1) {
                            tv_msg.setVisibility(View.VISIBLE);
                        } else {
                            tv_msg.setVisibility(View.GONE);
                        }


                    } else {
                        tv_msg.setVisibility(View.VISIBLE);

                        Toast.makeText(myContext,
                                "No devices found!", Toast.LENGTH_SHORT).show();

                    }

                    device_list_adapter adapter = new device_list_adapter(myContext, list);
                    listview_devices.setAdapter(adapter);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(myContext, "Some error occurred! ", Toast.LENGTH_SHORT).show();// + e.getMessage()
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                Toast.makeText(myContext,
                        "Error!", Toast.LENGTH_SHORT).show();

                listview_devices.setAdapter(null);

                tv_msg.setVisibility(View.VISIBLE);
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void setFAB() {
        final LinearLayout main_bg = findViewById(R.id.main_bg);
        final View actionB = findViewById(R.id.action_b);

       /* FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
*/

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        //menuMultipleActions.addButton(actionC);

        menuMultipleActions.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                main_bg.setAlpha(0.4f);
            }

            @Override
            public void onMenuCollapsed() {
                main_bg.setAlpha(01f);
            }
        });

        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();
                //  startActivity(new Intent(myContext, AddDeviceActivity.class));

                startActivity(new Intent(myContext, AddDeviceActivity_New.class));
            }
        });

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

    private void dialog_AutoDateTimeSet() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Your Phone Date is Inaccurate !");
        message.setText("Please set Automatic date and Time");
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);

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
            final String UserId = beanArrayList.get(i).get("UserId");
            final String DeviceId = beanArrayList.get(i).get("DeviceId");
            final String DeviceName = beanArrayList.get(i).get("DeviceName");
            final String LockStatus = beanArrayList.get(i).get("LockStatus");
            final String DeviceType = beanArrayList.get(i).get("DeviceType");
            final String availability = beanArrayList.get(i).get("availability");
            final String IsError = beanArrayList.get(i).get("IsError");

            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_device, null);

                holder.index_no = (Button) convertview.findViewById(R.id.serial_no);
                holder.DeviceName = (TextView) convertview.findViewById(R.id.DeviceName);
                holder.row_jobFile = (LinearLayout) convertview.findViewById(R.id.row_jobFile);
                //holder.DeviceCount = (TextView) convertview.findViewById(R.id.DeviceCount);
                holder.txt_availability = (TextView) convertview.findViewById(R.id.txt_availability);

                holder.img_online = (ImageView) convertview.findViewById(R.id.img_online);
                holder.img_offline = (ImageView) convertview.findViewById(R.id.img_offline);
                holder.img_notification_sign = (ImageView) convertview.findViewById(R.id.img_notification_sign);

                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }


            holder.index_no.setText(index1);
            holder.DeviceName.setText(DeviceName);
            // holder.DeviceCount.setText("DeviceNodeMcu: " + index1);

            if (availability.equalsIgnoreCase("Online")) {
                holder.txt_availability.setText("Online");
                holder.img_online.setVisibility(View.VISIBLE);
                holder.img_offline.setVisibility(View.GONE);
            } else if (availability.equalsIgnoreCase("Offline")) {
                holder.txt_availability.setText("Offline");
                holder.img_online.setVisibility(View.GONE);
                holder.img_offline.setVisibility(View.VISIBLE);
            } else {
                holder.txt_availability.setText("Error");
                holder.img_online.setVisibility(View.GONE);
                holder.img_offline.setVisibility(View.VISIBLE);
            }


            String str_device_type = UTIL.getDeviceType(DeviceType);
          /*  if (str_device_type.equalsIgnoreCase("A")) {
                holder.img_notification_sign.setVisibility(View.INVISIBLE);
            } else  {*/
            if (IsError != null && IsError.equals("1")) {  //means new notification received
                holder.img_notification_sign.setVisibility(View.VISIBLE);
            } else {
                holder.img_notification_sign.setVisibility(View.INVISIBLE);
            }

            // }


            holder.row_jobFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent i = new Intent(context, Device_HomeActivity_new.class);
                    //    Intent i = new Intent(context, Device_HomeActivity.class);
                    i.putExtra("UserId", UserId);
                    i.putExtra("DeviceId", DeviceId);
                    i.putExtra("DeviceName", DeviceName);
                    i.putExtra("LockStatus", LockStatus);
                    i.putExtra("DeviceType", DeviceType);
                    startActivity(i);


                }
            });

            return convertview;
        }


        class Holder {
            TextView DeviceName, txt_availability;
            // TextView DeviceCount;
            LinearLayout row_jobFile;
            Button index_no;
            //nks
            ImageView img_online, img_offline, img_notification_sign;


        }


    }


}
