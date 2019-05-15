package com.pombingsoft.planet_iot.activity.water_level_controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.switch_controller.Add_Update_Schedule_Device_A_Activity;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

public class AddUpdateSchedule_Device_B_D_Activity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    Button btn_Run_Schedule, btn_Save_Schedule, btn_Delete_Schedule;

    EditText et_Time, et_Date, et_End_Date, et_End_Time;
    AlertDialog alertDialog;

    String UserId, DeviceId,  DateTime,SchedulerNum,Device_Type;
    String time_1, date_1;
    Context myContext;
    UTIL utill;
    boolean start_date_clicked, start_time_clicked;

    Spinner spinner_freq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_schedule__device_b_);
        myContext = AddUpdateSchedule_Device_B_D_Activity.this;
        utill = new UTIL(myContext);

        btn_Run_Schedule = findViewById(R.id.btn_Run_Schedule);
        btn_Save_Schedule = findViewById(R.id.btn_Save_Schedule);
        btn_Delete_Schedule = findViewById(R.id.btn_Delete_Schedule);

        et_Time = findViewById(R.id.et_Time);
        et_Date = findViewById(R.id.et_Date);
        et_End_Date = findViewById(R.id.et_End_Date);
        et_End_Time = findViewById(R.id.et_End_Time);

        try {
            spinner_freq = findViewById(R.id.spinner_freq);
            String[] freq = {"Daily", "Weekly"};
            ArrayAdapter aa = new ArrayAdapter(AddUpdateSchedule_Device_B_D_Activity.this, android.R.layout.simple_spinner_item, freq);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_freq.setAdapter(aa);
        }
        catch (Exception e){
            e.getCause();
        }


        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            DateTime = i.getStringExtra("DateTime");
            Device_Type = i.getStringExtra("Device_Type");

            SchedulerNum = i.getStringExtra("SchedulerNum");

           /* if (Scheduler_SavedAt.equals(UTIL.Server)) {
                btn_Run_Schedule.setVisibility(View.VISIBLE);
                btn_Save_Schedule.setVisibility(View.GONE);
            } else {
           */

            btn_Run_Schedule.setVisibility(View.VISIBLE);
            btn_Save_Schedule.setVisibility(View.GONE);
            btn_Delete_Schedule.setVisibility(View.VISIBLE);

            //   }

            if (DateTime == null || DateTime.equals("null") || DateTime.equals("0") || DateTime.equals("-1") || DateTime.trim().equals("")) {
                et_Time.setText("");
                et_Date.setText("");

                et_End_Date.setText("");
                et_End_Time.setText("");

                btn_Delete_Schedule.setVisibility(View.GONE);

            } else {

                btn_Delete_Schedule.setVisibility(View.VISIBLE);

                try {
                    String date_time = DateTime;
                    String[] dt = date_time.split(",");
                    String s = dt[0];
                    String[] ss = s.split("_");
                    String start_date = ss[0];
                    String start_time = ss[1];

                    et_Date.setText(start_date);
                    et_Time.setText(start_time);

                    String s1 = dt[1];
                    String[] ss1 = s1.split("_");
                    String end_date = ss1[0];
                    String end_time = ss1[1];

                    et_End_Date.setText(end_date);
                    et_End_Time.setText(end_time);


                 /*   if (dt[2].equalsIgnoreCase("o")) {

                        //  freq = "Once";
                        spinner_freq.setSelection(0);
                    } else*/ if (dt[2].equalsIgnoreCase("d")) {

                        //  freq = "Daily";
                        spinner_freq.setSelection(0);
                    } else if (dt[2].equalsIgnoreCase("w")) {

                        //  freq = "Weekly";
                        spinner_freq.setSelection(1);
                    } /*else if (dt[2].equalsIgnoreCase("m")) {

                        // freq = "Monthly";
                        spinner_freq.setSelection(3);
                    }*/

                } catch (Exception e) {
                    e.getMessage();
                }


            }

        } catch (Exception e) {
            e.getMessage();
        }


        et_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start_time_clicked = true;

                Click_getTime();
            }
        });
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_date_clicked = true;

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

        btn_Run_Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String date = et_Date.getText().toString();
                String time = et_Time.getText().toString();

                String End_date = et_End_Date.getText().toString();
                String End_time = et_End_Time.getText().toString();

                String freq = spinner_freq.getSelectedItem().toString().trim();

                freq = String.valueOf(freq.charAt(0));



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
                }


                else {
                    //2018-01-19_20:18
                    String startDate=et_Date.getText().toString().trim()+"T"+et_Time.getText().toString().trim()+":00";
                    String endDate=et_End_Date.getText().toString().trim()+"T"+et_End_Time.getText().toString().trim()+":00";

                    boolean isAfter=   UTIL.compareDates(startDate,endDate);
                    if(isAfter){
                        Toast.makeText(myContext,
                                "Start date cannot be greater than end date!", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }



                    //2018-01-19_20:18,2018-02-21_08:10
                    String dt = et_Date.getText().toString().trim() + "_" + et_Time.getText().toString().trim() + "," +
                            et_End_Date.getText().toString().trim() + "_" + et_End_Time.getText().toString().trim() + "," + freq;




                    //CallAPI_SetTimer(s);
                     SetSchedular( SchedulerNum,DeviceId, dt);


                }


            }
        });


        btn_Delete_Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_confirmation();
            }
        });


    }
    private void dialog_confirmation() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Do you want to delete schedule?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
               // if (Scheduler_SavedAt.equals(UTIL.Server)) {
                    deleteRunningScheduler();
              /*  }else{
                    deleteSavedScheduler();
                }
*/


            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private void Click_getDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddUpdateSchedule_Device_B_D_Activity.this,
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
        dpd.setMinDate(calendar);

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
                AddUpdateSchedule_Device_B_D_Activity.this,
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = +dayOfMonth + "/" + (++monthOfYear) + "/" + year;

        String dayOfMonthString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String monthOfYearString = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;

        //2018-01-19%20

        date_1 = year + "-" + monthOfYearString + "-" + dayOfMonthString;


         //   et_Date.setText(date_1);

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


           // et_Time.setText(time_1);


        if (start_time_clicked) {
            et_Time.setText(time_1);
        } else {
            et_End_Time.setText(time_1);
        }
    }


    private void SetSchedular(String SchedulerNumber,
                              String DeviceId,
                              String SchedulerTime) {

        ArrayList<String> arrayList_device_type_Switch =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_Switch));
        ArrayList<String> arrayList_device_type_WaterLevelController =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_WaterLevelController));
        ArrayList<String> arrayList_device_type_AirQualityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_AirQualityMonitor));
        ArrayList<String> arrayList_device_type_SecurityMonitor =
                new ArrayList<String>(Arrays.asList(UTIL.device_type_SecurityMonitor));
        if (new ConnectionDetector(myContext).isConnectingToInternet()) {
            if (arrayList_device_type_WaterLevelController.contains(Device_Type)) {
                callApi_AddUpdateSchedulerB( SchedulerNumber,
                         DeviceId,
                         SchedulerTime);

            } else if (arrayList_device_type_SecurityMonitor.contains(Device_Type)) {
                callApi_AddUpdateSchedulerD( SchedulerNumber,
                        DeviceId,
                        SchedulerTime);
            }


        }
        else{
            Toast.makeText(myContext,
                    UTIL.NoInternet, Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private void deleteRunningScheduler(){
        SetSchedular(SchedulerNum, DeviceId, "-1");
    }

    void callApi_AddUpdateSchedulerB(String SchedulerNumber,
                                     String DeviceId,
                                     String SchedulerTime){


        String tag_string_req = "req_login";
        utill.showProgressDialog("Kindly wait...");
        String URL = null;


        URL = UTIL.Domain_Arduino + UTIL.AddSchedulerB_API+"DeviceId="+DeviceId+
                "&number="+SchedulerNumber+"&time="+SchedulerTime;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server
                        Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        utill.hideProgressDialog();
                        Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    utill.hideProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(myContext,
                        "Volley Error!", Toast.LENGTH_SHORT).show();
                utill.hideProgressDialog();

            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);









    }
    void callApi_AddUpdateSchedulerD(String SchedulerNumber,
                                     String DeviceId,
                                     String SchedulerTime){


        String tag_string_req = "req_login";
        utill.showProgressDialog("Kindly wait...");
        String URL = null;


        URL = UTIL.Domain_Arduino + UTIL.AddSchedulerD_API+"DeviceId="+DeviceId+
                "&number="+SchedulerNumber+"&time="+SchedulerTime;
        URL = URL.replaceAll(Pattern.quote(" "), "%20");//nks


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    if (status.equals("1")) {//request updated at server
                        Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        utill.hideProgressDialog();
                        Toast.makeText(myContext, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    utill.hideProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(myContext, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(myContext,
                        "Volley Error!", Toast.LENGTH_SHORT).show();
                utill.hideProgressDialog();

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
