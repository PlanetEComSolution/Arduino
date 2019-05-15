package com.pombingsoft.planet_iot.activity.switch_controller;

import android.Manifest;
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
import com.pombingsoft.planet_iot.activity.AddDeviceActivity_New;
import com.pombingsoft.planet_iot.location.common.activities.SampleActivityBase;
import com.pombingsoft.planet_iot.location.common.logger.Log;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class UpdateWifiCredActivity extends SampleActivityBase
        implements GoogleApiClient.OnConnectionFailedListener {
    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x7;
    // GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    /***************************/
    Context myContext;
    UTIL utill;
    WifiManager wifiManager;
    // WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    Spinner spin_Device, spin_Wifi, spin_Device_location;
    ImageView text_refresh_device, text_refresh_wifi;
    EditText et_WIFi_Pass;
    Button btnAddDevice;
    com.toptoche.searchablespinnerlibrary.SearchableSpinner searchableSpinner_timeZone;//, searchableSpinner_device, searchableSpinner_wifi;
    AutoCompleteTextView searchableSpinner_device, searchableSpinner_wifi;
    AlertDialog b;
    ProgressDialog progressDialog;
    ArrayList<String> arrayList_timeZone;
    String WIFi_SSID = "";
    String WIFi_Pass = "";
    String Device_SSID = "", Device_Type;
    ArrayList<String> list_Device = new ArrayList<>();
    ArrayList<String> list_Wifi = new ArrayList<>();
    int Try_Count = 0;
    AlertDialog alertDialog;
    String readDeviceId;
    ProgressDialog pDialog;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)      //for current loc(gps)
                .build();
        setContentView(R.layout.activity_update_wifi);
        utill = new UTIL(UpdateWifiCredActivity.this);
        myContext = UpdateWifiCredActivity.this;
        init_views();


        String title = "Recommended!";
        String msg = "Please disable mobile data,\nUse Wi-Fi connection.";
        dialog_DisableMobileData(title, msg);


      /*  wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) { // If wifi disabled then enable it
            wifiManager.setWifiEnabled(true);

            utill.showProgressDialog("Kindly wait...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    utill.HideProgressDialog();
                    EnableLocation();

                }
            }, 3000);   //5 seconds
        } else {
            EnableLocation();
        }*/


    }

    private void EnableLocation() {
        //  utill.showProgressDialog("Enabling Location...");
        /******************************for gps current location***********************************************/
        try {
            // Get the location manager
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                //utill.HideProgressDialog();
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
            } else {

                fill_Spinner();
                //  setData_Spinners();
                //utill.HideProgressDialog();

            }
        } catch (Exception e) {
            e.getCause();
        }
    }

    /******************************Methods for gps current location***********************************************/
    private void askForPermission(String permission, Integer requestCode) {

        //  utill.HideProgressDialog();
        if (ContextCompat.checkSelfPermission(UpdateWifiCredActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?


            if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateWifiCredActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(UpdateWifiCredActivity.this, new String[]{permission}, requestCode);
            } else {

                ActivityCompat.requestPermissions(UpdateWifiCredActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //  Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            //  utill.HideProgressDialog();

            askForGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

                //   Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                askForGPS();
            } else {
                Toast.makeText(this, "Please allow this permission!", Toast.LENGTH_SHORT).show();

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
                        // in onActivityResult().
                        try {
                            status.startResolutionForResult(UpdateWifiCredActivity.this, GPS_SETTINGS);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_SETTINGS) {

            if (resultCode == RESULT_OK) {
                //  Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
                // setData_Spinners();
                fill_Spinner();
            } else {
                Toast.makeText(getApplicationContext(), "GPS is not enabled!", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    private void EnableWifi() {
        // utill.showProgressDialog("Enabling Wifi ...");
        if (!wifiManager.isWifiEnabled()) {
            // If wifi disabled then enable it
            wifiManager.setWifiEnabled(true);
            // utill.HideProgressDialog();

            EnableLocation();

        } else {
            //  utill.HideProgressDialog();
            EnableLocation();

        }

    }

    private void init_views() {
        spin_Device = (Spinner) findViewById(R.id.spin_Device);
        text_refresh_device = findViewById(R.id.text_refresh_device);

        spin_Wifi = findViewById(R.id.spin_Wifi);
        text_refresh_wifi = findViewById(R.id.text_refresh_wifi);
        et_WIFi_Pass = findViewById(R.id.et_WIFi_Pass);
        spin_Device_location = findViewById(R.id.spin_Device_location);
        btnAddDevice = findViewById(R.id.btnAddDevice);
        searchableSpinner_timeZone = findViewById(R.id.searchableSpinner);
        searchableSpinner_timeZone.setTitle("Time Zone");
        searchableSpinner_timeZone.setPositiveButton("OK");


       /* searchableSpinner_device.setTitle("Device(NodeMcu)");
        searchableSpinner_device.setPositiveButton("OK");*/


        searchableSpinner_device = findViewById(R.id.searchableSpinner_device);
        searchableSpinner_device.setHint(UTIL.SelectDevice);
        searchableSpinner_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchableSpinnerDialog(searchableSpinner_device, list_Device, "Device(NodeMcu)");
            }
        });

        searchableSpinner_wifi = findViewById(R.id.searchableSpinner_wifi);
      /*  searchableSpinner_wifi.setTitle("Wifi");
        searchableSpinner_wifi.setPositiveButton("OK");*/
        searchableSpinner_wifi.setHint(UTIL.SelectWIFI);
        searchableSpinner_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchableSpinnerDialog(searchableSpinner_wifi, list_Wifi, "Wi-Fi");
            }
        });


        // searchableSpinner.setA
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


        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // int selectedDevicePos = searchableSpinner_device.getSelectedItemPosition();
                Device_SSID = searchableSpinner_device.getText().toString().trim();


                //  int selectedWifiPos = searchableSpinner_wifi.getSelectedItemPosition();
                WIFi_SSID = searchableSpinner_wifi.getText().toString().trim();

                WIFi_Pass = et_WIFi_Pass.getText().toString().trim();


                if (Device_SSID.trim().equals("") || Device_SSID.equals(UTIL.SelectDevice)) {
                    Toast.makeText(UpdateWifiCredActivity.this, "Please select a Device!", Toast.LENGTH_SHORT).show();
                } else if (WIFi_SSID.trim().equals("") || WIFi_SSID.equals(UTIL.SelectWIFI)) {
                    Toast.makeText(UpdateWifiCredActivity.this, "Please select your Wifi!", Toast.LENGTH_SHORT).show();

                } else if (WIFi_Pass.isEmpty()) {
                    Toast.makeText(UpdateWifiCredActivity.this, "Please enter your Wifi Password!", Toast.LENGTH_SHORT).show();


                } else {


                    String[] device_desc = Device_SSID.split("_");//tNode_A1,tNode_A2,tNode_A4,tNode_A8,tNode_B,tNode_C
                    Device_Type = device_desc[1];//A1,A2,A4,A8,B,C

                    //  Toast.makeText(AddDeviceActivity_New.this, Time_Zone_Name, Toast.LENGTH_SHORT).show();

                    connectWithDevice_readDeviceID();
                    //  connectWithDevice_WriteData();

                }
            }
        });


    }

    private void setData_Spinners() {

        getAvailableNodeMcu();
        getAvailableWifi();
        //  setTimeZone();

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

    public void connectWithDevice_readDeviceID() {
        ShowProgressDialog();
        //  utill.showProgressDialog("Kindly wait..");
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
                    // yourMethod();
                    // utill.HideProgressDialog();

                    //  ReadDeviceID();

                    if (UTIL.isConnectedToNodeMcu(myContext)) {
                        Try_Count = 0;
                        ReadDeviceID();
                    } else {

                        if (Try_Count < 3) {
                            connectWithDevice_readDeviceID();
                        } else {
                            //  Toast.makeText(UpdateWifiCredActivity.this, "Unable to connect to Device!Try Again!", Toast.LENGTH_SHORT).show();
                            HideProgressDialog();
                            Try_Count = 0;
                            Dialog_showMessage("Error!", "Unable to connect to device\nPlease try again");

                        }

                    }


                }
            }, 4000);   //5 seconds


        } else {
            //  utill.HideProgressDialog();

            WriteDataToDevice();
        }


    }

    private void ReadDeviceID() {

        Log.e("API ReadDeviceID---", "ReadDeviceID");
        String tag_string_req = "req_login";
        //  utill.showProgressDialog("Reading Device ID...");
        String URL_LOGIN = null;

        progressDialogsetMessage("Reading device data ...");


        try {
            URL_LOGIN = UTIL.URL_Device + "/getEEPROM_id";

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //   utill.HideProgressDialog();

                try {

                    readDeviceId = response.trim();
                    DisconnectNodeMcu_and_connect_internet_toCheckDeviceDetails();

                } catch (Exception e) {
                    // JSON error
                    HideProgressDialog();
                    e.printStackTrace();
                    // Toast.makeText(myContext, "Some error occurred! ", Toast.LENGTH_LONG).show();
                    Dialog_showMessage("Error!", "Unable to read device id\nPlease try again");

                    Log.e("API ReadDeviceID err---", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
                // Toast.makeText(myContext, "Some error occurred! ", Toast.LENGTH_LONG).show();
                Dialog_showMessage("Error!", "Unable to read device id\nPlease try again");

                Log.e("API ReadDeviceID err---", error.toString());
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    void DisconnectNodeMcu_and_connect_internet_toCheckDeviceDetails() {
        // utill.showProgressDialog("Disconnecting Device...");
        progressDialogsetMessage("Disconnecting from device...");

        Try_Count++;
        try {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            wifiManager.disconnect();
            for (WifiConfiguration i : list) {

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
                        getDeviceDetail();
                    } else {
                        if (Try_Count < 3) {

                            DisconnectNodeMcu_and_connect_internet_toCheckDeviceDetails();
                        } else {
                            Try_Count = 0;
                            HideProgressDialog();
                       /* Toast.makeText(myContext,
                                UTIL.NoInternet, Toast.LENGTH_SHORT)
                                .show();*/


                            Dialog_showMessage("Error!", "Wifi credentials might be incorrect!\nPlease try again with correct credentials");


                        }


                    }

                }
            }, 5000);   //5 seconds


        } catch (Exception e) {
            e.getCause();
            HideProgressDialog();
            Toast.makeText(myContext,
                    "Some error occurred!", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    void getDeviceDetail() {

        progressDialogsetMessage("Checking device details...");

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

                //utill.HideProgressDialog();
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
                                HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                connectWithDevice_WriteData();
                            }
                        } else {

                            HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    HideProgressDialog();
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


                HideProgressDialog();
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
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsA_API + "DeviceId=" + readDeviceId + "&UserId=" + Uid;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


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
                                HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                connectWithDevice_WriteData();
                            }
                        } else {

                            HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    HideProgressDialog();
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
                HideProgressDialog();
                //  pDialog.dismiss();
                //utill.HideProgressDialog();
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
                                HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                connectWithDevice_WriteData();
                            }
                        } else {

                            HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    HideProgressDialog();
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
                HideProgressDialog();
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
                                HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                connectWithDevice_WriteData();
                            }
                        } else {

                            HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    HideProgressDialog();
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
                HideProgressDialog();
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

    public void connectWithDevice_WriteData() {
        //  utill.showProgressDialog("Kindly wait..");

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
                            //  Toast.makeText(UpdateWifiCredActivity.this, "Unable to connect to Device!Try Again!", Toast.LENGTH_SHORT).show();
                            HideProgressDialog();
                            Try_Count = 0;


                            Dialog_showMessage("Error!", "Unable to connect to device\nPlease try again");


                        }
                    }

                }
            }, 5000);   //5 seconds


        } else {

            Try_Count = 0;
            WriteDataToDevice();
        }


    }

    public void WriteDataToDevice() {

        progressDialogsetMessage("Transferring data to device...");


        if (UTIL.isConnectedToNodeMcu(myContext)) {

            //     boolean isMobileDataEnabled = UTIL.isMobileDataEnabled(myContext);
            //   if (!isMobileDataEnabled) {
            writeWifiCredentials();
            //  }
            /*else {
                HideProgressDialog();

                Toast.makeText(UpdateWifiCredActivity.this, "Disable mobile data!", Toast.LENGTH_SHORT).show();

            }
*/

        } else {
            if (Try_Count < 3) {//try 2 times
                connectWithDevice_WriteData();
            } else {
                HideProgressDialog();
                // utill.HideProgressDialog();
                Toast.makeText(UpdateWifiCredActivity.this, "Not connected to Device!Try Again!", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void writeWifiCredentials() {

        String tag_string_req = "req_login";
        // utill.showProgressDialog("Writing WIFi Credentials...");
        Log.e("API writeWifiCredentials  ---", "");


        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.URL_Device + "/set_wifi?a=" + WIFi_SSID + "&b=" + WIFi_Pass;
            URL_LOGIN = URL_LOGIN.replaceAll(Pattern.quote(" "), "%20");//nks
            Log.e("API writeWifiCredentials url ---", URL_LOGIN);


        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //    utill.HideProgressDialog();
                try {
                    DisconnectNodeMcu_and_connect_internet_toResetDevice();
                } catch (Exception e) {
                    // JSON error
                    HideProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(myContext, "Some error occurred! ", Toast.LENGTH_LONG).show();
                    Log.e("API writeWifiCredentials err ---", e.getMessage());

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();

                Toast.makeText(myContext, "Some error occurred! ", Toast.LENGTH_LONG).show();
                Log.e("API writeWifiCredentials err ---", error.getMessage());

            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    void DisconnectNodeMcu_and_connect_internet_toResetDevice() {

        progressDialogsetMessage("Disconnecting from device ...");
        Try_Count++;
        try {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            wifiManager.disconnect();
            for (WifiConfiguration i : list) {
                //  String ssid = i.SSID;
                //   if (ssid != null && ssid.startsWith("\"" + UTIL.DeviceNamePrefix)) {
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
                // }
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

                    HideProgressDialog();
                    dialog_resetDevice();
                }
            }, 5000);   //5 seconds


            // wifiManager.reconnect();

        } catch (Exception e) {
            e.getCause();
        }

    }

    private void dialog_msg(String str_title, String str_msg) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
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

    private void ShowProgressDialog() {
        if (progressDialog == null || (!progressDialog.isShowing())) {
            progressDialog = ProgressDialog.show(myContext, null, null, true);
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


    private void fill_Spinner() {
        //   boolean isMobileDataEnabled = UTIL.isMobileDataEnabled(myContext);
        //   if (!isMobileDataEnabled) {//mobile data off is necessary to call api of nodMcu in MI devices
        setData_Spinners();
        //   } else {

        HideProgressDialog();

           /* String title = "Mobile data is ON!";
            String msg = "Please disable mobile data!";
            dialog_DisableMobileData(title, msg);*/

        //  }
    }

    private void dialog_DisableMobileData(String title_str, String msg_str) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
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

                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) { // If wifi disabled then enable it
                    wifiManager.setWifiEnabled(true);

                    ShowProgressDialog();
                    progressDialogsetMessage("Kindly wait...");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            HideProgressDialog();
                            EnableLocation();

                        }
                    }, 3000);   //5 seconds
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

    private void dialog_resetDevice() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Wi-Fi credentials updated successfully!");
        message.setText("Please restart device!");
        positiveBtn.setText("Ok");
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();

            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    public void showSearchableSpinnerDialog(final AutoCompleteTextView autoCompleteTextView, final ArrayList<String> data, String dialog_title) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        //  final ArrayList<String> arrayList = TimeZoneExample.getAllTimeZone();
        final ArrayList<String> list = new ArrayList<>();

        //list = new ArrayList<>();
        //  list.add("--Select Device Time Zone--");
        //  list.addAll(arrayList);

        final EditText autoText_TimeZone = (EditText) dialogView.findViewById(R.id.autoText_TimeZone);
        // autoText_TimeZone.setHint("--Select Device Time Zone--");

        final ListView listvw = (ListView) dialogView.findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, data);
        listvw.setAdapter(adapter);
        listvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String itemValue = (String) listvw.getItemAtPosition(position);


                //  autoText_.setText(itemValue);
                autoCompleteTextView.setText(itemValue);

                b.dismiss();
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
        //  dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                b.dismiss();
               /* if(autoText_TimeZone.getText().toString().trim().length()>0) {
                    autoText_.setText(autoText_TimeZone.getText().toString().trim());
                }*/
            }
        });

        b = dialogBuilder.create();
        b.show();
    }

    private void Dialog_showMessage(String title_str, String msg_str) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
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
}
