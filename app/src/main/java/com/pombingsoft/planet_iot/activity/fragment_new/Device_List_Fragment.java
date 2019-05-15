package com.pombingsoft.planet_iot.activity.fragment_new;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
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
import com.pombingsoft.planet_iot.activity.ActivityWithNavigationMenu;
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

import static android.content.Context.NOTIFICATION_SERVICE;

/*import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;*/

/**
 * Created by Admin on 3/1/2018.
 */

public class Device_List_Fragment extends Fragment {
    ListView listview_devices;
    Context myContext;
    UTIL utill;
    TextView tv_msg;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    //  LinearLayout ll_AddDevice;
    AlertDialog alertDialog;
    String availability;
    LinearLayout main_bg;
    // View actionB;

    // FloatingActionsMenu menuMultipleActions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgmnt_device_list, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);
        listview_devices = rootView.findViewById(R.id.device_listview);
        tv_msg = rootView.findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        main_bg = rootView.findViewById(R.id.main_bg);
        getActivity().setTitle("Device List");
        //  getActivity().setTitle(Html.fromHtml("<font color='#fff'>Device List </font>"));
        setFAB(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isTimeAutomatic = UTIL.isTimeAutomatic(myContext);
        if (isTimeAutomatic) {
            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                //  getDeviceList_ApiCall();
                getDeviceList_ApiCall_New();
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
                            String currentTime = utill.getCurrentTime(Device_TimeZoneID);
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

    void getDeviceList_ApiCall_New() {

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
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_New_API + "uid=" + Uid;

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

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray_A = jsonObject.getJSONArray("Table");
                    JSONArray jsonArray_B = jsonObject.getJSONArray("Table1");
                    JSONArray jsonArray_C = jsonObject.getJSONArray("Table2");
                    JSONArray jsonArray_D = jsonObject.getJSONArray("Table3");


/*****************************************A***************************************/
                    if (jsonArray_A.length() > 0) {

                        for (int i = 0; i < jsonArray_A.length(); i++) {

                            JSONObject jsonObjectA = jsonArray_A.getJSONObject(i);
                            String UserId = jsonObjectA.getString("UserId");
                            String DeviceName = jsonObjectA.getString("DeviceName");
                            String DeviceId = jsonObjectA.getString("DeviceId").trim();

                            String Device_LastSeen_dateTime = jsonObjectA.getString("LastSeen");
                            String LockStatus = jsonObjectA.getString("LocakStatus");
                            String DeviceType = jsonObjectA.getString("DeviceType");
                            String Device_TimeZone = jsonObjectA.getString("TimeZone");

                            String IsError = jsonObjectA.getString("IsError");
                            String Device_TimeZoneID = jsonObjectA.getString("TimeZoneId");
                            String currentTime = utill.getCurrentTime(Device_TimeZone);
                            long TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);




                            /**/
                            String Status_Pin_d0 = jsonObjectA.getString("Status_Pin_d0");
                            String Status_Pin_d1 = jsonObjectA.getString("Status_Pin_d1");
                            String Status_Pin_d2 = jsonObjectA.getString("Status_Pin_d2");
                            String Status_Pin_d3 = jsonObjectA.getString("Status_Pin_d3");
                            String Status_Pin_d4 = jsonObjectA.getString("Status_Pin_d4");
                            String Status_Pin_d5 = jsonObjectA.getString("Status_Pin_d5");
                            String Status_Pin_d6 = jsonObjectA.getString("Status_Pin_d6");
                            String Status_Pin_d7 = jsonObjectA.getString("Status_Pin_d7");
                            String Status_Pin_d8 = jsonObjectA.getString("Status_Pin_d8");
                            String Status_Pin_d9 = jsonObjectA.getString("Status_Pin_d9");
                            String Dimmer_1 = jsonObjectA.getString("Dimmer_1");
                            String Dimmer_2 = jsonObjectA.getString("Dimmer_2");
                            String Dimmer_3 = jsonObjectA.getString("Dimmer_3");
                            /**/
                            Log.e("DeviceId", DeviceId);
                            Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            Log.e("currentTime", currentTime);
                            Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));
                            Log.e("**********", "*********");

                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }

                            Log.e("availability", availability);

                            if (!DeviceId.equals("")) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("UserId", UserId);
                                hashMap.put("DeviceId", DeviceId);
                                hashMap.put("DeviceName", DeviceName);
                                hashMap.put("LockStatus", LockStatus);
                                hashMap.put("DeviceType", DeviceType);
                                hashMap.put("availability", availability);
                                hashMap.put("IsError", IsError);

                                hashMap.put("Status_Pin_d0", Status_Pin_d0);
                                hashMap.put("Status_Pin_d1", Status_Pin_d1);
                                hashMap.put("Status_Pin_d2", Status_Pin_d2);
                                hashMap.put("Status_Pin_d3", Status_Pin_d3);
                                hashMap.put("Status_Pin_d4", Status_Pin_d4);
                                hashMap.put("Status_Pin_d5", Status_Pin_d5);
                                hashMap.put("Status_Pin_d6", Status_Pin_d6);
                                hashMap.put("Status_Pin_d7", Status_Pin_d7);
                                hashMap.put("Status_Pin_d8", Status_Pin_d8);
                                hashMap.put("Status_Pin_d9", Status_Pin_d9);

                                hashMap.put("Dimmer_1", Dimmer_1);
                                hashMap.put("Dimmer_2", Dimmer_2);
                                hashMap.put("Dimmer_3", Dimmer_3);


                                list.add(hashMap);
                            }

                        }


                    }


/*************************************B*******************************************/

                    if (jsonArray_B.length() > 0) {

                        for (int i = 0; i < jsonArray_B.length(); i++) {

                            JSONObject jsonObjectB = jsonArray_B.getJSONObject(i);
                            String UserId = jsonObjectB.getString("UserId");
                            String DeviceName = jsonObjectB.getString("DeviceName");
                            String DeviceId = jsonObjectB.getString("DeviceId").trim();

                            String Device_LastSeen_dateTime = jsonObjectB.getString("LastSeen");
                            String LockStatus = jsonObjectB.getString("LocakStatus");
                            String DeviceType = jsonObjectB.getString("DeviceType");
                            String Device_TimeZone = jsonObjectB.getString("TimeZone");

                            String IsError = jsonObjectB.getString("IsError");
                            String Device_TimeZoneID = jsonObjectB.getString("TimeZoneId");
                            String currentTime = utill.getCurrentTime(Device_TimeZone);

                            /**/


                            /**/


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
                            if (!DeviceId.equals("")) {

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("UserId", UserId);
                                hashMap.put("DeviceId", DeviceId);
                                hashMap.put("DeviceName", DeviceName);
                                hashMap.put("LockStatus", LockStatus);
                                hashMap.put("DeviceType", DeviceType);
                                hashMap.put("availability", availability);
                                hashMap.put("IsError", IsError);


                                list.add(hashMap);
                            }

                        }


                    }


/****************************************C****************************************/

                    if (jsonArray_C.length() > 0) {

                        for (int i = 0; i < jsonArray_C.length(); i++) {

                            JSONObject jsonObjectC = jsonArray_C.getJSONObject(i);


                            String UserId = jsonObjectC.getString("UserId");
                            String DeviceName = jsonObjectC.getString("DeviceName");
                            String DeviceId = jsonObjectC.getString("DeviceId").trim();

                            String Device_LastSeen_dateTime = jsonObjectC.getString("LastSeen");
                            String LockStatus = jsonObjectC.getString("LocakStatus");
                            String DeviceType = jsonObjectC.getString("DeviceType");
                            String Device_TimeZone = jsonObjectC.getString("TimeZone");

                            String IsError = jsonObjectC.getString("IsError");
                            String Device_TimeZoneID = jsonObjectC.getString("TimeZoneId");
                            String currentTime = utill.getCurrentTime(Device_TimeZone);
                            String Buzzer_State = jsonObjectC.getString("Buzzer_State");

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

                            if (!DeviceId.equals("")) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("UserId", UserId);
                                hashMap.put("DeviceId", DeviceId);
                                hashMap.put("DeviceName", DeviceName);
                                hashMap.put("LockStatus", LockStatus);
                                hashMap.put("DeviceType", DeviceType);
                                hashMap.put("availability", availability);
                                hashMap.put("IsError", IsError);
                                hashMap.put("Buzzer_State", Buzzer_State);

                                list.add(hashMap);
                            }

                        }


                    }


/*******************************************D*************************************/

                    if (jsonArray_D.length() > 0) {


                        for (int i = 0; i < jsonArray_D.length(); i++) {

                            JSONObject jsonObjectD = jsonArray_D.getJSONObject(i);


                            String UserId = jsonObjectD.getString("UserId");
                            String DeviceName = jsonObjectD.getString("DeviceName");
                            String DeviceId = jsonObjectD.getString("DeviceId").trim();

                            String Device_LastSeen_dateTime = jsonObjectD.getString("LastSeen");
                            String LockStatus = jsonObjectD.getString("LocakStatus");
                            String DeviceType = jsonObjectD.getString("DeviceType");
                            String Device_TimeZone = jsonObjectD.getString("TimeZone");

                            String IsError = jsonObjectD.getString("IsError");
                            String Device_TimeZoneID = jsonObjectD.getString("TimeZoneId");
                            String currentTime = utill.getCurrentTime(Device_TimeZone);

                            /**/
                            String Flood_state = jsonObjectD.getString("Flood_Level");
                            String Fire_state = jsonObjectD.getString("Fire_Alarm");
                            /**/

                            long TotalelapsedSeconds = utill.timeDiffereceInSeconds(currentTime, Device_LastSeen_dateTime);


                            Log.e("DeviceId", DeviceId);
                            Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            Log.e("currentTime", currentTime);
                            Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));

                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online_For_Device_D) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }

                            if (!DeviceId.equals("")) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("UserId", UserId);
                                hashMap.put("DeviceId", DeviceId);
                                hashMap.put("DeviceName", DeviceName);
                                hashMap.put("LockStatus", LockStatus);
                                hashMap.put("DeviceType", DeviceType);
                                hashMap.put("availability", availability);
                                hashMap.put("IsError", IsError);

                                hashMap.put("Flood_state", Flood_state);
                                hashMap.put("Fire_state", Fire_state);


                                list.add(hashMap);
                            }
                        }

                    }
/********************************************************************************/
                    if (list.size() < 1) {
                        tv_msg.setVisibility(View.VISIBLE);
                        Toast.makeText(myContext,
                                "No devices found!", Toast.LENGTH_SHORT).show();
                    } else {
                        tv_msg.setVisibility(View.GONE);
                    }


                    device_list_adapter adapter = new device_list_adapter(myContext, list);
                    listview_devices.setAdapter(adapter);

                    SendOfflineDeviceLocalNotification();

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

    private void DialogInternet() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(myContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                getActivity().finish();
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

    private void dialog_AutoDateTimeSet() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
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

    private void setFAB(View rootView) {

       /* actionB = rootView.findViewById(R.id.action_b);
        menuMultipleActions = (FloatingActionsMenu)rootView. findViewById(R.id.multiple_actions);
*/
        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.fab_addNewDevice);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityWithNavigationMenu) getActivity()).AddNewDeviceFragment();

            }
        });

        //final LinearLayout main_bg = roofindViewById(R.id.main_bg);
        //   final View actionB = findViewById(R.id.action_b);

       /* FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
      */

        //final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        //menuMultipleActions.addButton(actionC);
/*
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

              //  startActivity(new Intent(myContext, AddDeviceActivity_New.class));

                ((ActivityWithNavigationMenu)getActivity()).AddNewDeviceFragment();


            }
        });*/

    }

    private void sendMyNotification(String message) {

        //On click of notification it redirect to this Activity
        Intent intent = new Intent(getActivity(), ActivityWithNavigationMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Home Automation")
                .setContentText(message)
                .setAutoCancel(true)

                .setSound(soundUri);
        //.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(UTIL.random_number_notificationId(), notificationBuilder.build());


    }

    public void startNotification(String message) {
        // TODO Auto-generated method stub
        NotificationCompat.Builder notification;
        PendingIntent pIntent;
        NotificationManager manager;

        TaskStackBuilder stackBuilder;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            final String channelId = "my_channel_01";
            final String channelName = "channel name";
            final NotificationChannel defaultChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
            manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(defaultChannel);
            }


            Notification notification2 = new Notification.Builder(getActivity())
                    .setContentTitle("Home Automation")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .build();


            manager.notify(UTIL.random_number_notificationId(), notification2);

        } else {
            //Creating Notification Builder
            notification = new NotificationCompat.Builder(getActivity());
            //Title for Notification
            notification.setContentTitle("Home Automation");
            //Message in the Notification
            notification.setContentText(message);
            //Alert shown when Notification is received

            //Icon to be set on Notification
            notification.setSmallIcon(R.mipmap.ic_launcher);

            /*nks*/
      /*  notification.setCategory(NotificationCompat.CATEGORY_SERVICE);
        notification.setPriority(NotificationCompat.PRIORITY_MIN);*/
            notification.setAutoCancel(true);

            manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            manager.notify(UTIL.random_number_notificationId(), notification.build());

        }


    }

    public void SendOfflineDeviceLocalNotification() {

        for (HashMap<String, String> map : list) {
            String status = map.get("availability");
            String Device_name = map.get("DeviceName");
            if (status.equalsIgnoreCase("Offline")) {
                //  sendMyNotification(Device_name + " is offline!");
                startNotification(Device_name + " is offline!");
            }
        }

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

            final device_list_adapter.Holder holder;
            final String index1 = String.valueOf(i + 1);
            final String UserId = beanArrayList.get(i).get("UserId");
            final String DeviceId = beanArrayList.get(i).get("DeviceId");
            final String DeviceName = beanArrayList.get(i).get("DeviceName");
            final String LockStatus = beanArrayList.get(i).get("LockStatus");
            final String DeviceType = beanArrayList.get(i).get("DeviceType");
            final String availability = beanArrayList.get(i).get("availability");
            final String IsError = beanArrayList.get(i).get("IsError");

            if (convertview == null) {
                holder = new device_list_adapter.Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_device, null);

                holder.index_no = (Button) convertview.findViewById(R.id.serial_no);
                holder.DeviceName = (TextView) convertview.findViewById(R.id.DeviceName);
                holder.row_jobFile = (LinearLayout) convertview.findViewById(R.id.row_jobFile);
                //holder.DeviceCount = (TextView) convertview.findViewById(R.id.DeviceCount);
                holder.txt_availability = (TextView) convertview.findViewById(R.id.txt_availability);

                holder.img_online = (ImageView) convertview.findViewById(R.id.img_online);
                holder.img_offline = (ImageView) convertview.findViewById(R.id.img_offline);
                holder.img_notification_sign = (ImageView) convertview.findViewById(R.id.img_notification_sign);


                /**/
                holder.switch_btn = convertview.findViewById(R.id.switch_btn);
                holder.switchButton_buzzerC = convertview.findViewById(R.id.switchButton_buzzer);
                holder.imgvw_Flood = convertview.findViewById(R.id.imgvw_Flood);
                holder.switchButton_buzzer_D = convertview.findViewById(R.id.switchButton_buzzer_D);

                holder.switch_btn.setVisibility(View.GONE);
                holder.switchButton_buzzerC.setVisibility(View.GONE);
                holder.imgvw_Flood.setVisibility(View.GONE);
                holder.switchButton_buzzer_D.setVisibility(View.GONE);

                /**/


                convertview.setTag(holder);
            } else {
                holder = (device_list_adapter.Holder) convertview.getTag();
            }


            holder.index_no.setText(index1);
            holder.DeviceName.setText(DeviceName);
            // holder.DeviceCount.setText("Device: " + index1);

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
           /* if (str_device_type.equalsIgnoreCase("A")) {
                holder.img_notification_sign.setVisibility(View.INVISIBLE);
            } else {*/
            if (IsError != null && IsError.equals("1")) {  //means new notification received
                holder.img_notification_sign.setVisibility(View.VISIBLE);
            } else {
                holder.img_notification_sign.setVisibility(View.INVISIBLE);
            }

            // }

            /***************************************************************************/
            try {
                if (str_device_type.equalsIgnoreCase("A")) {


                    final String Status_Pin_d0 = beanArrayList.get(i).get("Status_Pin_d0");
                    final String Status_Pin_d1 = beanArrayList.get(i).get("Status_Pin_d1");
                    final String Status_Pin_d2 = beanArrayList.get(i).get("Status_Pin_d2");
                    final String Status_Pin_d3 = beanArrayList.get(i).get("Status_Pin_d3");
                    final String Status_Pin_d4 = beanArrayList.get(i).get("Status_Pin_d4");
                    final String Status_Pin_d5 = beanArrayList.get(i).get("Status_Pin_d5");
                    final String Status_Pin_d6 = beanArrayList.get(i).get("Status_Pin_d6");
                    final String Status_Pin_d7 = beanArrayList.get(i).get("Status_Pin_d7");
                    final String Status_Pin_d8 = beanArrayList.get(i).get("Status_Pin_d8");
                    final String Status_Pin_d9 = beanArrayList.get(i).get("Status_Pin_d9");
                    final String Dimmer_1 = beanArrayList.get(i).get("Dimmer_1");
                    final String Dimmer_2 = beanArrayList.get(i).get("Dimmer_2");
                    final String Dimmer_3 = beanArrayList.get(i).get("Dimmer_3");

                    holder.switchButton_buzzerC.setVisibility(View.GONE);
                    holder.imgvw_Flood.setVisibility(View.GONE);
                    holder.switchButton_buzzer_D.setVisibility(View.GONE);


                    if (Status_Pin_d0.equals("1") || Status_Pin_d1.equals("1") || Status_Pin_d2.equals("1")
                            || Status_Pin_d3.equals("1") || Status_Pin_d4.equals("1") || Status_Pin_d5.equals("1")
                            || Status_Pin_d6.equals("1") || Status_Pin_d7.equals("1") || Status_Pin_d8.equals("1")
                            || Status_Pin_d9.equals("1")
                            ) {

                        holder.switch_btn.setVisibility(View.VISIBLE);
                        // holder.switch_btn.setChecked(true);

                    } else {
                        holder.switch_btn.setVisibility(View.GONE);
                    }
                } else if (str_device_type.equalsIgnoreCase("B")) {
                    holder.switch_btn.setVisibility(View.GONE);
                    holder.switchButton_buzzerC.setVisibility(View.GONE);
                    holder.imgvw_Flood.setVisibility(View.GONE);
                    holder.switchButton_buzzer_D.setVisibility(View.GONE);
                } else if (str_device_type.equalsIgnoreCase("C")) {


                    holder.switch_btn.setVisibility(View.GONE);
                    holder.imgvw_Flood.setVisibility(View.GONE);
                    holder.switchButton_buzzer_D.setVisibility(View.GONE);


                    final String Buzzer_State = beanArrayList.get(i).get("Buzzer_State");
                    if (Buzzer_State != null && Buzzer_State.equals("1")) {
                        holder.switchButton_buzzerC.setVisibility(View.VISIBLE);
                        // holder.switchButton_buzzerC.setChecked(true);
                    } else {
                        holder.switchButton_buzzerC.setVisibility(View.GONE);
                    }

                } else if (str_device_type.equalsIgnoreCase("D")) {

                    holder.switch_btn.setVisibility(View.GONE);
                    holder.switchButton_buzzerC.setVisibility(View.GONE);

                    final String Flood_state = beanArrayList.get(i).get("Flood_state");
                    final String Fire_state = beanArrayList.get(i).get("Fire_state");

                    if (UTIL.is_Device_With_FLOOD_Sensor(DeviceType)) {
                        if (Flood_state.equalsIgnoreCase("1") || Flood_state.equalsIgnoreCase("2")) {
                            holder.imgvw_Flood.setVisibility(View.VISIBLE);
                            holder.imgvw_Flood.setImageResource(R.drawable.danger);

                        } else {
                            holder.imgvw_Flood.setVisibility(View.GONE);
                        }
                    }

                    if (UTIL.is_Device_With_FIRE_Sensor(DeviceType)) {
                        if (DeviceType.equals("D2") || DeviceType.equals("D3")) {
                            if (Fire_state == null || Fire_state.equalsIgnoreCase("0")) {
                                holder.switchButton_buzzer_D.setVisibility(View.GONE);
                            } else {
                                holder.switchButton_buzzer_D.setVisibility(View.VISIBLE);
                                // holder.switchButton_buzzer_D.setChecked(true);

                            }
                        }
                    }


                }
            } catch (Exception e) {
                e.getMessage();
            }

            /**************************************************************************/


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
            ImageView switch_btn;
            ImageView switchButton_buzzerC;

            ImageView imgvw_Flood;
            ImageView switchButton_buzzer_D;
        }


    }
}