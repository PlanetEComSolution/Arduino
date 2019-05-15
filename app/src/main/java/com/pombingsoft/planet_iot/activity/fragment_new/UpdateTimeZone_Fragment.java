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


public class UpdateTimeZone_Fragment extends SampleActivityBase_New
        implements GoogleApiClient.OnConnectionFailedListener {
    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x7;


    // GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    /***************************/
    Context myContext;
    UTIL utill;
    String DeviceId = "";
    WifiManager wifiManager;

    List<ScanResult> wifiList;

    Spinner spin_Device;
    ImageView text_refresh_device;

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
    ArrayList<String> list_Device = new ArrayList<>();


    int SettingRequestCode_ToUpdateGMTonServer = 123;
    int SettingRequestCode_ToCheckDeviceDetail = 456;
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
        View rootView = inflater.inflate(R.layout.frgmnt_update_timezone, container, false);

        myContext = getActivity();
        utill = new UTIL(myContext);
        mGoogleApiClient = new GoogleApiClient.Builder(myContext)
                // .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)      //for current loc(gps)
                .build();


        getActivity().setTitle("Update Device Time Zone");


        init_views(rootView);
        String title = "Recommended!";
        String msg = "Please disable mobile data,\nUse Wi-Fi connection.";
        dialog_DisableMobileData(title, msg);

        return rootView;
    }


    private void init_views(View rootView) {
        spin_Device = (Spinner) rootView.findViewById(R.id.spin_Device);
        text_refresh_device = rootView.findViewById(R.id.text_refresh_device);


        btnAddDevice = rootView.findViewById(R.id.btnAddDevice);

        searchableSpinner_device = rootView.findViewById(R.id.searchableSpinner_device);
        searchableSpinner_device.setHint(UTIL.SelectDevice);
        searchableSpinner_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSearchableSpinnerDialog(searchableSpinner_device, list_Device, "Device(NodeMcu)");

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

                    String[] device_desc = Device_SSID.split("_");//tNode_A1,tNode_A2,tNode_A4,tNode_A8,tNode_B,tNode_C
                    Device_Type = device_desc[1];//A1,A2,A4,A8,B,C1,C2,C3,C4
                    connectWithDevice_readDeviceID();

                }
            }
        });
    }

    private void EnableLocation() {


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

                if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                    getTimeZoneList();
                } else {
                    HideProgressDialog();
                    DialogInternet();
                }


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

                getTimeZoneList();

            } else {
                Toast.makeText(getActivity(), "GPS is not enabled!", Toast.LENGTH_LONG).show();

                ((ActivityWithNavigationMenu) getActivity()).addDeviceListFragment();
            }

        } else if (requestCode == SettingRequestCode_ToUpdateGMTonServer) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                UpdateGMTonServer(readDeviceId, Device_Type, Time_Zone_Name, Time_Zone_Id);
            } else {
                Dialog_ConnectInternet(SettingRequestCode_ToUpdateGMTonServer);
            }


        } else if (requestCode == SettingRequestCode_ToCheckDeviceDetail) {

            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getDeviceDetail();
            } else {
                Dialog_ConnectInternet(SettingRequestCode_ToCheckDeviceDetail);
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


    private void getTimeZoneList() {
        List_timeZone.clear();
        Log.e("API---", "getTimeZoneList");
        String tag_string_req = "req_login";

        ShowProgressDialog();
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

                setData_Spinners();


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

        HideProgressDialog();

        if (list_Device.size() < 2) {
            Dialog_showMessage("No device found!", "Please switch off and switch on device\nThen try again");
        }
    }

    private void getAvailableNodeMcu() {
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();
        String device_name = UTIL.DeviceNamePrefix;


        list_Device.clear();
        list_Device.add(UTIL.SelectDevice);
        for (int i = 0; i < wifiList.size(); i++) {
            String wifi_ssid = wifiList.get(i).SSID;
            String wifi_bssid = wifiList.get(i).BSSID;
            if ((wifi_ssid != null) && (wifi_ssid.startsWith(device_name))) {
                list_Device.add(wifi_ssid);
            }


        }


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
                    DisconnectNodeMcu_and_connect_internet_toCheckDeviceDetails();

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
            progressDialogsetMessage("Connecting to internet ...");

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
                            Dialog_ConnectInternet(SettingRequestCode_ToCheckDeviceDetail);
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
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update time zone on this device!";
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
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetDeviceDetailsB_API + "DeviceId=" + readDeviceId + "&UserId=" + Uid;

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
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();

                        Dialog_showMessage("Sorry!", "Unable to get device details\nPlease try again");


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
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update time zone on this device!";
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
                            String msg = "You can not update time zone on this device!";
                            dialog_msg(title, msg);

                        }


                    } else {
                        HideProgressDialog();
                        String title = "Sorry";
                        String msg = "You can not update time zone on this device!";
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


    private void dialog_msg(String str_title, String str_msg) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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

            writeGMT();

        }

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

                    DisconnectNodeMcu_and_connect_internet_toUpdateGMTonServer();


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

    void DisconnectNodeMcu_and_connect_internet_toUpdateGMTonServer() {
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
            progressDialogsetMessage("Connecting to internet ...");

            wifiManager.reconnect();


            //now wait
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {


                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        Try_Count = 0;

                        UpdateGMTonServer(readDeviceId, Device_Type, Time_Zone_Name, Time_Zone_Id);


                    } else {
                        if (Try_Count < 3) {

                            DisconnectNodeMcu_and_connect_internet_toUpdateGMTonServer();
                        } else {
                            Try_Count = 0;
                            HideProgressDialog();
                                   Dialog_ConnectInternet(SettingRequestCode_ToUpdateGMTonServer);
                        }
                    }






                }
            }, 5000);   //5 seconds


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
        title.setText("Device updated successfully!");
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
    private void UpdateGMTonServer(String DeviceId,
                                   String DeviceType,
                                   String timezoneName,
                                   String timezoneID
    ) {



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
                        HideProgressDialog();

                        try {
                            JSONObject jObj = response;
                            String errormsg = jObj.getString("errormsg");
                            String status = jObj.getString("Status");
                            if (status.equals("1")) {

                                dialog_resetDevice();

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
                HideProgressDialog();
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

    private void Dialog_ConnectInternet(final int REQUEST_CODE) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
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

}
