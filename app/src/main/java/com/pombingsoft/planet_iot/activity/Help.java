package com.pombingsoft.planet_iot.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.ConnectionDetector;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Help extends AppCompatActivity {
    Context myContext;
    EditText et_mobile, et_password;
    UTIL utill;
    ArrayList<HashMap<String, String>> list=new ArrayList<>();
    ListView help_listview;
    TextView tv_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        myContext = Help.this;
        utill = new UTIL(myContext);


        help_listview = findViewById(R.id.help_listview);
        tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

    }



    @Override
    protected void onResume() {
        super.onResume();



            if (new ConnectionDetector(myContext).isConnectingToInternet()) {
                getHelp_ApiCall();
            } else {

                Toast.makeText(myContext,
                        UTIL.NoInternet, Toast.LENGTH_SHORT)
                        .show();
            }


    }

    void getHelp_ApiCall() {

        list.clear();
        String tag_string_req = "req_login";

        utill.showProgressDialog("");
        String URL_LOGIN = null;


        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.GetHelp_API ;

        } catch (Exception e) {
            e.printStackTrace();
        }


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

                            String Id = jsonObject.getString("Id");
                            String Title = jsonObject.getString("Title");
                            String Description = jsonObject.getString("Description");

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("Id", Id);
                            hashMap.put("Title", Title);
                            hashMap.put("Description", Description);

                            list.add(hashMap);
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

                    help_list_adapter adapter = new help_list_adapter(myContext, list);
                    help_listview.setAdapter(adapter);

                } catch (JSONException e) {
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

                help_listview.setAdapter(null);

                tv_msg.setVisibility(View.VISIBLE);
            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public class help_list_adapter extends BaseAdapter {
        List<HashMap<String, String>> beanArrayList;
        Context context;
        int count = 1;

        public help_list_adapter(Context context, List<HashMap<String, String>> beanArrayList) {
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

            final String Id = beanArrayList.get(i).get("Id");
            final String Title = beanArrayList.get(i).get("Title");
            final String Description = beanArrayList.get(i).get("Description");



            if (convertview == null) {
                holder = new Holder();

                convertview = LayoutInflater.from(context).inflate(R.layout.row_help, null);


                holder.Ques_no = (TextView) convertview.findViewById(R.id.Ques_no);
                holder.Ques = (TextView) convertview.findViewById(R.id.Ques);
                holder.Ans_no = (TextView) convertview.findViewById(R.id.Ans_no);
                holder.Ans = (TextView) convertview.findViewById(R.id.Ans);

                convertview.setTag(holder);
            } else {
                holder = (Holder) convertview.getTag();
            }


            holder.Ques_no.setText("Q. "+Id+" :");
            holder.Ques.setText(Title);
            holder.Ans_no.setText( "Ans. :");
            holder.Ans.setText(Description);


            return convertview;
        }


        class Holder {
            TextView Ques_no, Ques, Ans_no,Ans;



        }


    }

}
