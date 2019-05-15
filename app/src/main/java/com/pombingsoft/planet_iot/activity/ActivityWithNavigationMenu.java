package com.pombingsoft.planet_iot.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.fragment_new.AboutUs_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.AddDevice_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.AddExistingDevice_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.ContactUs_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.Device_List_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.FAQ_Fragment;
import com.pombingsoft.planet_iot.activity.fragment_new.MyProfileFragment;
import com.pombingsoft.planet_iot.activity.fragment_new.UpdateTimeZone_Fragment_New;
import com.pombingsoft.planet_iot.activity.fragment_new.UpdateWifiCredNew_Fragment;
import com.pombingsoft.planet_iot.util.AppController;
import com.pombingsoft.planet_iot.util.UTIL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityWithNavigationMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context myContext;
    UTIL utill;
    AlertDialog alertDialog;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        myContext = ActivityWithNavigationMenu.this;
        utill = new UTIL(myContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.nav_icn);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        manupulateDrawerItems();
        addDeviceListFragment();
        loadFragmentfromIntent();
        // UpdateFCMTokenAtServer();
        UpdateFCMTokenAtServer_New();


    }


    public void addDeviceListFragment() {
        Device_List_Fragment fragment = new Device_List_Fragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frgmnt_placehodler, fragment);
        fragmentTransaction.commit();


    }

    public void AddNewDeviceFragment() {
        AddDevice_Fragment fragment = new AddDevice_Fragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frgmnt_placehodler, fragment);
        fragmentTransaction.commit();


    }

    public void loadFragmentfromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String value1 = extras.getString(UTIL.Key_TimeZone);
        if (value1 != null) {
            UpdateTimeZone_Fragment_New fragment = new UpdateTimeZone_Fragment_New();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frgmnt_placehodler, fragment);
            fragmentTransaction.commit();
        }

        String value2 = extras.getString(UTIL.Key_Wifi);
        if (value2 != null) {
            UpdateWifiCredNew_Fragment fragment = new UpdateWifiCredNew_Fragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frgmnt_placehodler, fragment);
            fragmentTransaction.commit();

        }


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        /*
        Menu menu = navigationView.getMenu();
        menu.add(R.id.group_1, 123, Menu.NONE, "Title1");
        menu.add(R.id.group_1, 124, Menu.NONE, "Title2");
       */

        closeDrawer();
        int id = item.getItemId();

        // to not highlight when selected
        //  navigationView.getMenu().getItem(item).setChecked(false);

        if (id == R.id.nav_controlDevice) {

            replaceFragmnt(new Device_List_Fragment());
        } else if (id == R.id.nav_add_new_device) {
            //  startActivity(new Intent(ActivityWithNavigationMenu.this, AddDeviceActivity_New.class));

            replaceFragmnt(new AddDevice_Fragment());

        } else if (id == R.id.nav_add_existing_device) {
            //  startActivity(new Intent(ActivityWithNavigationMenu.this, AddExistingDeviceActivity.class));

            replaceFragmnt(new AddExistingDevice_Fragment());

        } else if (id == R.id.nav_logout) {
            dialog_LOGOUT();
        } else if (id == R.id.nav_ContactUs) {
            // startActivity(new Intent(ActivityWithNavigationMenu.this, ContactUs.class));
            replaceFragmnt(new ContactUs_Fragment());

        } else if (id == R.id.nav_AboutUs) {
            //  startActivity(new Intent(ActivityWithNavigationMenu.this, AboutUs.class));
            replaceFragmnt(new AboutUs_Fragment());

        } else if (id == R.id.nav_Help) {
            //startActivity(new Intent(HomeActivity.this, Help.class));
            // startActivity(new Intent(ActivityWithNavigationMenu.this, FAQ_Activity.class));

            replaceFragmnt(new FAQ_Fragment());


        } else if (id == R.id.nav_Profile) {
            //  startActivity(new Intent(ActivityWithNavigationMenu.this, MyProfileActivity.class));
            replaceFragmnt(new MyProfileFragment());

        } else if (id == R.id.nav_update_wifi) {
            // startActivity(new Intent(ActivityWithNavigationMenu.this, UpdateWifiCredActivity.class));

            replaceFragmnt(new UpdateWifiCredNew_Fragment());
        } else if (id == R.id.nav_update_timezone) {
            // startActivity(new Intent(ActivityWithNavigationMenu.this, UpdateWifiCredActivity.class));

            // replaceFragmnt(new UpdateTimeZone_Fragment());
            replaceFragmnt(new UpdateTimeZone_Fragment_New());
        }


        return false;
    }

    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void replaceFragmnt(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // fragmentTransaction.setCustomAnimations( R.anim.left_in, R.anim.left_out);
        fragmentTransaction.replace(R.id.frgmnt_placehodler, fragment);
        //fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
        //
        ActivityWithNavigationMenu.this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        //HomeActivity.this.overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );

    }


    public void manupulateDrawerItems() {

        /*set drawer menu programmatically*/

        ImageView imageView;
        TextView textviewUsr;
        String Uid = UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_UserId);

        if (Uid != null && (!Uid.equals(""))) {
            navigationView.inflateMenu(R.menu.menu_drawer_with_login_new);
            //  headerLayout.setVisibility(View.VISIBLE);

            View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main2, null);
            navigationView.addHeaderView(nav_header);
            View headerLayout = navigationView.getHeaderView(0);
            imageView = (ImageView) headerLayout.findViewById(R.id.imageView);
            textviewUsr = (TextView) headerLayout.findViewById(R.id.textUserName);
            imageView.setVisibility(View.GONE);
            String user = UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_USERNAME);
            textviewUsr.setText("Welcome\n\n" + user);
            if (UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_GENDER).equalsIgnoreCase("M") || UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_GENDER).equalsIgnoreCase("MALE")) {
                imageView.setBackgroundResource(R.drawable.mannnnn);
            } else {
                imageView.setBackgroundResource(R.drawable.woman);
            }
            headerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeDrawer();
                    replaceFragmnt(new MyProfileFragment());
                }
            });

        } else {
            //    navigationView.inflateMenu(R.menu.menu_drawer_without_login);


        }


    }

    private void dialog_LOGOUT() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Do you want to logout?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                UTIL.clearPref(ActivityWithNavigationMenu.this);
                Intent abc = new Intent(ActivityWithNavigationMenu.this, LoginActivity.class);
                startActivity(abc);
                Toast.makeText(ActivityWithNavigationMenu.this, "Logout successfully", Toast.LENGTH_SHORT).show();
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
        alertDialog.show();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                dialog_Exit();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
           /* Toast.makeText(this, "Please click BACK again to exit!",
                    Toast.LENGTH_SHORT).show();*/
            addDeviceListFragment();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);


        }
    }

    private void dialog_Exit() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fancyalertdialog, null);
        dialogBuilder.setView(dialogView);


        final TextView title = dialogView.findViewById(R.id.title);
        final TextView message = dialogView.findViewById(R.id.message);
        final Button positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        final Button negativeBtn = dialogView.findViewById(R.id.negativeBtn);


        // dialogBuilder.setTitle("Device Details");
        title.setText("Do you want to exit?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //   finish();
                ActivityWithNavigationMenu.this.finishAffinity();


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AddDevice_Fragment.GPS_SETTINGS) {
                //  new AddDevice_Fragment().onActivityResult(requestCode, resultCode, data);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

        } else {
            Toast.makeText(getApplicationContext(), "GPS is not enabled!", Toast.LENGTH_LONG).show();

            addDeviceListFragment();
        }


    }

    private void UpdateFCMTokenAtServer() {
        String tag_string_req = "req_login";
        String Uid = UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_UserId);
        String token = UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_FCMTOken);

        String URL_LOGIN = null;
        try {
            URL_LOGIN = UTIL.Domain_Arduino + UTIL.FCMTokenUpdate_API + "UserId=" + Uid + "&FCMToken=" + token;
            URL_LOGIN = URL_LOGIN.replaceAll(" ", "%20");
            Log.e("FCMToken update URL", URL_LOGIN);
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("FCM Token update res", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FCM Token update Error", String.valueOf(error));
            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void UpdateFCMTokenAtServer_New() {

        String UserId = UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_UserId);
        String FCMToken = UTIL.getPref(ActivityWithNavigationMenu.this, UTIL.Key_FCMTOken);

        String tag_string_req = "req_login";
        String URL = null;

        Map<String, String> params = new HashMap<>();
        params.put("UserId", UserId);
        params.put("FCMToken", FCMToken);

        URL = UTIL.Domain_Arduino + UTIL.FCMTokenUpdate_API;

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //   utill.hideProgressDialog();
                        Log.e("FCM Token update res", String.valueOf(response));

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FCM Token update Error", String.valueOf(error));

            }
        }) {
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request_json, tag_string_req);


    }


}
