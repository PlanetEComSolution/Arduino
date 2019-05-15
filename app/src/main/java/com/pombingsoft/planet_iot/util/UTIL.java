package com.pombingsoft.planet_iot.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.pombingsoft.planet_iot.R;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.pombingsoft.planet_iot.util.AppController.TAG;

/**
 * Created by Admin on 7/25/2017.
 */

public class UTIL {
    public static final String HEADER = "http://";
    public static final Integer Water_controller_SETTINGS = 102;
    public static final Integer Security_monitor_SETTINGS = 202;
    public static String URL_Device = "http://192.168.4.1";
    public static String URL_Cloud = "https://cloud.arest.io/";
    public static String Login_API = "/api/Arduino/Login?";//string mobileno, string pwd
    public static String Register_User_API = "/api/Arduino/RegisterUser?";//string Username, string Password, string Mobile, string EmailId
    public static String Register_Device_API = "/api/Arduino/RegisterDevice?";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String Register_DeviceA_API = "/api/Arduino/RegisterDeviceA";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String UpdateTimeZone_API = "/api/Arduino/UpdateTimeZone";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String UpdateWiFiCred_API = "/api/Arduino/UpdateWifiCRED";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String FCMTokenUpdate_API = "/api/Arduino/UpdateTokenNew";


    public static String Register_DeviceB_API = "/api/Arduino/RegisterDeviceB";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String Register_DeviceC_API = "/api/Arduino/RegisterDeviceC";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String Register_DeviceD_API = "/api/Arduino/RegisterDeviceD";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String GetDeviceDetailsA_API = "/api/Arduino/GetDeviceDetailsA?";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String GetDeviceDetailsB_API = "/api/Arduino/GetDeviceDetailsB?";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String GetDeviceDetailsC_API = "/api/Arduino/GetDeviceDetailsC?";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String GetDeviceDetailsD_API = "/api/Arduino/GetDeviceDetailsD?";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String GetDeviceErrorList_API = "/api/Arduino/GetDeviceError?";//string UserId, string DeviceId, string DeviceName, string Pin_D4, string Pin_D5, string Pin_D6, string Pin_D7, string Pin_D8
    public static String getDeviceList_API = "/api/Arduino/GetDeviceListData?";//string uid
    public static String getDeviceList_New_API = "/api/Arduino/GetDeviceListDataNew?";//string uid

    public static String getDeviceDataLog_API = "/api/Arduino/GetDeviceDDataLog?";//string uid
    public static String getDeviceID_API = "/api/Arduino/GetDeviceId?";//string UserId
    public static String getTimeZoneList_API = "/api/Arduino/GetTimeZone";//string UserId
    public static String getPIN_Status_API = "/api/Arduino/GetDevicePINStatus?";//string UserId
    public static String ControlDevice_API = "/api/Arduino/ControlDevice?";//string UserId
    public static String ControlDeviceA_API = "/api/Arduino/ControlDeviceA?";//string UserId
    public static String UpdateDeviceInfo_API = "/api/Arduino/UpdateDevice?";//string UserId
    public static String UpdateDeviceDetails_A_API = "/api/Arduino/UpdateDeviceA?";//string UserId
    public static String UpdateDeviceDetails_C_API = "/api/Arduino/UpdateDeviceC?";//string UserId
    public static String UpdateDeviceDetails_D_API = "/api/Arduino/UpdateDeviceD?";//string UserId
    public static String UpdateRangeDevice_D_API = "/api/Arduino/UpdateRangeD?";//string UserId

    public static String UpdateDeviceDetails_B_API = "/api/Arduino/UpdateDeviceB?";//string UserId
    public static String SetDimmer_API = "/api/Arduino/SetDimmerStatus";//string UserId
    public static String Dimmer_Low = "0";
    public static String Dimmer_Medium = "125";
    public static String Dimmer_High = "255";
    public static String CheckDeviceRegistration_API = "/api/Arduino/CheckDeviceRegistration?";//string UserId
    public static String SetTimer_API = "/api/Arduino/SetDeviceTimer?";//string UserId
    public static String DeleteDevice_API = "/api/Arduino/DeleteDeviceee?";//string UserId
    public static String DeleteDevice_API_New = "/api/Arduino/DeleteDevice?";//string UserId
    public static String AddExistingDevice_API = "/api/Arduino/AddExistingDevice?";//string UserId
    public static String DeviceUsersDetail_API = "/api/Arduino/GetDeviceDetailsUser?";//string UserId
    public static String UpdateUserRole_API = "/api/Arduino/UpdateRole";//string UserId
    public static String AddScheduler_API = "/api/Arduino/AddScheduler";//string UserId
    public static String AddSchedulerB_API = "/api/Arduino/AddSchedulerB?";//string UserId
    public static String AddSchedulerD_API = "/api/Arduino/AddSchedulerD?";//string UserId
    public static String SetBuzzer_Device_C_API = "/api/Arduino/SetBuzzerC?";//string UserId
    public static String SetBuzzer_Device_D_API = "/api/Arduino/SetBuzzerD?";//string UserId
    public static String SetActive_Inactive_Device_C_API = "/api/Arduino/SetAciveInactiveC?";//string UserId
    public static String SetActive_Inactive_Device_D_API = "/api/Arduino/SetAciveInactiveD?";//string UserId
    public static String SetActive_Inactive_Device = "/api/Arduino/SetAciveInactive?";//string UserId
    public static String SendTextDeviceC_API = "/api/Arduino/SendTextDeviceC";//string UserId
    public static String SetNotificationRead_API = "/api/Arduino/SetNotificarionRead?";//string UserId
    public static String GetProfile_API = "/api/Arduino/GetProfile?";//string UserId
    public static String UpdateProfile_API = "/api/Arduino/UpdateProfile?";//string UserId
    public static String ChangePassword_API = "/api/Arduino/ChangePassword?";//string UserId
    public static String GetContact_API = "/api/Arduino/GetContact?";//string UserId
    public static String GetHelp_API = "/api/Arduino/GetHelp?";//string UserId
    public static String GetAbout_API = "/api/Arduino/GetAbout?";//string UserId
    public static String DeviceNamePrefix = "tNode";//string UserId
    public static String DevicePASS = "password";//string UserId
    public static String Key_GENDER = "gender";//string UserId
    public static String Key_RoleId_Admin = "1";
    public static String Key_RoleId_Sub_admin = "2";
    public static String Key_RoleId_Manager = "3";
    public static String Key_RoleId_General = "4";
    public static String Key_TimeZone = "11";
    public static String Key_Wifi = "111";

    public static String Text_Scheduler_Not_Set = "Not Set";//string UserId
    public static String Text_Scheduler_1 = "Scheduler 1";
    public static String Text_Scheduler_2 = "Scheduler 2";
    public static String Text_Scheduler_3 = "Scheduler 3";
    public static int Error_val = -5555;
    public static int Second_Online = 40;//20
    public static int Second_Online_For_Device_D = 10;//20
   // public static int Second_Online_For_Device_D = 5*60;//20
    public static int Time_Recall = 5000;
    /*change below api*/
    public static String ONLINE = "Online";
    public static String OFFLINE = "Offline";
    public static String ERROR = "Error";
    public static String Lock_Unlock_API = "/api/Arduino/LockUnLockDevice";//string UserId
    public static String getDeviceDetails_API = "/api/Arduino/CheckDeviceRegistration?";//string UserId
    public static String Device_Switch_A = "A";
    public static String Device_Switch_A1 = "A1";
    public static String Device_Switch_A2 = "A2";
    public static String Device_Switch_A4 = "A4";
    public static String Device_Switch_A8 = "A8";
    public static String Key_NoData = "Error";//string UserId
    public static String NoInternet = "Kindly check your internet connection!";
    public static String Key_UserId = "Key_UserId";
    public static String Key_USERNAME = "USERNAME";
    public static String Key_Schedule = "Schedule";
    public static String Key_FCMTOken = "FCMTOken";
    public static String Key_Mobile = "Mobile";
    public static String Key_DeviceId = "DeviceId";
    public static String Key_Scheduler_Num = "Scheduler_Num";
    public static String Key_Pin_Num = "Pin_Num";
    public static String Key_Scheduler_Time = "Scheduler_Time";
    public static String Key_Scheduler_SavedAt = "SavedAt";
    public static String Server = "Server";
    public static String Local = "Local";
    public static String SelectDevice = "Select Device";
    public static String SelectWIFI = "Select Wi-Fi";
    public static String SelectTimeZone = "Select Time Zone";
    public static String device_type_Switch[] = {"A", "A1", "A2", "A4", "A8"};
    public static String device_type_WaterLevelController[] = {"B"};
    public static String device_type_AirQualityMonitor[] = {"C1", "C2", "C3", "C4"};
    public static String device_type_SecurityMonitor[] = {"D1", "D2", "D3", "D4", "D5"};

    public static String device_with_Flood_Sensor[] = {"D1", "D2", "D5"};
    public static String device_with_Fire_Sensor[] = {"D2", "D3"};


    static String Domain_Arduino_Live = "http://www.ohmax.in/";
    static String Domain_Arduino_Dev = "http://www.ohmax.in";

    public static String Domain_Arduino = Domain_Arduino_Dev;
    static String Domain_Arduino_Staging = "http://ohmax.in/";
    private static String dtFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private ProgressDialog progressDialog;
    private Context myContext;


    public UTIL(Context context) {
        myContext = context;
    }

    public static void setPref(Context context, String key, String value) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPref(Context context, String key) {
    String val="";

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            val= preferences.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public static boolean clearPref(Context context) {
        boolean result = false;
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            result = preferences.edit().clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    public static boolean isConnectedToNodeMcu(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        String s = info[i].getExtraInfo();

                        if (s.startsWith("\"" + UTIL.DeviceNamePrefix)) {
                            return true;
                        }

                    }

        }
        return false;
    }

    public static boolean isConnectedToMyWifi(Context context, String Wifi_SSID) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        String s = info[i].getExtraInfo();

                        if (s.equals("\"" + Wifi_SSID + "\"")) {
                            return true;
                        }

                    }
        }
        return false;
    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static Boolean isMobileDataEnabled(Context context) {
// 1st code works on huwai and not on samsung
// 2nd work on samsung but  not on huwai
        /*try {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod) {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);

                return mobileDataEnabled;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error getting mobile data state", ex);
        }

        return false;
*/

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        String mobilereason = mobileInfo.getReason();
        String mobilestate = mobileInfo.getState().toString();

        return mobileConnected;

    }

    public static boolean compareDates(String startDate, String endDate) {
        boolean isAfter = false;
        try {
            // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat(dtFormat, Locale.US);
            Date strStartDate = sdf.parse(startDate);
            Date strEndDate = sdf.parse(endDate);

            if (strStartDate.after(strEndDate)) {
                // catalog_outdated = 1;
                isAfter = true;
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return isAfter;
    }

    public static void notification(Context context, String notificationType, String Title, String msg) {


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "M_CH_ID");

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Hearty365")
                .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle(Title)
                .setContentText(msg)
                .setContentInfo("Info");


        //You can add sound through the builder, i.e. a sound from the RingtoneManager:
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notification_Id = random_number();

        try {
            notificationManager.notify(notification_Id, notificationBuilder.build());
        } catch (Exception e) {
            e.getMessage();
        }


    }

    public static int random_number() {
        Random r = new Random();
        int i1 = r.nextInt(50 - 10) + 10;
        return i1;
    }
    public static int random_number_notificationId() {
        Random r = new Random();
        int i1 = r.nextInt(500000 - 10) + 10;
        return i1;
    }

    public static boolean isWIFIConnected(Context context) {
        boolean isWifi = false;
        boolean notNodeMcu = true;
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = connManager.getActiveNetworkInfo();
        isWifi = current != null && current.getType() == ConnectivityManager.TYPE_WIFI;
        // return isWifi;

        String s = current.getExtraInfo();
        if (s.startsWith("\"" + UTIL.DeviceNamePrefix)) {
            notNodeMcu = false;
        }

        return (isWifi && notNodeMcu);

    }

    public static String getDeviceType(String Device_Type) {
        String deviceType = "";

        ArrayList<String> arrayList_device_type_Switch =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
        ArrayList<String> arrayList_device_type_WaterLevelController =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
        ArrayList<String> arrayList_device_type_AirQualityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));
        ArrayList<String> arrayList_device_type_SecurityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));

        if (arrayList_device_type_Switch.contains(Device_Type)) {
            deviceType = "A";

        } else if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {
            deviceType = "B";

        } else if (arrayList_device_type_AirQualityMonitor.contains(Device_Type)) {
            deviceType = "C";

        } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {
            deviceType = "D";

        }

        return deviceType;


    }

    public static boolean is_Device_With_FIRE_Sensor(String DeviceType) {
        return Arrays.asList(device_with_Fire_Sensor).contains(DeviceType);
    }

    public static boolean is_Device_With_FLOOD_Sensor(String DeviceType) {
        return Arrays.asList(device_with_Flood_Sensor).contains(DeviceType);
    }

    public void showProgressDialog(String message) {
        if (progressDialog == null || (!progressDialog.isShowing())) {
            progressDialog = ProgressDialog.show(myContext, null, null, true);
            progressDialog.setContentView(R.layout.elemento_progress);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            final TextView tv = progressDialog.getWindow().findViewById(R.id.textView6);
            tv.setText(message);
            progressDialog.setCancelable(false);
            if (!progressDialog.isShowing())
                progressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

    }

    public String getCurrentTime(String strTimeZone) {


        String gmt = strTimeZone.substring(strTimeZone.indexOf("T") + 1);
        String op = "";
        String diff = "";
        String hrs = "";
        String mnts = "";

        if (gmt.charAt(0) == '+') {
            op = "+";
            diff = gmt.substring(gmt.indexOf("+") + 1);
        } else if (gmt.charAt(0) == '-') {
            op = "-";
            diff = gmt.substring(gmt.indexOf("-") + 1);
        } else {
            op = "+";
            diff = gmt;
        }
        hrs = diff.substring(0, diff.indexOf(":"));
        mnts = diff.substring(diff.indexOf(":") + 1);


        String utcTime = GetUTCdatetimeAsString();
        SimpleDateFormat format = new SimpleDateFormat(dtFormat);
        int hr = 0, mnt = 0;
        try {
            hr = Integer.parseInt(hrs);
            mnt = Integer.parseInt(mnts);
        } catch (Exception e) {
            e.getCause();
        }
        Calendar calendar = Calendar.getInstance();
        if (op.equals("+")) {
            try {
                Date date = format.parse(utcTime);
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, hr);
                calendar.add(Calendar.MINUTE, mnt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Date date = format.parse(utcTime);
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, -hr);
                calendar.add(Calendar.MINUTE, -mnt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        String d = format.format(calendar.getTime());


      /*  Date date = new Date();
        DateFormat df = new SimpleDateFormat(dtFormat);
        df.setTimeZone(TimeZone.getTimeZone(str_TimeZoneId));
        String formattedDate = df.format(date);
        return formattedDate;*/
        return d;
    }

    public String GetUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(dtFormat);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public Date StringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);

        try {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public long timeDiffereceInSeconds(String startDate, String endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dtFormat);
        long elapsedSeconds = 0;
        long TotalelapsedSeconds = 0;
        try {
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);

            long different = date1.getTime() - date2.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;


            TotalelapsedSeconds = different / secondsInMilli;


            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            elapsedSeconds = different / secondsInMilli;


        } catch (Exception e) {
            TotalelapsedSeconds = UTIL.Error_val;
            e.getMessage();
        }
        return TotalelapsedSeconds;
    }


}
