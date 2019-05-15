package com.pombingsoft.planet_iot.activity.fragment_new;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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


public class FAQ_Fragment extends Fragment {

    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    Context myContext;
    EditText et_mobile, et_password;
    UTIL utill;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_faq_, container, false);

        myContext = getActivity();
        utill = new UTIL(myContext);
        expListView = (ExpandableListView)rootView. findViewById(R.id.lvExp);

        getActivity().setTitle("FAQ");
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    expListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });



        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                // TODO Auto-generated method stub




                return false;
            }
        });

        return rootView;
    }






   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        myContext = FAQ_Fragment.this;
        utill = new UTIL(myContext);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();


        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return false;
            }
        });


        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });


        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    expListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });



        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                // TODO Auto-generated method stub




                return false;
            }
        });
    }*/

    @Override
    public void onResume() {
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

        listDataChild.clear();
        listDataHeader.clear();
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


                            ArrayList<String> list=new ArrayList<>();
                            list.add(Description);
                            listDataChild.put(Title,list);
                            listDataHeader.add(Title);
                        }


                    } else {

                        Toast.makeText(myContext,
                                "No devices found!", Toast.LENGTH_SHORT).show();
                    }

                    ExpandableListAdapter listAdapter = new ExpandableListAdapter(myContext, listDataHeader, listDataChild);

                    expListView.setAdapter(listAdapter);

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




            }
        }) {


        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Where do I start?");
        listDataHeader.add("What is meant by Live NIT?");
        listDataHeader.add("How are Corrigendum displayed?");
        listDataHeader.add("Is Member Registration mandatory? What are the advantages of registering on the site?");
        listDataHeader.add("How do I reach NTPC offices across the country?");
        listDataHeader.add("What is the format of the date mentioned on the site?");
        listDataHeader.add("What is the OS version required to run the application?");
        listDataHeader.add("Contact for any issue?");
        listDataHeader.add("Version Name of the app?");


        List<String> ans1 = new ArrayList<String>();
        ans1.add("Start by entering an NIT Number and/or any Text included in the Scope of Work, in the Search Box. You can narrow your search by selecting when the NIT was issued from the ‘Posted in Last’ box.");


        List<String> ans2 = new ArrayList<String>();
        ans2.add("Live NITs are those NITs for which the Tender Document Sale Close Date has not expired. NITs for which the Tender Document Sale Close Date is within the last 7 days are also included in this category.");


        List<String> ans3 = new ArrayList<String>();
        ans3.add("Any NIT with a corrigendum will be indicated with a \"Corrigendum\" marker NIT with Corrigendum adjacent to it. The details of a Corrigendum are displayed just above the original NIT Text, which can be viewed by clicking on the \"View NIT Details\" icon View NIT Details.");

        List<String> ans4 = new ArrayList<String>();
        ans4.add("Member Registration is not mandatory for general access.\n" +
                "\n" +
                "However, it you choose to register, you can mention your preference(s) of Contract Classifications and/or Sources of NITs. Registered users also have the option of subscribing to notification, each time a new NIT in their chosen Contract Classifications is published on the site, through an automated email. Upon logging in, when you click on ‘My NITs’ only your preferred NIT(s) will be displayed.\n" +
                "\n" +
                "PDF File  Read more about user registration.\n" +
                "\n" +
                "Other advantages for registered members are listed in the Services section.");

        List<String> ans5 = new ArrayList<String>();
        ans5.add("Online with.\n" +
                "www.ntpctender.com");

        List<String> ans6 = new ArrayList<String>();
        ans6.add("The date across the site is mentioned in the DD/MM/YYYY format, except at places mentioned otherwise.");
        List<String> ans7 = new ArrayList<String>();
        ans7.add("Currently the App is supported Android handset with 4.0.3(Ice Cream Sandwich) & above versions.");

        List<String> ans8 = new ArrayList<String>();
        ans8.add("iterpmobapps@ntpc.co.in");

        List<String> ans9 = new ArrayList<String>();
        ans9.add("Version 1.0");

        listDataChild.put(listDataHeader.get(0), ans1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), ans2);
        listDataChild.put(listDataHeader.get(2), ans3);
        listDataChild.put(listDataHeader.get(3), ans4);
        listDataChild.put(listDataHeader.get(4), ans5);
        listDataChild.put(listDataHeader.get(5), ans6);
        listDataChild.put(listDataHeader.get(6), ans7);
        listDataChild.put(listDataHeader.get(7), ans8);
       listDataChild.put(listDataHeader.get(8), ans9);



    }
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader;
        private HashMap<String, List<String>> _listDataChild;
        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.faq_question_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);
            txtListChild.setText(childText);




            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.faq_ans_item, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);
            if (getChildrenCount(groupPosition) == 0)
                ((ImageView) convertView.findViewById(R.id.arrow)).setVisibility(View.INVISIBLE);
            if (isExpanded) {
                ((ImageView) convertView.findViewById(R.id.arrow)).setImageDrawable(_context.getResources().getDrawable(R.drawable.down_arrow_white));
            } else {
                ((ImageView) convertView.findViewById(R.id.arrow)).setImageDrawable(_context.getResources().getDrawable(R.drawable.right_arrow_white));
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

}
