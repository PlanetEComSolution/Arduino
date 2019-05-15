package com.pombingsoft.planet_iot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.switch_controller.AddExistingDeviceActivity;
import com.pombingsoft.planet_iot.activity.switch_controller.UpdateWifiCredActivity;
import com.pombingsoft.planet_iot.util.UTIL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pombingsoft.planet_iot.model.Item;

import android.support.v7.widget.GridLayoutManager;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    Button btn_BuyVoucher;
    TextView txt_vouchersCount;
    NavigationView navigationView;
    AlertDialog alertDialog;
    ArrayList<Item> Item_list = new ArrayList<>();
    private RecyclerView recyclerView;


    static final int RequestPermissionCode =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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


      /*  if(CheckingPermissionIsEnabledOrNot())
        {
         //   Toast.makeText(HomeActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        }
        else{
            RequestMultiplePermission();
        }*/

        init();
        manupulateDrawerItems();


    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //  super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();

                return;
            }


            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit!",
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 2000);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*
         *//**//*
        Menu menu = navigationView.getMenu();
        menu.add(R.id.group_1, 123, Menu.NONE, "Title1");
        menu.add(R.id.group_1, 124, Menu.NONE, "Title2");


      *//**/


        int id = item.getItemId();

        // to not highlight when selected
        //  navigationView.getMenu().getItem(item).setChecked(false);


        if (id == R.id.nav_home) {
            // Handle the  action


        } else if (id == R.id.nav_controlDevice) {

            startActivity(new Intent(HomeActivity.this, DeviceListActivity.class));

        } else if (id == R.id.nav_add_new_device) {
            startActivity(new Intent(HomeActivity.this, AddDeviceActivity_New.class));

        } else if (id == R.id.nav_add_existing_device) {
            startActivity(new Intent(HomeActivity.this, AddExistingDeviceActivity.class));

        } else if (id == R.id.nav_logout) {

            dialog_LOGOUT();

        } else if (id == R.id.nav_login) {

        }
        else if (id == R.id.nav_ContactUs) {
            startActivity(new Intent(HomeActivity.this, ContactUs.class));

        }
        else if (id == R.id.nav_AboutUs) {
            startActivity(new Intent(HomeActivity.this, AboutUs.class));

        }
        else if (id == R.id.nav_Help) {
         //   startActivity(new Intent(HomeActivity.this, Help.class));
            startActivity(new Intent(HomeActivity.this, FAQ_Activity.class));

        }



        HomeActivity.this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        //  HomeActivity.this.overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;//return true if want to highlight selected item

    }

    private void init() {
        Item_list.add(new Item("Control DeviceNodeMcu", R.drawable.switch_blue));
        Item_list.add(new Item("Add New DeviceNodeMcu", R.drawable.plus_blue));
        Item_list.add(new Item("Update Wi-Fi", R.drawable.wifi_blue));
        Item_list.add(new Item("Add Existing DeviceNodeMcu", R.drawable.plus_blue_2));
        Item_list.add(new Item("Logout", R.drawable.logout_blue));
        Item_list.add(new Item("Profile", R.drawable.user_blue));


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        IconListAdapter mAdapter = new IconListAdapter(Item_list);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
        recyclerView.setItemAnimator(new android.support.v7.widget.DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);





      /*  LinearLayout ll_controlDevice = (LinearLayout) findViewById(R.id.ll_controlDevice);
        LinearLayout ll_AddNewDevice = (LinearLayout) findViewById(R.id.ll_AddNewDevice);
        LinearLayout ll_Logout = (LinearLayout) findViewById(R.id.ll_Logout);
        LinearLayout ll_AddExistingDevice = (LinearLayout) findViewById(R.id.ll_AddExistingDevice);
        LinearLayout ll_updateWifi = (LinearLayout) findViewById(R.id.ll_updateWifi);

        LinearLayout ll_Profile = (LinearLayout) findViewById(R.id.ll_Profile);


        ll_controlDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, DeviceListActivity.class));
            }
        });

        ll_AddNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, AddDeviceActivity_New.class));

            }
        });
        ll_AddExistingDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, AddExistingDeviceActivity.class));

            }
        });
        ll_updateWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, UpdateWifiCredActivity.class));

            }
        });
        ll_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_LOGOUT();

            }
        });

        ll_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));

            }
        });
*/

    }

    public void manupulateDrawerItems() {

        /*set drawer menu programmatically*/

        ImageView imageView;
        TextView textviewUsr;
        String Uid = UTIL.getPref(HomeActivity.this, UTIL.Key_UserId);

        if (Uid != null && (!Uid.equals(""))) {
            navigationView.inflateMenu(R.menu.menu_drawer_with_login);
            //  headerLayout.setVisibility(View.VISIBLE);

            View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main2, null);
            navigationView.addHeaderView(nav_header);
            View headerLayout = navigationView.getHeaderView(0);
            imageView = (ImageView) headerLayout.findViewById(R.id.imageView);
            textviewUsr = (TextView) headerLayout.findViewById(R.id.textUserName);
            imageView.setVisibility(View.GONE);
            String user = UTIL.getPref(HomeActivity.this, UTIL.Key_USERNAME);
            textviewUsr.setText("Welcome\n\n" + user);
            if (UTIL.getPref(HomeActivity.this, UTIL.Key_GENDER).equalsIgnoreCase("M") || UTIL.getPref(HomeActivity.this, UTIL.Key_GENDER).equalsIgnoreCase("MALE")) {
                imageView.setBackgroundResource(R.drawable.mannnnn);
            } else {
                imageView.setBackgroundResource(R.drawable.woman);
            }


        } else {
         //   navigationView.inflateMenu(R.menu.menu_drawer_without_login);


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


        // dialogBuilder.setTitle("DeviceNodeMcu Details");
        title.setText("Do you want to logout?");
        message.setText("Are you sure?");
        positiveBtn.setText("Yes");
        negativeBtn.setText("No");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                UTIL.clearPref(HomeActivity.this);
                Intent abc = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(abc);
                Toast.makeText(HomeActivity.this, "Logout successfully", Toast.LENGTH_SHORT).show();
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


    public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.MyViewHolder> {

        private List<Item> ItemList;


        public IconListAdapter(List<Item> ItemList) {
            this.ItemList = ItemList;

        }

        @Override
        public IconListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_dash, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final Item item = ItemList.get(position);
            holder.title.setText(item.getTitle());
            holder.icon.setImageResource(item.getIcon());

            holder.ll_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    switch (position) {
                        case 0:
                            startActivity(new Intent(HomeActivity.this, DeviceListActivity.class));

                            break;
                        case 1:
                            startActivity(new Intent(HomeActivity.this, AddDeviceActivity_New.class));

                            break;
                        case 2:
                            startActivity(new Intent(HomeActivity.this, UpdateWifiCredActivity.class));

                            break;
                        case 3:

                            startActivity(new Intent(HomeActivity.this, AddExistingDeviceActivity.class));

                            break;
                        case 4:
                            dialog_LOGOUT();
                            break;
                        case 5:
                            startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));

                            break;


                    }


                }
            });


        }

        @Override
        public int getItemCount() {
            return ItemList.size();
        }



        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView icon;
            public LinearLayout ll_parent;

            public MyViewHolder(android.view.View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.txt_title);
                icon = (ImageView) view.findViewById(R.id.icon);
                ll_parent = (LinearLayout) view.findViewById(R.id.ll_menu);
            }
        }
    }
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,

                }, RequestPermissionCode);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean Location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExtrenalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (WriteExtrenalStorage && Location ) {

                      //  Toast.makeText(HomeActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(HomeActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED
               ;
    }

}
