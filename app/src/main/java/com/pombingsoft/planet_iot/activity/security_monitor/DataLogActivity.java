package com.pombingsoft.planet_iot.activity.security_monitor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataLogActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    ListView listview_devices;
    Context myContext;
    UTIL utill;
    TextView tv_msg;
    String time_1, date_1;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> list_dataLog = new ArrayList<>();
    //  LinearLayout ll_AddDevice;
    AlertDialog alertDialog;
    boolean start_date_clicked, start_time_clicked;
    String sDate, endDate;
    String DeviceName, DeviceId;
    EditText et_Time, et_Date, et_End_Date, et_End_Time;
    Button btn_getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log_main);
        myContext = DataLogActivity.this;
        utill = new UTIL(myContext);
        listview_devices = findViewById(R.id.device_listview);
        tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);


        btn_getData = findViewById(R.id.btn_getData);

        et_Time = findViewById(R.id.et_Time);
        et_Date = findViewById(R.id.et_Date);
        et_End_Date = findViewById(R.id.et_End_Date);
        et_End_Time = findViewById(R.id.et_End_Time);

        try {
            Intent i = getIntent();
            DeviceName = i.getStringExtra("DeviceName");
            DeviceId = i.getStringExtra("DeviceId");


        } catch (Exception e) {
            e.getMessage();
        }
        et_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start_time_clicked = true;
                //end_time_clicked=false;

                Click_getTime();
            }
        });
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_date_clicked = true;
                // end_date_clicked=false;

                Click_getDate();
            }
        });
        et_End_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_date_clicked = false;
                Click_getDate();
                // end_date_clicked=true;
            }
        });

        et_End_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_time_clicked = false;
                Click_getTime();
                //  end_time_clicked=true;
            }
        });


        btn_getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = et_Date.getText().toString();
                String time = et_Time.getText().toString();

                String End_date = et_End_Date.getText().toString();
                String End_time = et_End_Time.getText().toString();


                if (date.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter start date!", Toast.LENGTH_SHORT)
                            .show();
                } else if (time.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter start time!", Toast.LENGTH_SHORT)
                            .show();
                } else if (End_date.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter end date!", Toast.LENGTH_SHORT)
                            .show();
                } else if (End_time.isEmpty()) {
                    Toast.makeText(myContext,
                            "Please enter end time!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                   /* String startDate=et_Date.getText().toString().trim()+"T"+et_Time.getText().toString().trim()+":00";
                    String endDate=et_End_Date.getText().toString().trim()+"T"+et_End_Time.getText().toString().trim()+":00";
*/

                    sDate = et_Date.getText().toString().trim() + "T" + et_Time.getText().toString().trim();
                    endDate = et_End_Date.getText().toString().trim() + "T" + et_End_Time.getText().toString().trim();


                    boolean isAfter = UTIL.compareDates(sDate, endDate);

                    if (isAfter) {
                        Toast.makeText(myContext,
                                "Start date cannot be greater than end date!", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }


                   /* String dt = et_Date.getText().toString().trim() + "_" + et_Time.getText().toString().trim() + "," +
                            et_End_Date.getText().toString().trim() + "_" + et_End_Time.getText().toString().trim() + "," + freq;

*/


                    if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                        getDataLog_ApiCall();
                    } else {

                        DialogInternet();
                    }

                }


            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if (list_dataLog.size() == 0) {
                    Toast.makeText(myContext, "No data to download.\nClick GET DATA button", Toast.LENGTH_SHORT).show();
                } else {


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());

                    String data = String.valueOf(list_dataLog);

                    data = data.replaceAll("[{}]", "");
                    data = data.replaceAll(",", "\n");
                    data = data.substring(1, data.length() - 1);

                    generateNoteOnSD(myContext, "Log_" + currentDateandTime + ".txt", data);

                }

            }
        });

    }


    void getDataLog_ApiCall() {

        list.clear();
        list_dataLog.clear();
        String tag_string_req = "req_login";

      /*  final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting devices...");
        if (!pDialog.isShowing())
            pDialog.show();*/

          utill.showProgressDialog("Fetching data...");

        String URL_LOGIN = null;

        /*
         * http://localhost:63020/api/arduino/
         * GetDeviceDDataLog?deviceId=kmbpHDekB9&sdate=2018-04-18%2011:19:30&edate=2018-04-18%2012:24:03

         * */

        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.getDeviceDataLog_API + "deviceId=" + DeviceId + "&sdate=" + sDate + "&edate=" + endDate;

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                utill.hideProgressDialog();

                //  utill.hideProgressDialog();
                //1. //[]

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //  String DeviceId = jsonObject.getString("DeviceId");
                            String Temperature = jsonObject.getString("Temperature");
                            String Humidity = jsonObject.getString("Humidity");

                            String Secuirity_Breach = jsonObject.getString("Secuirity_Breach");
                            String Flood_Level = jsonObject.getString("Flood_Level");
                            String Fire_Alarm = jsonObject.getString("Fire_Alarm");
                            String NDateTime = jsonObject.getString("NDateTime");


                            if (Temperature.equals("null") || Temperature.equals(null)) {// || Temperature == null || Temperature.equals(null)) {
                                Temperature = "N/A";
                            }
                            if (Humidity.equals("null") || Humidity.equals(null)) {// || Humidity == null || Humidity.equals(null)) {
                                Humidity = "N/A";
                            }

                            NDateTime = NDateTime.replace("T", " ");
                            NDateTime = NDateTime.substring(0, NDateTime.indexOf("."));


                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("Temperature", Temperature);
                            hashMap.put("Humidity", Humidity);
                            hashMap.put("Secuirity_Breach", Secuirity_Breach);
                            hashMap.put("Flood_Level", Flood_Level);
                            hashMap.put("Fire_Alarm", Fire_Alarm);
                            hashMap.put("NDateTime", NDateTime);


                            list.add(hashMap);


                            //Data Log


                            HashMap<String, String> hashMap_2 = new HashMap<>();
                        /*    hashMap_2.put("Id", String.valueOf(i+1));
                            hashMap_2.put("Temperature", Temperature);
                            hashMap_2.put("Humidity", Humidity);
                            hashMap_2.put("Date_Time", NDateTime+"\n");

*/


                            hashMap_2.put("Temperature", Temperature);

                            hashMap_2.put("Humidity", Humidity);

                            hashMap_2.put("Date_Time", NDateTime);

                            hashMap_2.put("Id", String.valueOf(i + 1) + "\n");


                            list_dataLog.add(hashMap_2);


                        }

                        if (list.size() < 1) {
                            tv_msg.setVisibility(View.VISIBLE);
                        } else {
                            tv_msg.setVisibility(View.GONE);
                        }


                    } else {
                        tv_msg.setVisibility(View.VISIBLE);

                        Toast.makeText(myContext,
                                "No data found!", Toast.LENGTH_SHORT).show();
                    }

                    device_list_adapter adapter = new device_list_adapter(myContext, list);
                    listview_devices.setAdapter(adapter);

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
                Toast.makeText(myContext,
                        "Volley Error!", Toast.LENGTH_SHORT).show();


            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = +dayOfMonth + "/" + (++monthOfYear) + "/" + year;

        String dayOfMonthString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String monthOfYearString = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;

        //2018-01-19%20

        date_1 = year + "-" + monthOfYearString + "-" + dayOfMonthString;

        if (start_date_clicked) {
            et_Date.setText(date_1);
        } else {
            et_End_Date.setText(date_1);
        }


    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        String t = hourString + ":" + minuteString;
        //19:11:09.983
        //  et_Time.setText(t);
        time_1 = hourString + ":" + minuteString;//+ ":" + secondString + "." + "000";

        if (start_time_clicked) {
            et_Time.setText(time_1);
        } else {
            et_End_Time.setText(time_1);
        }

    }

    private void Click_getDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DataLogActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dpd.setThemeDark(false);
        dpd.vibrate(false);
        dpd.dismissOnPause(false);
        dpd.showYearPickerFirst(false);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        // dpd.setAccentColor(Color.parseColor("#9C27B0"));
        dpd.setAccentColor(getResources().getColor(R.color.light_skyblue));


        dpd.setTitle("Select Date");
        dpd.setYearRange(1985, 2028);
        //   dpd.setMinDate(calendar);


        /*
        *  Calendar date1 = Calendar.getInstance();
                    Calendar date2 = Calendar.getInstance();
                    date2.add(Calendar.WEEK_OF_MONTH, -1);
                    Calendar date3 = Calendar.getInstance();
                    date3.add(Calendar.WEEK_OF_MONTH, 1);
                    Calendar[] days = {date1, date2, date3};
                    dpd.setHighlightedDays(days);
        * */
        /*
        *  Calendar[] days = new Calendar[13];
                    for (int i = -6; i < 7; i++) {
                        Calendar day = Calendar.getInstance();
                        day.add(Calendar.DAY_OF_MONTH, i * 2);
                        days[i + 6] = day;
                    }
                    dpd.setSelectableDays(days);
        *
        *
        * */
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    private void Click_getTime() {
        boolean is24HourMode = true;
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                DataLogActivity.this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        tpd.setThemeDark(false);
        tpd.vibrate(false);
        tpd.dismissOnPause(false);
        tpd.enableSeconds(false);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        //  tpd.setAccentColor(Color.parseColor("#9C27B0"));
        tpd.setAccentColor(getResources().getColor(R.color.light_skyblue));

        tpd.setTitle("Select Time");

/* if (limitSelectableTimes.isChecked()) {
                    if (enableSeconds.isChecked()) {
                        tpd.setTimeInterval(3, 5, 10);
                    } else {
                        tpd.setTimeInterval(3, 5, 60);
                    }
                }
                if (disableSpecificTimes.isChecked()) {
                    Timepoint[] disabledTimes = {
                            new Timepoint(10),
                            new Timepoint(10, 30),
                            new Timepoint(11),
                            new Timepoint(12, 30)
                    };
                    tpd.setDisabledTimes(disabledTimes);
                }*/

        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");


    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {

    /*    final ProgressDialog pDialog = new ProgressDialog(myContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Downloading...");
        if (!pDialog.isShowing())
            pDialog.show();*/

        utill.showProgressDialog("Downloading...");


        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Data Log");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);

            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

           utill.hideProgressDialog();
           /* Toast.makeText(myContext, "File downloaded successfully !\n" +

                    "" + "Location:" + gpxfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
              */

            dialog_fileDownloaded("Location:" + gpxfile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
           utill.hideProgressDialog();
            Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show();
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
            String Temperature = beanArrayList.get(i).get("Temperature");
            String Humidity = beanArrayList.get(i).get("Humidity");
            final String Secuirity_Breach = beanArrayList.get(i).get("Secuirity_Breach");
            final String Flood_Level = beanArrayList.get(i).get("Flood_Level");
            final String Fire_Alarm = beanArrayList.get(i).get("Fire_Alarm");
            String NDateTime = beanArrayList.get(i).get("NDateTime");


            if (convertview == null) {
                holder = new device_list_adapter.Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_data_log, null);


                holder.index_no = (Button) convertview.findViewById(R.id.serial_no);

                holder.Date = (TextView) convertview.findViewById(R.id.Date);
                holder.temp = (TextView) convertview.findViewById(R.id.temp);
                holder.humidity = (TextView) convertview.findViewById(R.id.humidity);


                convertview.setTag(holder);
            } else {
                holder = (device_list_adapter.Holder) convertview.getTag();
            }


          /*  NDateTime = NDateTime.replace("T", " ");
            NDateTime = NDateTime.substring(0, NDateTime.indexOf("."));

            if (Temperature.equals("null")) {// || Temperature == null || Temperature.equals(null)) {
                Temperature = "N/A";
            }
            if (Humidity.equals("null")) {// || Humidity == null || Humidity.equals(null)) {
                Humidity = "N/A";
            }*/

            holder.index_no.setText(index1);
            holder.Date.setText(NDateTime);
            holder.temp.setText(Temperature);
            holder.humidity.setText(Humidity);


            return convertview;
        }


        class Holder {


            TextView Date, temp, humidity;
            LinearLayout row_jobFile;
            Button index_no;
            //nks


        }


    }
    private void dialog_fileDownloaded(String path) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("File downloaded successfully!");
        message.setText(path);
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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
