package com.pombingsoft.planet_iot.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.switch_controller.Add_Update_Schedule_Device_A_Activity;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedScheduler_Fragment extends Fragment {
    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus, DimmerType, PinNum;
    ArrayList<HashMap<String, String>> list;
    ListView scheduler_listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_scheduler, container, false);
        myContext = getActivity();
        utill = new UTIL(myContext);

        try {
            Bundle i = getArguments();
            UserId = i.getString("UserId");
            DeviceId = i.getString("DeviceId");
            DeviceLockStatus = i.getString("LockStatus");
            Device_Type = i.getString("DeviceType");
            DeviceName = i.getString("DeviceName");
            PinNum = i.getString("PinNum");

        } catch (Exception e) {
            e.getMessage();
        }
        scheduler_listview = rootView.findViewById(R.id.scheduler_listview);


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getSavedScheduler();

    }


    private void getSavedScheduler() {
        list = new ArrayList<>();
        //Now check if it exists in local shared preference
        String schedule = UTIL.getPref(myContext, UTIL.Key_Schedule);
        if (!schedule.equals("")) {
            try {
                JSONArray jsonArray_1 = new JSONArray(schedule);
                if (jsonArray_1.length() > 0) {


                    String pin_number = PinNum.substring(4);//1

                    // search scheduler_1
                    for (int i = 0; i < jsonArray_1.length(); i++) {
                        JSONObject jsonObject_1 = jsonArray_1.getJSONObject(i);
                        String str_deviceId = jsonObject_1.getString(UTIL.Key_DeviceId);
                        String str_pin_num = jsonObject_1.getString(UTIL.Key_Pin_Num);
                        String str_scheduler_num = jsonObject_1.getString(UTIL.Key_Scheduler_Num);
                        String str_scheduler_time = jsonObject_1.getString(UTIL.Key_Scheduler_Time);

                        if (str_deviceId.equals(DeviceId) && str_pin_num.equals(pin_number) && str_scheduler_num.equals("1")) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", str_scheduler_time);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
                            list.add(hashMap1);
                            break;
                        } else if (i == jsonArray_1.length()-1) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", "");
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
                            list.add(hashMap1);
                        }
                    }

                    // search scheduler_2
                    for (int i = 0; i < jsonArray_1.length(); i++) {
                        JSONObject jsonObject_1 = jsonArray_1.getJSONObject(i);
                        String str_deviceId = jsonObject_1.getString(UTIL.Key_DeviceId);
                        String str_pin_num = jsonObject_1.getString(UTIL.Key_Pin_Num);
                        String str_scheduler_num = jsonObject_1.getString(UTIL.Key_Scheduler_Num);
                        String str_scheduler_time = jsonObject_1.getString(UTIL.Key_Scheduler_Time);

                        if (str_deviceId.equals(DeviceId) && str_pin_num.equals(pin_number) && str_scheduler_num.equals("2")) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", str_scheduler_time);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
                            list.add(hashMap1);
                            break;
                        } else if (i == jsonArray_1.length()-1) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", "");
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
                            list.add(hashMap1);
                        }
                    }

                    // search scheduler_3
                    for (int i = 0; i < jsonArray_1.length(); i++) {
                        JSONObject jsonObject_1 = jsonArray_1.getJSONObject(i);
                        String str_deviceId = jsonObject_1.getString(UTIL.Key_DeviceId);
                        String str_pin_num = jsonObject_1.getString(UTIL.Key_Pin_Num);
                        String str_scheduler_num = jsonObject_1.getString(UTIL.Key_Scheduler_Num);
                        String str_scheduler_time = jsonObject_1.getString(UTIL.Key_Scheduler_Time);

                        if (str_deviceId.equals(DeviceId) && str_pin_num.equals(pin_number) && str_scheduler_num.equals("3")) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", str_scheduler_time);
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
                            list.add(hashMap1);
                            break;
                        } else if (i == jsonArray_1.length()-1) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("DateTime", "");
                            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
                            list.add(hashMap1);
                        }
                    }


                } else {
                    // No local scheduler exists  for this deviceId and PinNum
                    addTempDataToList();
                }


            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            // No local scheduler exists
            addTempDataToList();
        }
        device_list_adapter adapter = new device_list_adapter(myContext, list);
        scheduler_listview.setAdapter(adapter);

    }

    private void addTempDataToList() {
        list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            HashMap<String, String> hashMap1 = new HashMap<>();
            hashMap1.put("DateTime", "");
            hashMap1.put(UTIL.Key_Scheduler_SavedAt, UTIL.Local);
            list.add(hashMap1);
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

            final Holder holder;
            final String index1 = String.valueOf(i + 1);
            final String DateTime = beanArrayList.get(i).get("DateTime");
            final String Scheduler_SavedAt = beanArrayList.get(i).get(UTIL.Key_Scheduler_SavedAt);


            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_scheduler, null);


                holder.textvw_scheduler_count = (TextView) convertview.findViewById(R.id.textvw_scheduler_count);

                holder.textvw_scheduler_startDate = (TextView) convertview.findViewById(R.id.textvw_scheduler_startDate);
                holder.textvw_scheduler_endDate = (TextView) convertview.findViewById(R.id.textvw_scheduler_endDate);
                holder.textvw_scheduler_freq = (TextView) convertview.findViewById(R.id.textvw_scheduler_freq);


                holder.img_add_Scheduler = convertview.findViewById(R.id.img_add_Scheduler);
                holder.img_update_Scheduler = convertview.findViewById(R.id.img_update_Scheduler);
                holder.img_delete_Scheduler = convertview.findViewById(R.id.img_delete_Scheduler);


                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }


            if (i == 0) {
                holder.textvw_scheduler_count.setText(UTIL.Text_Scheduler_1);

            } else if (i == 1) {
                holder.textvw_scheduler_count.setText(UTIL.Text_Scheduler_2);

            } else if (i == 2) {
                holder.textvw_scheduler_count.setText(UTIL.Text_Scheduler_3);

            }

            // if (false) {
            if (DateTime == null || DateTime.equals("null") || DateTime.equals("0") || DateTime.equals("-1") || DateTime.equals("")) {

                // holder.textvw_scheduler.setText(UTIL.Text_Scheduler_Not_Set);

                holder.textvw_scheduler_startDate.setText(UTIL.Text_Scheduler_Not_Set);
                holder.textvw_scheduler_endDate.setText(UTIL.Text_Scheduler_Not_Set);
                holder.textvw_scheduler_freq.setText(UTIL.Text_Scheduler_Not_Set);


                holder.img_add_Scheduler.setVisibility(View.VISIBLE);
                holder.img_update_Scheduler.setVisibility(View.GONE);
                holder.img_delete_Scheduler.setVisibility(View.GONE);
            } else {
                //holder.textvw_scheduler.setText(DateTime);

                String date_time = DateTime;
                date_time = date_time.replaceAll("_", " ");
                String[] dt = date_time.split(",");

                String freq = "";
                String startDate = "";
                String endDate = "";

                try {
                    startDate = dt[0];
                } catch (Exception e) {
                    e.getMessage();
                }
                try {
                    endDate = dt[1];
                } catch (Exception e) {
                    e.getMessage();
                }

                try {
                    if (dt[2].equalsIgnoreCase("o")) {
                        freq = "Once";
                    } else if (dt[2].equalsIgnoreCase("d")) {
                        freq = "Daily";
                    } else if (dt[2].equalsIgnoreCase("w")) {
                        freq = "Weekly";
                    } else if (dt[2].equalsIgnoreCase("m")) {
                        freq = "Monthly";
                    }
                } catch (Exception e) {
                    e.getMessage();
                }


                holder.textvw_scheduler_startDate.setText(startDate);
                holder.textvw_scheduler_endDate.setText(endDate);
                holder.textvw_scheduler_freq.setText(freq);


                holder.img_add_Scheduler.setVisibility(View.GONE);
                holder.img_update_Scheduler.setVisibility(View.VISIBLE);
                holder.img_delete_Scheduler.setVisibility(View.VISIBLE);
            }


            convertview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = (new Intent(myContext, Add_Update_Schedule_Device_A_Activity.class));
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("DeviceId", DeviceId);
                    intent.putExtra("PinNum", PinNum);
                    intent.putExtra("SchedulerNum", String.valueOf(i + 1));
                    intent.putExtra("DateTime", DateTime);
                    intent.putExtra(UTIL.Key_Scheduler_SavedAt, Scheduler_SavedAt);


                    startActivity(intent);
                }
            });


            return convertview;
        }


        class Holder {
            TextView textvw_scheduler_count, textvw_scheduler_startDate, textvw_scheduler_endDate, textvw_scheduler_freq;
            //nks
            ImageView img_add_Scheduler, img_update_Scheduler, img_delete_Scheduler;

        }


    }

}