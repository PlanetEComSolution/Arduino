package com.pombingsoft.planet_iot.activity.fragment_new;

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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.drive.Drive;
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


public class UpdateWifiCredNew_Fragment extends SampleActivityBase_New
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
    ArrayList<DeviceNodeMcu> list_DeviceNodeMcuNew = new ArrayList<>();
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
        View rootView = inflater.inflate(R.layout.activity_update_wifi_new, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                //.enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(LocationServices.API)      //for current loc(gps)
                .build();

        getActivity().setTitle("Update Wi-Fi Credentials");
        init_views(rootView);


//        String title = "Recommended!";
//        String msg = "Please disable mobile data,\nUse Wi-Fi connection.";
//        dialog_DisableMobileData(title, msg);

        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            getDeviceList_ApiCall_New();
        } else {
            DialogInternet();
        }


        return rootView;
    }

    private void enable_WIFI() {
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (!wifiManager.isWifiEnabled()) { // If wifi disabled then enable it
                wifiManager.setWifiEnabled(true);
                utill.showProgressDialog("");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        utill.hideProgressDialog();
                        EnableLocation();
                    }
                }, 3000);   //5 seconds
            } else {
                EnableLocation();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please enable Wi-fi and Location!", Toast.LENGTH_SHORT).show();
            e.getCause();
        }
    }


    private void EnableLocation() {
        //  utill.showProgressDialog("Enabling Location...");
        /******************************for gps current location***********************************************/
        try {
            // Get the location manager
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                //utill.HideProgressDialog();
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
            } else {

                fill_Spinner();
                //setData_Spinners();
                //utill.HideProgressDialog();

            }
        } catch (Exception e) {
            e.getCause();
        }
    }

    /******************************Methods for gps current location***********************************************/
    private void askForPermission(String permission, Integer requestCode) {

        //  utill.HideProgressDialog();
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
            //  Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            //  utill.HideProgressDialog();

            askForGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {

                //   Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();

                askForGPS();
            } else {
                Toast.makeText(getActivity(), "Please allow getActivity() permission!", Toast.LENGTH_SHORT).show();

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
                // setData_Spinners();
                fill_Spinner();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "GPS is not enabled!", Toast.LENGTH_LONG).show();
                ((ActivityWithNavigationMenu) getActivity()).addDeviceListFragment();
            }

        }
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


    private void init_views(View rootView) {
        spin_Device = (Spinner) rootView.findViewById(R.id.spin_Device);
        text_refresh_device = rootView.findViewById(R.id.text_refresh_device);

        spin_Wifi = rootView.findViewById(R.id.spin_Wifi);
        text_refresh_wifi = rootView.findViewById(R.id.text_refresh_wifi);
        et_WIFi_Pass = rootView.findViewById(R.id.et_WIFi_Pass);
        spin_Device_location = rootView.findViewById(R.id.spin_Device_location);
        btnAddDevice = rootView.findViewById(R.id.btnAddDevice);
        searchableSpinner_timeZone = rootView.findViewById(R.id.searchableSpinner);
        searchableSpinner_timeZone.setTitle("Time Zone");
        searchableSpinner_timeZone.setPositiveButton("OK");


       /* searchableSpinner_device.setTitle("DeviceNodeMcu(NodeMcu)");
        searchableSpinner_device.setPositiveButton("OK");*/


        searchableSpinner_device = rootView.findViewById(R.id.searchableSpinner_device);
        searchableSpinner_device.setHint(UTIL.SelectDevice);
        searchableSpinner_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    showSearchableSpinnerDialog(searchableSpinner_device, list_Device, "DeviceNodeMcu(NodeMcu)");
                showSearchableSpinnerDialogDevice(searchableSpinner_device, list_DeviceNodeMcuNew, "Device(NodeMcu)");

            }
        });

        searchableSpinner_wifi = rootView.findViewById(R.id.searchableSpinner_wifi);
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
                    Toast.makeText(getActivity(), "Please select a DeviceNodeMcu!", Toast.LENGTH_SHORT).show();
                } else if (WIFi_SSID.trim().equals("") || WIFi_SSID.equals(UTIL.SelectWIFI)) {
                    Toast.makeText(getActivity(), "Please select your Wifi!", Toast.LENGTH_SHORT).show();

                } else if (WIFi_Pass.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your Wifi Password!", Toast.LENGTH_SHORT).show();

                } else if (WIFi_Pass.contains(" ")) {
                    Toast.makeText(getActivity(), "Please don't use blank space in Wi-Fi credentials!", Toast.LENGTH_SHORT).show();

                } else {


                    //  String[] device_desc = Device_SSID.split("_");//tNode_A1,tNode_A2,tNode_A4,tNode_A8,tNode_B,tNode_C
                    //  Device_Type = device_desc[1];//A1,A2,A4,A8,B,C

                    //  Toast.makeText(AddDeviceActivity_New.this, Time_Zone_Name, Toast.LENGTH_SHORT).show();

                    //  connectWithDevice_readDeviceID();
                    //  connectWithDevice_WriteData();

                    dialog_msg_wifi("Are you sure Wi-Fi credentials are correct?", "otherwise device will not be able to connect to internet.");


                }
            }
        });




    }

    private void setData_Spinners() {
        //  getAvailableNodeMcu();
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


    }

    void getDeviceDetail() {

        // progressDialogsetMessage("Checking device details...");
        utill.showProgressDialog("Checking device details..");
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
                                //  HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");
                            } else {
                                // connectWithDevice_WriteData();
                                UpdateWiFionServer(readDeviceId, Device_Type, WIFi_SSID, WIFi_Pass);
                            }
                        } else {

                            //  HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        //   HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    //   HideProgressDialog();

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
                //    HideProgressDialog();
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
                                //   HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                // connectWithDevice_WriteData();
                                UpdateWiFionServer(readDeviceId, Device_Type, WIFi_SSID, WIFi_Pass);

                            }
                        } else {

                            //   HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        //  HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


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
                utill.hideProgressDialog();
                //   HideProgressDialog();
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
                                UpdateWiFionServer(readDeviceId, Device_Type, WIFi_SSID, WIFi_Pass);

                            }
                        } else {

                            //    HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        // HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
                        dialog_msg(title, msg);


                    }


                } catch (JSONException e) {
                    // JSON error
                    // HideProgressDialog();
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
                utill.hideProgressDialog();
                //    HideProgressDialog();
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
                                //        HideProgressDialog();

                                String title = "This device is locked.";
                                String msg = "Please unlock first";
                                dialog_msg(title, msg);
                                //  dialogWithPositiveBtn("This device is locked.\nPlease unlock first.");

                            } else {
                                //  connectWithDevice_WriteData();
                                UpdateWiFionServer(readDeviceId, Device_Type, WIFi_SSID, WIFi_Pass);

                            }
                        } else {

                            //   HideProgressDialog();
                            String title = "Sorry";
                            String msg = "You can not update Wi-Fi credentials on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        //    HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update Wi-Fi credentials on this device!";
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
                utill.hideProgressDialog();
                //   HideProgressDialog();
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

    private void dialog_msg_wifi(String str_title, String str_msg) {

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
        // negativeBtn.setVisibility(View.GONE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                    getDeviceDetail();
                } else {
                    HideProgressDialog();
                    DialogInternet();
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
        title.setText("Wi-Fi credentials updated successfully!");
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

    public void showSearchableSpinnerDialog(final AutoCompleteTextView autoCompleteTextView, final ArrayList<String> data, String dialog_title) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                if (itemValue.contains(" ")) {
                    Toast.makeText(getActivity(), "Please don't use blank space in Wi-Fi credentials!", Toast.LENGTH_SHORT).show();

                    autoCompleteTextView.setText(UTIL.SelectWIFI);

                } else {
                    autoCompleteTextView.setText(itemValue);
                }

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

    public void showSearchableSpinnerDialogDevice(final AutoCompleteTextView autoCompleteTextView, final ArrayList<DeviceNodeMcu> data, String dialog_title) {

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

        b = dialogBuilder.create();
        b.show();
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
                enable_WIFI();

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

    private void UpdateWiFionServer(String DeviceId,
                                    String DeviceType,
                                    String timezoneName,
                                    String timezoneID
    ) {

        utill.showProgressDialog("Updating Wi-Fi ...");
        String tag_string_req = "req_login";
        String URL = null;


        Map<String, String> params = new HashMap<>();
        params.put("DeviceId", DeviceId);
        params.put("DeviceType", DeviceType);
        params.put("timezoneName", timezoneName);
        params.put("timezoneID", timezoneID);

        URL = UTIL.Domain_Arduino + UTIL.UpdateWiFiCred_API;

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
                            Log.e("api err---", "UpdateWiFionServer");
                            Dialog_showMessage("Error!", "Unable to update time zone!\nPlease try again");
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utill.hideProgressDialog();
                //   Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_LONG).show();
                Log.e("api err---", "UpdateWiFionServer");
                Dialog_showMessage("Error!", "Unable to update time zone!\nPlease try again");


            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


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
