package com.pombingsoft.planet_iot.activity.fragment_new;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.ActivityWithNavigationMenu;
import com.pombingsoft.planet_iot.location.common.activities.SampleActivityBase_New;
import com.pombingsoft.planet_iot.location.common.logger.Log;
import com.pombingsoft.planet_iot.model.DeviceNodeMcu;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.Myspinner_timezone;
import com.pombingsoft.planet_iot.util.TimeZoneExample;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


public class UpdateTimeZone_Fragment_New extends Fragment {

    /***************************/
    Context myContext;
    UTIL utill;
    Spinner spin_Device;

    Button btnAddDevice;

    AutoCompleteTextView autoText_, searchableSpinner_device;
    AlertDialog alertDialog1;


    ArrayList<HashMap<String, String>> List_timeZone = new ArrayList<>();

    String Time_Zone_Name = "";
    String Time_Zone_Id = "";

    String Device_SSID = "";
    int Try_Count = 0;
    AlertDialog alertDialog;
    String Device_Type;
    String readDeviceId;
    ProgressDialog progressDialog;
    ArrayList<DeviceNodeMcu> list_DeviceNodeMcuNew = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgmnt_update_timezone_new, container, false);

        myContext = getActivity();
        utill = new UTIL(myContext);
        getActivity().setTitle("Update Device Time Zone");
        init_views(rootView);

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getTimeZoneList();
        } else {
            DialogInternet();
        }

        return rootView;
    }


    private void init_views(View rootView) {
        spin_Device = (Spinner) rootView.findViewById(R.id.spin_Device);

        btnAddDevice = rootView.findViewById(R.id.btnAddDevice);
        searchableSpinner_device = rootView.findViewById(R.id.searchableSpinner_device);
        searchableSpinner_device.setHint(UTIL.SelectDevice);
        searchableSpinner_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSearchableSpinnerDialog(searchableSpinner_device, list_DeviceNodeMcuNew, "Device(NodeMcu)");

            }
        });


        autoText_ = rootView.findViewById(R.id.autoText_);
        autoText_.setHint(UTIL.SelectTimeZone);
        autoText_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> arrayList = TimeZoneExample.getAllTimeZone();
                //   showSearchableSpinnerDialog(autoText_, arrayList, "Time Zone");
                showSearchableSpinnerDialog_TimeZone(autoText_, List_timeZone, "Time Zone");

            }
        });

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //  int selectedDevicePos = searchableSpinner_device.getSelectedItemPosition();
                Device_SSID = searchableSpinner_device.getText().toString().trim();


                String TimeZone = autoText_.getText().toString().trim();

                if (Device_SSID.trim().equals("") || Device_SSID.equals(UTIL.SelectDevice)) {
                    Toast.makeText(myContext, "Please select a Device!", Toast.LENGTH_SHORT).show();
                } else if (TimeZone.equals("")) {
                    Toast.makeText(myContext, "Please select Time Zone!", Toast.LENGTH_SHORT).show();

                } else if (TimeZone.isEmpty()) {
                    Toast.makeText(myContext, "Please enter correct Time Zone!", Toast.LENGTH_SHORT).show();

                } else {

                    //    String[] device_desc = Device_SSID.split("_");//tNode_A1,tNode_A2,tNode_A4,tNode_A8,tNode_B,tNode_C
                    //   Device_Type = device_desc[1];//A1,A2,A4,A8,B,C1,C2,C3,C4
                    // connectWithDevice_readDeviceID();
                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        getDeviceDetail();
                    } else {
                        HideProgressDialog();
                        DialogInternet();
                    }


                }
            }
        });
    }


    private void getTimeZoneList() {
        List_timeZone.clear();
        Log.e("API---", "getTimeZoneList");
        String tag_string_req = "req_login";
        utill.showProgressDialog("");

        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.getTimeZoneList_API;

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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String Country_Code = jsonObject.getString("Country_Code");
                        String Country_Name = jsonObject.getString("Country_Name");
                        String Time_Zone = jsonObject.getString("Time_Zone");
                        String GMT_Offset = jsonObject.getString("GMT_Offset");

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("Country_Code", Country_Code);
                        hashMap.put("Country_Name", Country_Name);
                        hashMap.put("Time_Zone", Time_Zone);
                        hashMap.put("GMT_Offset", GMT_Offset);

                        List_timeZone.add(hashMap);
                    }


                } catch (JSONException e) {
                    // JSON error
                    //  HideProgressDialog();
                    e.printStackTrace();
                    //   Toast.makeText(myContext, " Some error occurred! " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("API error---", "getTimeZoneList");

                    Dialog_showMessage("Error!", "Unable to get Time Zone\nPlease try again");

                }
                //  HideProgressDialog();
                //   getAvailableNodeMcu();
                getDeviceList_ApiCall_New();
                //     setData_Spinners();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  HideProgressDialog();
                utill.hideProgressDialog();
                //  Toast.makeText(myContext, "Volly error: ", Toast.LENGTH_LONG).show();
                Log.e("API error---", "getTimeZoneList");
                Dialog_showMessage("Error!", "Unable to get Time Zone\nPlease try again");

            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void showSearchableSpinnerDialog(final AutoCompleteTextView autoCompleteTextView, final ArrayList<DeviceNodeMcu> data, String dialog_title) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final ArrayList<DeviceNodeMcu> list = new ArrayList<>();
        final EditText autoText_TimeZone = (EditText) dialogView.findViewById(R.id.autoText_TimeZone);
        final ListView listvw = (ListView) dialogView.findViewById(R.id.listview);
        final LinearLayout ll_search = dialogView.findViewById(R.id.ll_search);
        ll_search.setVisibility(View.GONE);


        ArrayAdapter<DeviceNodeMcu> adapter = new ArrayAdapter<DeviceNodeMcu>(myContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, data);
        listvw.setAdapter(adapter);
        listvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                DeviceNodeMcu DeviceNodeMcu = (DeviceNodeMcu) listvw.getItemAtPosition(position);
                String SelectedDeviceName = DeviceNodeMcu.getDeviceName();
                readDeviceId = DeviceNodeMcu.getDeviceId();
                Device_Type = DeviceNodeMcu.getDeviceType();
                String SelectedDeviceAvailability = DeviceNodeMcu.getAvailability();
                if (SelectedDeviceAvailability.equals(UTIL.ONLINE)) {

                    autoCompleteTextView.setText(SelectedDeviceName);
                } else {
                    Dialog_showMessage("Sorry!", "This device is offline!");

                }

                alertDialog1.dismiss();
            }
        });


        autoText_TimeZone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                /*to clear autocomplete*/
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (motionEvent.getRawX() >= (autoText_TimeZone.getRight() - autoText_TimeZone.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                            autoText_TimeZone.setText("");
                            return true;
                        }
                    }
                } catch (Exception e) {
                }


                return false;
            }
        });

        autoText_TimeZone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = autoText_TimeZone.getText().toString().trim().toLowerCase();
                if (autoText_TimeZone.getText().length() == 0) {
                    autoText_TimeZone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    ArrayAdapter<DeviceNodeMcu> adapter = new ArrayAdapter<DeviceNodeMcu>(myContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, data);
                    listvw.setAdapter(adapter);


                } else {
                    autoText_TimeZone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clear, 0);
                    list.clear();
                    for (int i = 0; i < data.size(); i++) {


                        String listText = data.get(i).getDeviceName().toLowerCase();
                        if (listText.contains(text)) {
                            list.add(data.get(i));
                        }
                    }
                    ArrayAdapter<DeviceNodeMcu> adapter = new ArrayAdapter<DeviceNodeMcu>(myContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, list);
                    listvw.setAdapter(adapter);


                }
            }
        });


        ///// new work


        dialogBuilder.setTitle(dialog_title);
//        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                alertDialog1.dismiss();
//
//            }
//        });

        alertDialog1 = dialogBuilder.create();
        alertDialog1.show();
    }

    public void showSearchableSpinnerDialog_TimeZone(final AutoCompleteTextView autoCompleteTextView, final ArrayList<HashMap<String, String>> data, String dialog_title) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText autoText_TimeZone = (EditText) dialogView.findViewById(R.id.autoText_TimeZone);
        final ListView listvw = (ListView) dialogView.findViewById(R.id.listview);

        final ArrayList<Myspinner_timezone> list_temp = new ArrayList<>();
        final ArrayList<Myspinner_timezone> list = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            HashMap<String, String> hashMap = data.get(i);

            String Country_Name = hashMap.get("Country_Name");
            String Time_Zone = hashMap.get("Time_Zone");
            String GMT_Offset = hashMap.get("GMT_Offset");
            GMT_Offset = GMT_Offset.replace(" ", "");
            String text = "(" + GMT_Offset + ")" + " " + Country_Name;

            Myspinner_timezone myspinner_timezone = new Myspinner_timezone(text, Time_Zone, GMT_Offset);
            list_temp.add(myspinner_timezone);
        }

        list.addAll(list_temp);


        ArrayAdapter<Myspinner_timezone> adapter = new ArrayAdapter<Myspinner_timezone>(myContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, list_temp);
        listvw.setAdapter(adapter);


        listvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Myspinner_timezone spinner_ = (Myspinner_timezone) listvw.getItemAtPosition(position);
                String text = spinner_.getSpinnerText();
                Time_Zone_Id = spinner_.gettimezone_id();
                Time_Zone_Name = spinner_.gettimezone_name();
                autoCompleteTextView.setText(text);
                alertDialog1.dismiss();
            }
        });


        autoText_TimeZone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                /*to clear autocomplete*/
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (motionEvent.getRawX() >= (autoText_TimeZone.getRight() - autoText_TimeZone.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                            autoText_TimeZone.setText("");
                            return true;
                        }
                    }
                } catch (Exception e) {
                }


                return false;
            }
        });

        autoText_TimeZone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = autoText_TimeZone.getText().toString().trim().toLowerCase();
                if (autoText_TimeZone.getText().length() == 0) {
                    autoText_TimeZone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    ArrayAdapter<Myspinner_timezone> adapter = new ArrayAdapter<Myspinner_timezone>(myContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, list);
                    listvw.setAdapter(adapter);


                } else {
                    autoText_TimeZone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clear, 0);
                    list_temp.clear();

                    for (int i = 0; i < list.size(); i++) {
                        String listText = list.get(i).getSpinnerText().toLowerCase();
                        if (listText.contains(text.toLowerCase())) {
                            list_temp.add(list.get(i));
                        }
                    }

                    ArrayAdapter<Myspinner_timezone> adapter = new ArrayAdapter<Myspinner_timezone>(myContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, list_temp);
                    listvw.setAdapter(adapter);

                }
            }
        });


        ///// new work


        dialogBuilder.setTitle(dialog_title);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialog1.dismiss();
                autoCompleteTextView.setText("");
            }
        });

        alertDialog1 = dialogBuilder.create();
        alertDialog1.show();
    }


    void getDeviceDetail() {

        utill.showProgressDialog("Checking device details..");
        // progressDialogsetMessage("Checking device details...");

        ArrayList<String> arrayList_device_type_Switch =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
        ArrayList<String> arrayList_device_type_WaterLevelController =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
        ArrayList<String> arrayList_device_type_AirQualityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));

        ArrayList<String> arrayList_device_type_SecurityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));


        if (arrayList_device_type_Switch.contains(Device_Type)) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {

                getDeviceDetailsA_ApiCall();

            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }


        } else if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsB_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (arrayList_device_type_AirQualityMonitor.contains(Device_Type)) {


            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsC_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {


            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetailsD_ApiCall();
            } else {
                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }
        }


    }

    void getDeviceDetailsA_ApiCall() {
        String tag_string_req = "req_login";

        Log.e("API getDeviceDetailsA_ApiCall ---", "getDeviceDetailsA_ApiCall");
        //  utill.showProgressDialog("Getting device...");
       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device details...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsA_API + "DeviceId=" + readDeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                utill.hideProgressDialog();
                //  utill.hideProgressDialog();
                //   pDialog.dismiss();
                //1. //[]

                //2.


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        String LockStatus = jsonObject.getString("LocakStatus");// 0 or 1
                        String RoleId = jsonObject.getString("RoleId").trim();

                        if (RoleId.equalsIgnoreCase("1")) {
                            //means Admin .can update wifi cred
                            if (LockStatus.equalsIgnoreCase("1")) {
                                // HideProgressDialog();
                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                // connectWithDevice_WriteData();
                                UpdateGMTonServer(readDeviceId, Device_Type, Time_Zone_Name, Time_Zone_Id);
                            }
                        } else {

                            //   HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        // HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update time zone on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    //  HideProgressDialog();
                    utill.hideProgressDialog();
                    e.printStackTrace();
                  /*  Toast.makeText(myContext,
                            "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                    Log.e("API getDeviceDetailsA_ApiCall err ---", e.getMessage());
                    Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                utill.hideProgressDialog();
                // HideProgressDialog();
                //  pDialog.dismiss();
                //utill.HideProgressDialog();
             /*   Toast.makeText(myContext,
                        "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                Log.e("API getDeviceDetailsA_ApiCall err ---", error.toString());
                Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");


            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetailsB_ApiCall() {
        String tag_string_req = "req_login";
        //  utill.showProgressDialog("Getting device...");
      /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/
        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        Log.e("API getDeviceDetailsB_ApiCall  ---", "");
        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsB_API + "DeviceId=" + readDeviceId + "&UserId=" + Uid;

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

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        String LockStatus = jsonObject.getString("LocakStatus");// 0 or 1
                        String RoleId = jsonObject.getString("RoleId").trim();

                        if (RoleId.equalsIgnoreCase("1")) {
                            //means Admin .can update wifi cred
                            if (LockStatus.equalsIgnoreCase("1")) {
                                //  HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                // connectWithDevice_WriteData();
                                UpdateGMTonServer(readDeviceId, Device_Type, Time_Zone_Name, Time_Zone_Id);

                            }
                        } else {

                            //  HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        //   HideProgressDialog();

                        Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");


                    }


                } catch (JSONException e) {
                    // JSON error
                    //  HideProgressDialog();
                    e.printStackTrace();
                   /* Toast.makeText(myContext,
                            "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                    Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");

                    Log.e("API getDeviceDetailsB_ApiCall err ---", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //   HideProgressDialog();
                //  pDialog.dismiss();
                utill.hideProgressDialog();
               /* Toast.makeText(myContext,
                        "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");

                Log.e("API getDeviceDetailsB_ApiCall err ---", error.toString());

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetailsC_ApiCall() {
        String tag_string_req = "req_login";
        Log.e("API getDeviceDetailsC_ApiCall  ---", "");

        String URL_LOGIN = null;

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsC_API + "DeviceId=" + readDeviceId + "&UserId=" + Uid;

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

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        String LockStatus = jsonObject.getString("LocakStatus");// 0 or 1
                        String RoleId = jsonObject.getString("RoleId").trim();

                        if (RoleId.equalsIgnoreCase("1")) {
                            //means Admin .can update wifi cred
                            if (LockStatus.equalsIgnoreCase("1")) {
                                //  HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                //  connectWithDevice_WriteData();
                                UpdateGMTonServer(readDeviceId, Device_Type, Time_Zone_Name, Time_Zone_Id);

                            }
                        } else {

                            // HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        //  HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update time zone on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    //   HideProgressDialog();
                    e.printStackTrace();
                  /*  Toast.makeText(myContext,
                            "Some error occurred!", Toast.LENGTH_SHORT).show();
*/
                    Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");

                    Log.e("API getDeviceDetailsC_ApiCall err ---", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // HideProgressDialog();
                utill.hideProgressDialog();
                //  pDialog.dismiss();
                //utill.HideProgressDialog();
              /*  Toast.makeText(myContext,
                        "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");


                Log.e("API getDeviceDetailsC_ApiCall err ---", error.getMessage());
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void getDeviceDetailsD_ApiCall() {
        String tag_string_req = "req_login";
        Log.e("API getDeviceDetailsD_ApiCall  ---", "");
       /* final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        String URL_LOGIN = null;
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);

        try {
            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceList_API + "uid=" + Uid;
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsD_API + "DeviceId=" + readDeviceId + "&UserId=" + Uid;

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

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String DeviceId = jsonObject.getString("DeviceId");
                        String LockStatus = jsonObject.getString("LocakStatus");// 0 or 1
                        String RoleId = jsonObject.getString("RoleId").trim();

                        if (RoleId.equalsIgnoreCase("1")) {
                            //means Admin .can update wifi cred
                            if (LockStatus.equalsIgnoreCase("1")) {
                                // HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                // connectWithDevice_WriteData();
                                UpdateGMTonServer(readDeviceId, Device_Type, Time_Zone_Name, Time_Zone_Id);

                            }
                        } else {

                            // HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        // HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update time zone on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    // HideProgressDialog();
                    e.printStackTrace();
                  /*  Toast.makeText(myContext,
                            "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                    Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");

                    Log.e("getdeviceDetailsD error", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //
                //   HideProgressDialog();
                utill.hideProgressDialog();
                //  pDialog.dismiss();
                //utill.HideProgressDialog();
             /*   Toast.makeText(myContext,
                        "Some error occurred!", Toast.LENGTH_SHORT).show();*/
                Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");

                Log.e("getdeviceDetailsD error", error.toString());

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void dialog_msg(String str_title, String str_msg) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText(str_title);
        message.setText(str_msg);
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //  finish();

            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void dialog_resetDevice() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Time Zone updated successfully!");
        message.setText("Please restart device!");
        positiveBtn.setText("Ok");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                ((ActivityWithNavigationMenu) getActivity()).addDeviceListFragment();


            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void DialogInternet() {

        String title_str = "No internet connection!";
        String msg_str = "Please connect mobile to Wi-Fi!";
        //dialog_DisableMobileData(title, msg);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();


                ((ActivityWithNavigationMenu) getActivity()).addDeviceListFragment();


            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void ShowProgressDialog() {
        if (progressDialog == null || (!progressDialog.isShowing())) {
            progressDialog = ProgressDialog.show(getActivity(), null, null, true);
            progressDialog.setContentView(R.layout.elemento_progress);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            if (!progressDialog.isShowing())
                progressDialog.show();
        }
    }

    private void progressDialogsetMessage(String msg) {
        final TextView tv = progressDialog.getWindow().findViewById(R.id.textView6);
        tv.setText(msg);
    }

    private void HideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void UpdateGMTonServer(String DeviceId,
                                   String DeviceType,
                                   String timezoneName,
                                   String timezoneID
    ) {

        utill.showProgressDialog("Updating Time Zone...");
        String tag_string_req = "req_login";
        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("DeviceId", DeviceId);
        params.put("DeviceType", DeviceType);
        params.put("timezoneName", timezoneName);
        params.put("timezoneID", timezoneID);

        URL = UTIL.Domain_Arduino + UTIL.UpdateTimeZone_API;

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //   utill.hideProgressDialog();
                        try {
                            JSONObject jObj = response;
                            String errormsg = jObj.getString("errormsg");
                            String status = jObj.getString("Status");
                            if (status.equals("1")) {
                                //dialog_resetDevice();
                                //now wait
                                Try_Count = 0;
                                check();
                               /* Toast.makeText(myContext,
                                        errormsg, Toast.LENGTH_SHORT).show();*/

                            } else if (status.equals("2")) {
                                Toast.makeText(myContext,
                                        errormsg, Toast.LENGTH_SHORT).show();
                                Dialog_showMessage("Error!", "Unable to update time zone!\nPlease try again");
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Log.e("api err---", "UpdateGMTonServer");
                            Dialog_showMessage("Error!", "Unable to update time zone!\nPlease try again");
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                //   Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Log.e("api err---", "UpdateGMTonServer");
                Dialog_showMessage("Error!", "Unable to update time zone!\nPlease try again");


            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }

    private void Dialog_showMessage(String title_str, String msg_str) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();


            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void Dialog_ConnectInternet(final int REQUEST_CODE) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Phone is not connected to internet!");
        message.setText("Connect to internet first!");
        positiveBtn.setText("Ok");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), REQUEST_CODE);
            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    void getDeviceList_ApiCall_New() {

        // list.clear();
        list_DeviceNodeMcuNew.clear();
        //  list_DeviceNodeMcuNew.add(new DeviceNodeMcu(UTIL.SelectDevice, "", UTIL.OFFLINE, ""));


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
                //  list.clear();
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
                            android.util.Log.e("DeviceId", DeviceId);
                            android.util.Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            android.util.Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            android.util.Log.e("currentTime", currentTime);
                            android.util.Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));
                            android.util.Log.e("**********", "*********");

                            String availability;
                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }

                            android.util.Log.e("availability", availability);

                            if (!DeviceId.equals("")) {

                                list_DeviceNodeMcuNew.add(new DeviceNodeMcu(DeviceName, DeviceId, availability, DeviceType));

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


                            android.util.Log.e("DeviceId", DeviceId);
                            android.util.Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            android.util.Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            android.util.Log.e("currentTime", currentTime);
                            android.util.Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));
                            String availability;
                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }
                            if (!DeviceId.equals("")) {


                                list_DeviceNodeMcuNew.add(new DeviceNodeMcu(DeviceName, DeviceId, availability, DeviceType));

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


                            android.util.Log.e("DeviceId", DeviceId);
                            android.util.Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            android.util.Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            android.util.Log.e("currentTime", currentTime);
                            android.util.Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));
                            String availability;
                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }

                            if (!DeviceId.equals("")) {

                                list_DeviceNodeMcuNew.add(new DeviceNodeMcu(DeviceName, DeviceId, availability, DeviceType));

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


                            android.util.Log.e("DeviceId", DeviceId);
                            android.util.Log.e("LastSeen_dateTime", Device_LastSeen_dateTime);
                            android.util.Log.e("Device_TimeZoneID", Device_TimeZoneID);
                            android.util.Log.e("currentTime", currentTime);
                            android.util.Log.e("TotalelapsedSeconds", String.valueOf(TotalelapsedSeconds));
                            String availability;
                            if (TotalelapsedSeconds == UTIL.Error_val) {
                                availability = "Error";
                            } else if (TotalelapsedSeconds <= UTIL.Second_Online) {
                                availability = "Online";
                            } else {
                                availability = "Offline";
                            }

                            if (!DeviceId.equals("")) {

                                list_DeviceNodeMcuNew.add(new DeviceNodeMcu(DeviceName, DeviceId, availability, DeviceType));
                            }
                        }

                    }
/********************************************************************************/
                    if (list_DeviceNodeMcuNew.size() < 1) {
                        // tv_msg.setVisibility(View.VISIBLE);
                        Toast.makeText(myContext,
                                "No devices found!", Toast.LENGTH_SHORT).show();
                    } else {
                        // tv_msg.setVisibility(View.GONE);
                    }

                    //  setData_Spinners();





                    /*Device_List_Fragment.device_list_adapter adapter = new Device_List_Fragment.device_list_adapter(myContext, list);
                    listview_devices.setAdapter(adapter);*/

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
              /*  Toast.makeText(myContext,
                        "Error!", Toast.LENGTH_SHORT).show();*/
                Dialog_showMessage("Error!", "Unable to get Devices\nPlease try again");
                // listview_devices.setAdapter(null);

                // tv_msg.setVisibility(View.VISIBLE);
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void check() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                utill.hideProgressDialog();

                /*Try_Count++;
                if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                    if (Try_Count < 2) {
                        getDeviceDetail();
                    } else {
                        HideProgressDialog();
                        Try_Count = 0;
                        Dialog_showMessage("Error!", "Unable to update timezone device.\nPlease try again");
                    }
                } else {
                    HideProgressDialog();
                    DialogInternet();
                }*/

                dialog_resetDevice();


            }
        }, 4000);   //5 seconds
    }
}
