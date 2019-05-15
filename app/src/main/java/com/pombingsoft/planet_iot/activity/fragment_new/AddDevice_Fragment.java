package com.pombingsoft.planet_iot.activity.fragment_new;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import com.pombingsoft.planet_iot.activity.security_monitor.SetRangeActivity;
import com.pombingsoft.planet_iot.activity.water_level_controller.SetUpActivity;
import com.pombingsoft.planet_iot.location.common.activities.SampleActivityBase_New;
import com.pombingsoft.planet_iot.location.common.logger.Log;
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
import java.util.TimeZone;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


public class AddDevice_Fragment extends SampleActivityBase_New
        implements GoogleApiClient.OnConnectionFailedListener {
    public static final Integer GPS_SETTINGS = 0x7;
    static final Integer LOCATION = 0x1;
    // GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    /***************************/
    Context myContext;
    UTIL utill;
    String DeviceId = "";
    WifiManager wifiManager;
    // WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    Spinner spin_Device, spin_Wifi, spin_Device_location;
    ImageView text_refresh_device, text_refresh_wifi, text_refresh_timzone;
    EditText et_DeviceName, et_WIFi_Pass, et_DevicePass;
    Button btnAddDevice;
    com.toptoche.searchablespinnerlibrary.SearchableSpinner searchableSpinner_timeZone;
    AutoCompleteTextView autoText_, searchableSpinner_device, searchableSpinner_wifi;
    AlertDialog alertDialog1;
    // AutoCompleteTextView autoText_TimeZone;
    //  ArrayList<String> arrayList_timeZone;

    ArrayList<HashMap<String, String>> List_timeZone = new ArrayList<>();
    String WIFi_SSID = "";
    String WIFi_Pass = "";
    String Time_Zone_Name = "";
    String Time_Zone_Id = "";
    //  ProgressDialog pDialog;
    String Device_SSID = "";
    int Try_Count = 0;
    AlertDialog alertDialog;
    String Device_Name, Device_Pass, Device_Type;
    String readDeviceId;
    //ArrayList<String> list;
    ProgressDialog progressDialog;
    ArrayList<String> list_Device = new ArrayList<>();
    ArrayList<String> list_Wifi = new ArrayList<>();
    String Tank_type = "";
    String Tank_function_state = "";
    String Upper_level_depth = "";
    String Upper_level_water_capacity = "";
    String Pump_on_time_max = "";
    String Pump_cool_time_min = "";
    String water_tank_data = "";
    /*Device D*/
    String Temp_MIN = "0", Temp_MAX = "0", Humidity_MIN = "0",
            Humidity_MAX = "0", TempHumState = "0";
    boolean IsRefreshTimeZone = false;
    /*location*/
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_test, container, false);

        myContext = getActivity();
        utill = new UTIL(myContext);
        mGoogleApiClient = new GoogleApiClient.Builder(myContext)
                // .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)      //for current loc(gps)
                .build();
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        getActivity().setTitle("Add New Device");
        init_views(rootView);
        String title = "Recommended!";
        String msg = "Please disable mobile data,\nUse Wi-Fi connection.";
        dialog_DisableMobileData(title, msg);

        return rootView;
    }


    private void init_views(View rootView) {
        spin_Device = (Spinner) rootView.findViewById(R.id.spin_Device);
        text_refresh_device = rootView.findViewById(R.id.text_refresh_device);
        et_DeviceName = rootView.findViewById(R.id.et_DeviceName);
        spin_Wifi = rootView.findViewById(R.id.spin_Wifi);
        text_refresh_wifi = rootView.findViewById(R.id.text_refresh_wifi);
        text_refresh_timzone = rootView.findViewById(R.id.text_refresh_timzone);
        et_WIFi_Pass = rootView.findViewById(R.id.et_WIFi_Pass);
        spin_Device_location = rootView.findViewById(R.id.spin_Device_location);
        btnAddDevice = rootView.findViewById(R.id.btnAddDevice);
        et_DevicePass = rootView.findViewById(R.id.et_DevicePass);


        searchableSpinner_timeZone = rootView.findViewById(R.id.searchableSpinner);
        searchableSpinner_timeZone.setTitle("Time Zone");
        searchableSpinner_timeZone.setPositiveButton("OK");


        searchableSpinner_device = rootView.findViewById(R.id.searchableSpinner_device);
        searchableSpinner_device.setHint(UTIL.SelectDevice);
        searchableSpinner_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSearchableSpinnerDialog(searchableSpinner_device, list_Device, "Device(NodeMcu)");

            }
        });


        //for device B setUp activity
        searchableSpinner_device.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Device_SSID = searchableSpinner_device.getText().toString().trim();
                if (Device_SSID.trim().equals("") || Device_SSID.equals(UTIL.SelectDevice)) {
                } else {
                    String[] device_desc = Device_SSID.split("_");//tNode_A1,tNode_A2,tNode_A4,tNode_A8,tNode_B,tNode_C1,tNode_C2,tNode_C3,tNode_C4,tNode_D1,tNode_D2,tNode_D3,tNode_D4
                    String device_type = device_desc[1];//A1,A2,A4,A8,B,C1,C2,C3,C4,D1,D2,D3,D4
                    if (device_type.equals("B")) {
                        dialog_SetUP_watercontroller();
                    } else if (device_type.equals("D1") || device_type.equals("D3")) {
                        dialog_SetUP_TempHumidity_Range();
                    }
                }


            }
        });


        searchableSpinner_wifi = rootView.findViewById(R.id.searchableSpinner_wifi);

        searchableSpinner_wifi.setHint(UTIL.SelectWIFI);
        searchableSpinner_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchableSpinnerDialog(searchableSpinner_wifi, list_Wifi, "Wi-Fi");
            }
        });


        text_refresh_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowProgressDialog();
                progressDialogsetMessage("Looking for Wi-Fi networks...");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        getAvailableWifi();
                        HideProgressDialog();
                        if (list_Wifi.size() < 2) {
                            Dialog_showMessage("No Wi-Fi found!", "Please try again");
                        }

                    }
                }, 3000);   //5 seconds


            }
        });


        text_refresh_timzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsRefreshTimeZone = true;
                ShowProgressDialog();
                getTimeZoneList();
            }
        });


        text_refresh_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowProgressDialog();
                progressDialogsetMessage("Looking for devices...");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        getAvailableNodeMcu();
                        HideProgressDialog();

                        if (list_Device.size() < 2) {
                            Dialog_showMessage("No device found!", "Please switch off and switch on device\nThen try again");
                        }

                    }
                }, 3000);   //5 seconds


            }
        });

        // searchableSpinner.getSelectedItemPosition();


///////
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


                Device_Name = et_DeviceName.getText().toString().trim();
                Device_Pass = et_DevicePass.getText().toString().trim();

                //    int selectedWifiPos = searchableSpinner_wifi.getSelectedItemPosition();
                WIFi_SSID = searchableSpinner_wifi.getText().toString().trim();
                WIFi_Pass = et_WIFi_Pass.getText().toString().trim();
                // String TimeZone = autoText_.getText().toString().trim();
                String TimeZone = autoText_.getText().toString().trim();

                if (Device_SSID.trim().equals("") || Device_SSID.equals(UTIL.SelectDevice)) {
                    Toast.makeText(myContext, "Please select a Device!", Toast.LENGTH_SHORT).show();
                } else if (Device_Name.isEmpty()) {
                    Toast.makeText(myContext, "Please enter a name for this device!", Toast.LENGTH_SHORT).show();

                } else if (Device_Pass.isEmpty()) {
                    Toast.makeText(myContext, "Please enter password for this device!", Toast.LENGTH_SHORT).show();

                } else if (WIFi_SSID.trim().equals("") || WIFi_SSID.equals(UTIL.SelectWIFI)) {
                    Toast.makeText(myContext, "Please select your Wifi!", Toast.LENGTH_SHORT).show();

                } else if (WIFi_Pass.isEmpty()) {
                    Toast.makeText(myContext, "Please enter your Wifi Password!", Toast.LENGTH_SHORT).show();

                } else if (TimeZone.equals("")) {
                    Toast.makeText(myContext, "Please select Time Zone!", Toast.LENGTH_SHORT).show();

                } else if (TimeZone.isEmpty()) {
                    Toast.makeText(myContext, "Please enter correct Time Zone!", Toast.LENGTH_SHORT).show();

                } else {



                   /* Time_Zone_Name = TimeZone.substring(TimeZone.indexOf("(") + 1, TimeZone.indexOf(")")).trim();//GMT+5:30
                    String st = Time_Zone_Name.substring(Time_Zone_Name.indexOf("GMT") + 3).trim();//+5:30
                    if (st.charAt(0) == '+') {//+5:30
                        String hr = st.substring(st.indexOf("+") + 1, st.indexOf(":"));//5
                        String minute = st.substring(st.indexOf(":"));//:30
                        if (hr.length() == 1) {
                            Time_Zone_Name = "GMT" + "+0" + hr + minute;//GMT+05:30
                        }
                    } else if (st.charAt(0) == '-') {//-5:30
                        String hr = st.substring(st.indexOf("-") + 1, st.indexOf(":"));//5
                        String minute = st.substring(st.indexOf(":"));//:30
                        if (hr.length() == 1) {
                            Time_Zone_Name = "GMT" + "-0" + hr + minute;//GMT-05:30
                        }
                    } else {//0:30,5:30
                        String hr = st.substring(0, st.indexOf(":"));//0
                        String minute = st.substring(st.indexOf(":"));//:30
                        if (hr.length() == 1) {
                            Time_Zone_Name = "GMT" + "+0" + hr + minute;//GMT+00:30
                        }
                    }
                    Time_Zone_Id = TimeZone.substring(TimeZone.indexOf(")") + 1).trim();//Asia/Kolkata
               */


                    String[] device_desc = Device_SSID.split("_");//tNode_A1,tNode_A2,tNode_A4,tNode_A8,tNode_B,tNode_C
                    Device_Type = device_desc[1];//A1,A2,A4,A8,B,C1,C2,C3,C4
                    connectWithDevice_readDeviceID();

                }
            }
        });
    }

    private void EnableLocation() {
      /*  ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Enabling Location...");
        if (!pDialog.isShowing())
            pDialog.show();*/


        /******************************for gps current location***********************************************/
        try {
            // Get the location manager
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
               /* if (pDialog != null && pDialog.isShowing()){
                    pDialog.dismiss();}*/
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
            } else {

                getUniqueDeviceId();

            }
        } catch (Exception e) {
            e.getCause();
        }
    }

    /******************************Methods for gps current location***********************************************/
    private void askForPermission(String permission, Integer requestCode) {

        // utill.HideProgressDialog();
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else {
            //  //  Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            //  utill.HideProgressDialog();

            askForGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {

                //   Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                askForGPS();
            } else {
                Toast.makeText(getActivity(), "Please allow this permission!", Toast.LENGTH_SHORT).show();

            }

        }


    }

    private void askForGPS() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // NO need to show the dialog;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //  GPS turned off, Show the user a dialog
                        // Show the dialog by calling startResolutionForResult(), and check the result

                        try {
                            status.startResolutionForResult(getActivity(), GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are unavailable so not possible to show any dialog now
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_SETTINGS) {

            if (resultCode == RESULT_OK) {
                //  Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
                //  setData_Spinners();

                getUniqueDeviceId();

            } else {
                Toast.makeText(getActivity(), "GPS is not enabled!", Toast.LENGTH_LONG).show();

                ((ActivityWithNavigationMenu) getActivity()).addDeviceListFragment();
            }

        } else if (requestCode == UTIL.Water_controller_SETTINGS) {
            if (data != null) {

                Tank_type = data.getStringExtra("tank_type");
                Tank_function_state = data.getStringExtra("tank_function_state");
                Upper_level_depth = data.getStringExtra("Upper_level_depth");
                Upper_level_water_capacity = data.getStringExtra("Upper_level_water_capacity");
                Pump_on_time_max = data.getStringExtra("pump_on_time_max");
                Pump_cool_time_min = data.getStringExtra("pump_cool_time_min");


                water_tank_data = Tank_type + "_" + Tank_function_state + "_" +
                        Upper_level_depth + "_" + Upper_level_water_capacity + "_" +
                        Pump_on_time_max + "_" + Pump_cool_time_min;
                afterSetUP_dialog();
            } else {
                dialog_SetUP_watercontroller();
            }

        } else if (requestCode == UTIL.Security_monitor_SETTINGS) {
            if (data != null) {

                Temp_MIN = data.getStringExtra("min_temp");
                Temp_MAX = data.getStringExtra("max_temp");
                Humidity_MIN = data.getStringExtra("min_humidity");
                Humidity_MAX = data.getStringExtra("max_humidity");

                afterSetUP_dialog();
            } else {
                dialog_SetUP_TempHumidity_Range();
            }

        }


        //to do
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private void getUniqueDeviceId() {

        Log.e("API---", "getUniqueDeviceId");
        ShowProgressDialog();
        progressDialogsetMessage("Getting unique id for device...");

        //   boolean isMobileDataEnabled = UTIL.isMobileDataEnabled(myContext);
        // if (!isMobileDataEnabled) {//mobile data off is necessary to call api of nodMcu in MI devices
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getDeviceId();
        } else {
            HideProgressDialog();
            DialogInternet();
        }
      /*  } else {

            HideProgressDialog();

            String title = "Mobile data is ON!";
            String msg = "Please disable mobile data!";
            dialog_DisableMobileData(title, msg);

        }*/


        //  }
    }

    private void getDeviceId() {

        Log.e("API---", "getDeviceId");
     /* final  ProgressDialog  pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting device ID...");
        if (!pDialog.isShowing())
            pDialog.show();
*/

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        String tag_string_req = "req_login";

        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceID_API + "UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //{"errormsg":"gDEdAkvDEY","Status":"1","UserId":null}
                //{"errormsg":"This user Id is not registered!","Status":"2","UserId":null}
                try {
                    JSONObject jObj = new JSONObject(response);
                    String errormsg = jObj.getString("errormsg");
                    String status = jObj.getString("Status");
                    // String UserId = jObj.getString("UserId");

                    if (status.equals("1")) {
                        //we have unique device id....now write to nodemcu
                        DeviceId = errormsg;
                        //  Enable_Wifi_Location(); // because we already enabled wifi
                        //  EnableLocation();
                        //  setData_Spinners();

                        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                            getTimeZoneList();
                        } else {
                            HideProgressDialog();
                            DialogInternet();
                        }


                    } else if (status.equals("2")) {
                        HideProgressDialog();
                        Toast.makeText(myContext,
                                "You are not valid user!", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    // JSON error
                    HideProgressDialog();
                    e.printStackTrace();
                    //   Toast.makeText(myContext, " Some error occurred! " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("API error---", "getDeviceId");

                    Dialog_showMessage("Error!", "Unable to get unique id\nPlease try again");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                Log.e("API error---", "getDeviceId");

                Dialog_showMessage("Error!", "Unable to get unique id\nPlease try again");

            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getTimeZoneList() {
        List_timeZone.clear();
        Log.e("API---", "getTimeZoneList");
        String tag_string_req = "req_login";

        progressDialogsetMessage("Getting available time zone ...");


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
                    HideProgressDialog();
                    e.printStackTrace();
                    //   Toast.makeText(myContext, " Some error occurred! " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("API error---", "getTimeZoneList");
                    Dialog_showMessage("Error!", "Unable to get Time Zone\nPlease try again");

                }

                if (IsRefreshTimeZone) {
                    IsRefreshTimeZone = false;
                    HideProgressDialog();
                } else {
                    setData_Spinners();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
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

    private void setData_Spinners() {
        progressDialogsetMessage("Looking for devices...");

        getAvailableNodeMcu();
        getAvailableWifi();
        //  setTimeZone();
        //  setTimeZone_new();
        HideProgressDialog();

        if (list_Device.size() < 2) {
            Dialog_showMessage("No device found!", "Please switch off and switch on device\nThen try again");
        }
    }

    private void getAvailableNodeMcu() {
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();
        String device_name = UTIL.DeviceNamePrefix;



      /*  ArrayList<Myspinner> list_Device = new ArrayList<>();
        list_Device.add(new Myspinner(UTIL.SelectDevice, "0"));
        for (int i = 0; i < wifiList.size(); i++) {
            String wifi_ssid = wifiList.get(i).SSID;
            String wifi_bssid = wifiList.get(i).BSSID;
            if ((wifi_ssid != null) && (wifi_ssid.startsWith(device_name))) {
                list_Device.add(new Myspinner(wifi_ssid, wifi_bssid));
            }


        }
*/
        list_Device.clear();
        list_Device.add(UTIL.SelectDevice);
        for (int i = 0; i < wifiList.size(); i++) {
            String wifi_ssid = wifiList.get(i).SSID;
            String wifi_bssid = wifiList.get(i).BSSID;
            if ((wifi_ssid != null) && (wifi_ssid.startsWith(device_name))) {
                list_Device.add(wifi_ssid);
            }


        }





      /*  SpinnerAdapter adapter = new ArrayAdapter<Myspinner>(myContext,
                android.R.layout.simple_list_item_1, list_Device);
        searchableSpinner_device.setAdapter(adapter);*/


    }

    private void getAvailableWifi() {

        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();

        String device_name = UTIL.DeviceNamePrefix;
     /*   ArrayList<Myspinner> list_Wifi = new ArrayList<>();
        list_Wifi.add(new Myspinner(UTIL.SelectWIFI, "0"));
        for (int i = 0; i < wifiList.size(); i++) {

            String wifi_ssid = wifiList.get(i).SSID;
            String wifi_bssid = wifiList.get(i).BSSID;

            if ((wifi_ssid != null) && (wifi_ssid.startsWith(device_name))) {

            } else {
                list_Wifi.add(new Myspinner(wifi_ssid, wifi_bssid));
            }

        }*/


        list_Wifi.clear();
        list_Wifi.add(UTIL.SelectWIFI);
        for (int i = 0; i < wifiList.size(); i++) {

            String wifi_ssid = wifiList.get(i).SSID;
            String wifi_bssid = wifiList.get(i).BSSID;

            if ((wifi_ssid != null) && (wifi_ssid.startsWith(device_name))) {

            } else {
                list_Wifi.add(wifi_ssid);
            }

        }






       /* SpinnerAdapter adapter = new ArrayAdapter<Myspinner>(myContext,
                android.R.layout.simple_list_item_1, list_Wifi);
        searchableSpinner_wifi.setAdapter(adapter);*/


    }

    public void showSearchableSpinnerDialog(final AutoCompleteTextView autoCompleteTextView, final ArrayList<String> data, String dialog_title) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final ArrayList<String> list = new ArrayList<>();
        final EditText autoText_TimeZone = (EditText) dialogView.findViewById(R.id.autoText_TimeZone);
        final ListView listvw = (ListView) dialogView.findViewById(R.id.listview);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, data);
        listvw.setAdapter(adapter);
        listvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String itemValue = (String) listvw.getItemAtPosition(position);

                autoCompleteTextView.setText(itemValue);
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, data);
                    listvw.setAdapter(adapter);


                } else {
                    autoText_TimeZone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clear, 0);
                    list.clear();
                    for (int i = 0; i < data.size(); i++) {
                        String listText = data.get(i).toLowerCase();
                        if (listText.contains(text)) {
                            list.add(data.get(i));
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1, list);
                    listvw.setAdapter(adapter);


                }
            }
        });


        ///// new work


        dialogBuilder.setTitle(dialog_title);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                alertDialog1.dismiss();

            }
        });

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

    public void connectWithDevice_readDeviceID() {
        Log.e("msg---", "connectWithDevice_readDeviceID");
        ShowProgressDialog();
        progressDialogsetMessage("Connecting to device ...");
        Try_Count = 0;
        if (!UTIL.isConnectedToNodeMcu(myContext)) {

            Try_Count++;


            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", Device_SSID);
            wifiConfig.preSharedKey = String.format("\"%s\"", UTIL.DevicePASS);
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();


            //now wait
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (UTIL.isConnectedToNodeMcu(myContext)) {
                        Try_Count = 0;
                        ReadDeviceID();
                    } else {

                        if (Try_Count < 3) {
                            connectWithDevice_readDeviceID();
                        } else {
                            HideProgressDialog();
                            // Toast.makeText(AddDeviceActivity_New.this, "Unable to connect to Device!Try Again!", Toast.LENGTH_SHORT).show();
                            Try_Count = 0;

                            Dialog_showMessage("Error!", "Unable to connect to device\nPlease try again");
                        }

                    }


                }
            }, 4000);   //5 seconds


        } else {
            // utill.HideProgressDialog();
            ReadDeviceID();
        }


    }

    private void ReadDeviceID() {
        Log.e("api---", "ReadDeviceID");
        progressDialogsetMessage("Reading device data ...");

        String tag_string_req = "req_login";


      /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Reading Device ID...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.URL_Device + "/getEEPROM_id";

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                /*  pDialog.dismiss();*/

                try {
                    Log.e("Reading deviceID", response);
                    readDeviceId = response.trim();
                    Try_Count = 0;
                    DisconnectNodeMcu_and_connect_internet_toCheckDeviceReg();

                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    HideProgressDialog();
                    //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                    Log.e("api error---", "ReadDeviceID");
                    Dialog_showMessage("Error!", "Unable to read device id\nPlease try again");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                /*  pDialog.dismiss();*/
                HideProgressDialog();
                // Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Log.e("api error---", "ReadDeviceID");
                Dialog_showMessage("Error!", "Unable to read device id\nPlease try again");
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    void DisconnectNodeMcu_and_connect_internet_toCheckDeviceReg() {

        progressDialogsetMessage("Disconnecting from device...");
        //  Try_Count = 0;
        try {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            wifiManager.disconnect();
            for (WifiConfiguration i : list) {
                // String ssid = i.SSID;
                //   if (ssid != null && ssid.startsWith("\"" + UTIL.DeviceNamePrefix)) {
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
                //  }
            }

            //now connect to internet

            progressDialogsetMessage("Connecting to your Wi-Fi ...");
            Try_Count++;

            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", WIFi_SSID);
            wifiConfig.preSharedKey = String.format("\"%s\"", WIFi_Pass);
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            //now wait
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    if (UTIL.isConnectedToMyWifi(getActivity(), WIFi_SSID)) {
                        Try_Count = 0;
                        CheckDeviceRegisteration();
                    } else {

                        //   HideProgressDialog();
                        //  Dialog_showMessage("Error!", "Wifi credentials might be incorrect!\nPlease try again with correct credentials");

                        if (Try_Count < 3) {
                            DisconnectNodeMcu_and_connect_internet_toCheckDeviceReg();
                        } else {
                            HideProgressDialog();
                            Try_Count = 0;
                            Dialog_showMessage("Error!", "Wifi credentials might be incorrect!\nPlease try again with correct credentials");

                        }


                    }

                }
            }, 5000);   //5 seconds


            // wifiManager.reconnect();

        } catch (Exception e) {
            e.getCause();
        }

    }

    private void CheckDeviceRegisteration() {
        Log.e("api ---", "CheckDeviceRegisteration");

        progressDialogsetMessage("Verifying device registration ...");

        String tag_string_req = "req_login";

        String URL_LOGIN = null;
        try {

            //   http://ohmax.in/api/Arduino/CheckDeviceRegistration?DeviceId=sylqxeBtHQ&DeviceType=A
            String device_type = Device_Type;
            device_type = String.valueOf(device_type.charAt(0));

            URL_LOGIN = UTIL.Domain_Arduino + UTIL.CheckDeviceRegistration_API + "DeviceId=" + readDeviceId;

            // URL_LOGIN = UTIL.Domain_Arduino + UTIL.CheckDeviceRegistration_API + "DeviceId=" + "";

            URL_LOGIN = URL_LOGIN.replaceAll(Pattern.quote(" "), "%20");//nks
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // utill.HideProgressDialog();
                //[{"Status":"0"}]

                try {
                    //  JSONArray jsonArray = new JSONArray(response);
                    //  if(jsonArray.length()>0) {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("0")) {
                        connectWithDevice_WriteData();
                    } else {        //This device is already registered

                        //device Id registered
                        HideProgressDialog();
                        dialog_DeviceRegistered();

                        //Toast.makeText(myContext, "This device is already registered.", Toast.LENGTH_SHORT).show();

                    }
                   /* }
                    else{ // if json array empty means readDevice id was blank ---->means eeprom is fresh
                        connectWithDevice_WriteData();
                    }*/

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    HideProgressDialog();
                    //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                    Log.e("api err---", "CheckDeviceRegisteration");
                    Dialog_showMessage("Error!", "Unable to verify device registration\nPlease try again");


                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                // Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();

                Log.e("api err---", "CheckDeviceRegisteration");
                Dialog_showMessage("Error!", "Unable to verify device registration\nPlease try again");

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void connectWithDevice_WriteData() {

        Log.e("msg---", "connectWithDevice_WriteData");

        progressDialogsetMessage("Connecting to device ...");

        if (!UTIL.isConnectedToNodeMcu(myContext)) {

            Try_Count++;


            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", Device_SSID);
            wifiConfig.preSharedKey = String.format("\"%s\"", UTIL.DevicePASS);
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();


            //now wait
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {


                    if (UTIL.isConnectedToNodeMcu(myContext)) {
                        Try_Count = 0;
                        WriteDataToDevice();

                    } else {

                        if (Try_Count < 3) {
                            connectWithDevice_WriteData();
                        } else {
                            HideProgressDialog();
                            Try_Count = 0;
                            //  Toast.makeText(AddDeviceActivity_New.this, "Unable to connect to Device!Try Again!", Toast.LENGTH_SHORT).show();

                            Dialog_showMessage("Error!", "Unable to connect to device\nPlease try again");


                        }
                    }


                }
            }, 5000);   //5 seconds


        } else {
            //   utill.HideProgressDialog();
            Try_Count = 0;
            WriteDataToDevice();
        }


    }

    public void WriteDataToDevice() {
        Log.e("msg---", "WriteDataToDevice");

        progressDialogsetMessage("Transferring data to device...");


        if (UTIL.isConnectedToNodeMcu(myContext)) {

            writeDeviceID();

        }

    }

    private void writeDeviceID() {
        Log.e("api---", "writeDeviceID");
        String tag_string_req = "req_login";
        // utill.ShowProgressDialog("Writing Device ID...");
        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.URL_Device + "/set_uid?a=" + DeviceId;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //   utill.HideProgressDialog();

                try {
                    if (response.equalsIgnoreCase("1")) {
                        writeWifiCredentials();
                    } else {
                        HideProgressDialog();
                        //  Toast.makeText(myContext, "Error ! Try after some time.", Toast.LENGTH_LONG).show();
                        Dialog_showMessage("Error!", "Unable to write device id\nPlease try again");

                    }


                } catch (Exception e) {
                    // JSON error
                    HideProgressDialog();
                    e.printStackTrace();
                    //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                    Log.e("api err---", "writeDeviceID");
                    Dialog_showMessage("Error!", "Unable to write device id\nPlease try again");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Log.e("api err---", "writeDeviceID");
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void writeWifiCredentials() {
        Log.e("api---", "writeWifiCredentials");
        String tag_string_req = "req_login";
        //  utill.ShowProgressDialog("Writing WIFi Credentials...");
        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.URL_Device + "/set_wifi?a=" + WIFi_SSID + "&alertDialog1=" + WIFi_Pass;
            URL_LOGIN = URL_LOGIN.replaceAll(Pattern.quote(" "), "%20");//nks


        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // utill.HideProgressDialog();

                try {

                    //Assuming SSID and Password correct
                    //  DisconnectNodeMcu_and_connect_internet_toUpdateGMTonServer();
                    writeGMT();
                  /*  if (response.equalsIgnoreCase("1")) {
                        checkWifiConnection();
                    } else {
                        Toast.makeText(AddDeviceActivity.this, "Error writing wifi credentials to device", Toast.LENGTH_LONG).show();

                    }*/


                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    HideProgressDialog();
                    //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                    Log.e("api err---", "writeWifiCredentials");

                    Dialog_showMessage("Error!", "Unable to write Wi-Fi credentials\nPlease try again");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Dialog_showMessage("Error!", "Unable to write Wi-Fi credentials\nPlease try again");

                Log.e("api err---", "writeWifiCredentials");
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void writeGMT() {

        Log.e("api ---", "writeGMT");
        String tag_string_req = "req_login";
        //  utill.ShowProgressDialog("Writing Device Location...");
        String URL_LOGIN = null;
        try {
            //  URL_LOGIN = UTIL.URL_Device + "/set_gmt?a=" + URLEncoder.encode(Time_Zone_Name, "utf-8");
            URL_LOGIN = UTIL.URL_Device + "/set_gmt?a=" + Time_Zone_Name;
            Log.d("URL_Device writeGMT ---", URL_LOGIN);
            Log.i("URL_Device writeGMT ---", URL_LOGIN);
            Log.e("URL_Device writeGMT ---", URL_LOGIN);
            //  URL_LOGIN = URL_LOGIN.replaceAll(Pattern.quote(" "), "%20");//nks

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    if (Device_Type.equals("B")) {
                        writeWaterTankData();
                    } else {
                        DisconnectNodeMcu_and_connect_internet_toRegisterDevice();
                    }


                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    HideProgressDialog();
                    //    Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("api err exception ---", "writeGMT");
                    Dialog_showMessage("Error!", "Unable to write time zone\nPlease try again");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                Log.e("api writeGMT err  volley---", error.getMessage());
                Dialog_showMessage("Error!", "Unable to write time zone\nPlease try again");

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void writeWaterTankData() {
        Log.e("api  ---", "writeWaterTankData");
        String tag_string_req = "req_login";
        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.URL_Device + "/write_data?a=" + water_tank_data;
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    DisconnectNodeMcu_and_connect_internet_toRegisterDevice();


                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    HideProgressDialog();
                    //Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                    //  Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("api err---", "CheckDeviceRegisteration");

                    Dialog_showMessage("Error!", "Unable to write tank data\nPlease try again");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Log.e("api err ---", "writeWaterTankData");
                Dialog_showMessage("Error!", "Unable to write tank data\nPlease try again");

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    void DisconnectNodeMcu_and_connect_internet_toRegisterDevice() {
        progressDialogsetMessage("Disconnecting from device ...");

        Try_Count++;
        Log.e("msg ---", "DisconnectNodeMcu_and_connect_internet_toUpdateGMTonServer");
        try {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            wifiManager.disconnect();
            for (WifiConfiguration i : list) {
                //   String ssid = i.SSID;
                //   if (ssid != null && ssid.startsWith("\"" + UTIL.DeviceNamePrefix)) {

                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();


            }

            //now connect to internet
            progressDialogsetMessage("Connecting to your Wi-Fi ...");


            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", WIFi_SSID);
            wifiConfig.preSharedKey = String.format("\"%s\"", WIFi_Pass);
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();


            //now wait
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {


                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        Try_Count = 0;

                        RegisterDevice();


                    } else {
                        if (Try_Count < 3) {

                            DisconnectNodeMcu_and_connect_internet_toRegisterDevice();
                        } else {
                            Try_Count = 0;
                            HideProgressDialog();
                           /* Toast.makeText(myContext,
                                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                                    .show();*/
                            Dialog_showMessage("Error!", "Unable to connect to your Wi-Fi\nPlease try again");

                        }
                    }


                }
            }, 5000);   //5 seconds


            // wifiManager.reconnect();

        } catch (Exception e) {
            e.getCause();
        }

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


        // dialogBuilder.setTitle("Device Details");
        title.setText("Device added successfully!");
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

    private void dialog_DeviceRegistered() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Sorry!");
        message.setText("Device is already registered");
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


        // dialogBuilder.setTitle("Device Details");
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

    private void dialog_DisableMobileData(String title_str, String msg_str) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);
        // dialogBuilder.setTitle("Device Details");
        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.GONE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();


                wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) { // If wifi disabled then enable it
                    wifiManager.setWifiEnabled(true);
                 /*   final ProgressDialog pDialog = new ProgressDialog(myContext);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Kindly wait...");
                    if (!pDialog.isShowing())
                        pDialog.show();*/

                    utill.showProgressDialog("Kindly wait...");
                    Log.e("msg", "Enabling wifi");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            utill.hideProgressDialog();
                            EnableLocation();

                        }
                    }, 3000);   //3 seconds
                } else {
                    EnableLocation();
                }


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

    //Switch
    private void RegisterDeviceA(String DeviceId,
                                 String DeviceName,
                                 String DeviceType,
                                 String Device_Password,
                                 String timezoneName,
                                 String timezoneID,
                                 String Pin_D0,
                                 String Pin_D1,
                                 String Pin_D2,
                                 String Pin_D3,
                                 String Pin_D4,
                                 String Pin_D5,
                                 String Pin_D6,
                                 String Pin_D7,
                                 String Pin_D8,
                                 String Pin_D9) {

        Log.e("api ---", "RegisterDeviceA");
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        TimeZone tz = TimeZone.getDefault();
       /* String timezoneName = tz.getDisplayName(false, TimeZone.SHORT);//GMT+05:30
        String timezoneID = tz.getID();//Asia/Kolkata
*/
        String tag_string_req = "req_login";
        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("UserId", Uid);
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("DeviceType", DeviceType);
        params.put("Device_Password", Device_Password);

        params.put("Pin_D0", Pin_D0);
        params.put("Pin_D1", Pin_D1);
        params.put("Pin_D2", Pin_D2);
        params.put("Pin_D3", Pin_D3);
        params.put("Pin_D4", Pin_D4);
        params.put("Pin_D5", Pin_D5);
        params.put("Pin_D6", Pin_D6);
        params.put("Pin_D7", Pin_D7);
        params.put("Pin_D8", Pin_D8);
        params.put("Pin_D9", Pin_D9);
        params.put("TimeZone", timezoneName);
        params.put("TimeZoneId", timezoneID);

        URL = UTIL.Domain_Arduino + UTIL.Register_DeviceA_API;


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        HideProgressDialog();
                        //  utill.HideProgressDialog();
                        //{"errormsg":"Device Registered","Status":"1","UserId":null}

                        try {
                            JSONObject jObj = response;
                            String errormsg = jObj.getString("errormsg");
                            String status = jObj.getString("Status");


                            if (status.equals("1")) {

                                dialog_resetDevice();


                            } else if (status.equals("2")) {
                                Toast.makeText(myContext,
                                        errormsg, Toast.LENGTH_SHORT).show();
                                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");


                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            //   Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                            Log.e("api err---", "RegisterDeviceA");

                            Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                //   Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Log.e("api err---", "RegisterDeviceA");
                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");


            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }

    //Water Level Monitor
    private void RegisterDeviceB(String DeviceId,
                                 String DeviceName,
                                 String DeviceType,
                                 String Device_Password,
                                 String timezoneName,
                                 String timezoneID,
                                 String Tank_type,
                                 String Tank_function_state,
                                 String Upper_level_depth,
                                 String Upper_level_water_capacity,
                                 String Pump_on_time_max,
                                 String Pump_cool_time_min
    ) {
        Log.e("api ---", "RegisterDeviceB");

        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        String tag_string_req = "req_login";
        String URL;


        Map<String, String> params = new HashMap<>();
        params.put("UserId", Uid);
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("DeviceType", DeviceType);
        params.put("Device_Password", Device_Password);
        params.put("TimeZone", timezoneName);
        params.put("TimeZoneId", timezoneID);

        params.put("Tank_type", Tank_type);
        params.put("Tank_function_state", Tank_function_state);
        params.put("Upper_level_depth", Upper_level_depth);
        params.put("Upper_level_water_capacity", Upper_level_water_capacity);
        params.put("Pump_on_time_max", Pump_on_time_max);
        params.put("pump_cool_time_min", Pump_cool_time_min);


        URL = UTIL.Domain_Arduino + UTIL.Register_DeviceB_API;


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        HideProgressDialog();

                        //{"errormsg":"Device Registered","Status":"1","UserId":null}

                        try {
                            JSONObject jObj = response;
                            String errormsg = jObj.getString("errormsg");
                            String status = jObj.getString("Status");


                            if (status.equals("1")) {

                                dialog_resetDevice();


                            } else if (status.equals("2")) {
                                Toast.makeText(myContext,
                                        errormsg, Toast.LENGTH_SHORT).show();
                                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            //   Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                            Log.e("api err---", "RegisterDeviceB");
                            Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                Log.e("api err---", "RegisterDeviceB");
            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }

    //Air quality monitor
    private void RegisterDeviceC(String DeviceId,
                                 String DeviceName,
                                 String DeviceType,
                                 String Device_Password,
                                 String timezoneName,
                                 String timezoneID) {

        Log.e("api ---", "RegisterDeviceC");
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        String tag_string_req = "req_login";
        String URL;


        Map<String, String> params = new HashMap<>();
        params.put("UserId", Uid);
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("DeviceType", DeviceType);
        params.put("Device_Password", Device_Password);
        params.put("TimeZone", timezoneName);
        params.put("TimeZoneId", timezoneID);

        URL = UTIL.Domain_Arduino + UTIL.Register_DeviceC_API;


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        HideProgressDialog();
                        //  utill.HideProgressDialog();
                        //{"errormsg":"Device Registered","Status":"1","UserId":null}

                        try {
                            JSONObject jObj = response;
                            String errormsg = jObj.getString("errormsg");
                            String status = jObj.getString("Status");


                            if (status.equals("1")) {

                                dialog_resetDevice();


                            } else if (status.equals("2")) {
                                Toast.makeText(myContext,
                                        errormsg, Toast.LENGTH_SHORT).show();
                                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            //    Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                            Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                            Log.e("api err---", "RegisterDeviceC");
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                //   Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                Log.e("api err---", "RegisterDeviceC");
            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }

    //Security monitor
    private void RegisterDeviceD(String DeviceId,
                                 String DeviceName,
                                 String DeviceType,
                                 String Device_Password,
                                 String timezoneName,
                                 String timezoneID) {

        Log.e("api ---", "RegisterDeviceD");
        String Uid = UTIL.getPref(myContext, UTIL.Key_UserId);
        String tag_string_req = "req_login";
        String URL;


        Map<String, String> params = new HashMap<>();
        params.put("UserId", Uid);
        params.put("DeviceId", DeviceId);
        params.put("DeviceName", DeviceName);
        params.put("DeviceType", DeviceType);
        params.put("Device_Password", Device_Password);
        params.put("TimeZone", timezoneName);
        params.put("TimeZoneId", timezoneID);

        params.put("TempRange_Max", Temp_MAX);
        params.put("TempRange_Min", Temp_MIN);
        params.put("HumidityRange_Max", Humidity_MAX);
        params.put("HumidityRange_Min", Humidity_MIN);
        params.put("TempHumState", TempHumState);


        Log.e("api params reg D ---", params.toString());

        URL = UTIL.Domain_Arduino + UTIL.Register_DeviceD_API;

        Log.e("api url reg D ---", URL);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        HideProgressDialog();
                        //  utill.HideProgressDialog();
                        //{"errormsg":"Device Registered","Status":"1","UserId":null}

                        try {
                            JSONObject jObj = response;
                            String errormsg = jObj.getString("errormsg");
                            String status = jObj.getString("Status");


                            if (status.equals("1")) {

                                dialog_resetDevice();


                            } else if (status.equals("2")) {
                                Toast.makeText(myContext,
                                        errormsg, Toast.LENGTH_SHORT).show();

                                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            //  Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                            Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");

                            Log.e("api err---", "RegisterDeviceC");
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                Dialog_showMessage("Error!", "Unable to register your device\nPlease try again");
                Log.e("api err---", "RegisterDeviceC");
            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }

    private void dialog_SetUP_watercontroller() {

        String title_str = "";
        String msg_str = "Please set up overhead and underground tank sensors \nas explained in instruction manual.";
        //dialog_DisableMobileData(title, msg);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setVisibility(View.GONE);
        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.VISIBLE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Intent i = new Intent(myContext, SetUpActivity.class);
                startActivityForResult(i, UTIL.Water_controller_SETTINGS);


            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                searchableSpinner_device.setHint(UTIL.SelectDevice);
                searchableSpinner_device.setText("");
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void dialog_SetUP_TempHumidity_Range() {

        String title_str = "";
        String msg_str = "Please set up Temperature and Humidity Range.";
        //dialog_DisableMobileData(title, msg);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setVisibility(View.GONE);
        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setText("Don't set");
        negativeBtn.setVisibility(View.VISIBLE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Intent i = new Intent(myContext, SetRangeActivity.class);
                startActivityForResult(i, UTIL.Security_monitor_SETTINGS);


            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //  searchableSpinner_device.setHint(UTIL.SelectDevice);
                //  searchableSpinner_device.setText("");

                TempHumState = "0";


            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void afterSetUP_dialog() {

        String title_str = "Thanks!";
        String msg_str = "Now provide wi-fi and other details";
        //dialog_DisableMobileData(title, msg);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText(title_str);
        message.setText(msg_str);
        positiveBtn.setText("Ok");
        negativeBtn.setText("No");
        negativeBtn.setVisibility(View.VISIBLE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                searchableSpinner_device.setEnabled(false);


            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                searchableSpinner_device.setHint(UTIL.SelectDevice);
                searchableSpinner_device.setText("");
                searchableSpinner_device.setEnabled(true);
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void RegisterDevice() {

        progressDialogsetMessage("Registering your device...");

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {


            ArrayList<String> arrayList_device_type_Switch =
                    new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
            ArrayList<String> arrayList_device_type_WaterLevelController =
                    new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
            ArrayList<String> arrayList_device_type_AirQualityMonitor =
                    new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));
            ArrayList<String> arrayList_device_type_SecurityMonitor =
                    new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));


            if (arrayList_device_type_Switch.contains(Device_Type)) {

                RegisterDeviceA(DeviceId, Device_Name,
                        Device_Type, Device_Pass, Time_Zone_Name, Time_Zone_Id,
                        "Switch_0", "Switch_1", "Switch_2",
                        "Switch_3", "Switch_4", "Switch_5",
                        "Switch_6", "Switch_7", "Switch_8",
                        "Switch_9");
            } else if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {


                RegisterDeviceB(DeviceId, Device_Name, Device_Type,
                        Device_Pass, Time_Zone_Name, Time_Zone_Id, Tank_type, Tank_function_state,
                        Upper_level_depth, Upper_level_water_capacity, Pump_on_time_max, Pump_cool_time_min);

            } else if (arrayList_device_type_AirQualityMonitor.contains(Device_Type)) {
                RegisterDeviceC(DeviceId, Device_Name, Device_Type,
                        Device_Pass, Time_Zone_Name, Time_Zone_Id);

            } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {
                RegisterDeviceD(DeviceId, Device_Name, Device_Type,
                        Device_Pass, Time_Zone_Name, Time_Zone_Id);

            }

        }
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

        // dialogBuilder.setTitle("Device Details");
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

    private void func2() {

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) { // If wifi disabled then enable it
            wifiManager.setWifiEnabled(true);
                 /*   final ProgressDialog pDialog = new ProgressDialog(myContext);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Kindly wait...");
                    if (!pDialog.isShowing())
                        pDialog.show();*/

            utill.showProgressDialog("Kindly wait...");
            Log.e("msg", "Enabling wifi");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    utill.hideProgressDialog();
                    EnableLocation();
                }
            }, 3000);   //3 seconds
        } else {
            EnableLocation();
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {

            sb = new StringBuilder();
            wifiList = wifiManager.getScanResults();
            sb.append("\n        Number Of Wifi connections :" + wifiList.size() + "\n\n");

            for (int i = 0; i < wifiList.size(); i++) {
                sb.append(new Integer(i + 1).toString() + ". ");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n\n");
            }


        }

    }


}
