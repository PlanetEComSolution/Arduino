package com.pombingsoft.planet_iot.activity.water_level_controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.activity.AddDeviceActivity_New;
import com.pombingsoft.planet_iot.util.Myspinner;
import com.pombingsoft.planet_iot.util.UTIL;

import java.util.ArrayList;

public class SetUpActivity extends AppCompatActivity {

    Spinner spinner_tank_setting, spinner_tankType;
    Button btn_Register_Device;

/*
    String DeviceId,
            DeviceName,
            DeviceType,
            Device_Password,
            timezoneName,
            timezoneID;*/
    EditText et_Upper_level_depth;
    EditText et_Upper_level_water_capacity;
    EditText et_pump_on_time_max;
    EditText et_pump_cool_time_min;
    Context myContext;
    UTIL utill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        utill = new UTIL(SetUpActivity.this);
        myContext = SetUpActivity.this;

        spinner_tank_setting = findViewById(R.id.spinner_tank_setting);
        spinner_tankType = findViewById(R.id.spinner_tankType);
        btn_Register_Device = findViewById(R.id.btn_Register_Device);


        et_Upper_level_depth = findViewById(R.id.et_Upper_level_depth);
        et_Upper_level_water_capacity = findViewById(R.id.et_Upper_level_water_capacity);
        et_pump_on_time_max = findViewById(R.id.et_pump_on_time_max);
        et_pump_cool_time_min = findViewById(R.id.et_pump_cool_time_min);


        ArrayList<Myspinner> list_tankType=new ArrayList<>();
        list_tankType.add(new Myspinner("Single Tank","0"));
        list_tankType.add(new Myspinner("Double Tank","1"));

        ArrayAdapter<ArrayList<Myspinner>> aa = new ArrayAdapter(SetUpActivity.this, android.R.layout.simple_spinner_dropdown_item, list_tankType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tank_setting.setAdapter(aa);



        ArrayList<Myspinner> tank_func_state=new ArrayList<>();
        tank_func_state.add(new Myspinner("Manual","0"));
        tank_func_state.add(new Myspinner("Fully Automatic","1"));
        tank_func_state.add(new Myspinner("Semi Automatic","2"));

        ArrayAdapter<ArrayList<Myspinner>> aa1 = new ArrayAdapter(SetUpActivity.this, android.R.layout.simple_spinner_dropdown_item, tank_func_state);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tankType.setAdapter(aa1);


/*

        String[] freq = {"Single Tank", "Double Tank"};
        ArrayAdapter aa = new ArrayAdapter(SetUpActivity.this, android.R.layout.simple_spinner_dropdown_item, freq);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tank_setting.setAdapter(aa);


        String[] freq1 = {"Manual", "Fully Automatic", "Semi Automatic"};
        ArrayAdapter aa1 = new ArrayAdapter(SetUpActivity.this, android.R.layout.simple_spinner_dropdown_item, freq1);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tankType.setAdapter(aa1);
*/


        et_Upper_level_depth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int Upper_level_depth =0;
                try {
                    Upper_level_depth = Integer.parseInt( et_Upper_level_depth.getText().toString().trim());
                if(Upper_level_depth>400){
                    Toast.makeText(myContext,"Max allowed number is 400.",Toast.LENGTH_SHORT).show();
                    et_Upper_level_depth.setText("");
                }
                } catch (Exception e) {
                   e.getMessage();
                }
            }
        });
        et_Upper_level_water_capacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int Upper_level_water_capacity =0;
                try {
                    Upper_level_water_capacity = Integer.parseInt( et_Upper_level_water_capacity.getText().toString().trim());
                    if(Upper_level_water_capacity>25000){
                        Toast.makeText(myContext,"Max allowed number is 25000.",Toast.LENGTH_SHORT).show();
                        et_Upper_level_water_capacity.setText("");
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        et_pump_on_time_max.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int pump_on_time_max =0;
                try {
                    pump_on_time_max = Integer.parseInt( et_pump_on_time_max.getText().toString().trim());
                    if(pump_on_time_max>240){
                        Toast.makeText(myContext,"Max allowed number is 240.",Toast.LENGTH_SHORT).show();
                        et_pump_on_time_max.setText("");
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        et_pump_cool_time_min.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int pump_cool_time_min =0;
                try {
                    pump_cool_time_min = Integer.parseInt( et_pump_cool_time_min.getText().toString().trim());
                    if(pump_cool_time_min>240){
                        Toast.makeText(myContext,"Max allowed number is 240.",Toast.LENGTH_SHORT).show();
                        et_pump_cool_time_min.setText("");
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        //   UTIL.notification(SetUpActivity.this, "1", "Switch", "Switch is ON");





        btn_Register_Device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Upper_level_depth=et_Upper_level_depth.getText().toString().trim();
                String Upper_level_water_capacity=et_Upper_level_water_capacity.getText().toString().trim();
                String pump_on_time_max=et_pump_on_time_max.getText().toString().trim();
                String pump_cool_time_min=et_pump_cool_time_min.getText().toString().trim();

                if(Upper_level_depth.equals("") || Upper_level_depth.equals("0")){
                    Toast.makeText(myContext,"Please enter upper level depth.",Toast.LENGTH_SHORT).show();

                }
               else if(Upper_level_water_capacity.equals("")|| Upper_level_water_capacity.equals("0")){
                    Toast.makeText(myContext,"Please enter upper level water capacity.",Toast.LENGTH_SHORT).show();

                }
                else if(pump_on_time_max.equals("")|| pump_on_time_max.equals("0")){
                    Toast.makeText(myContext,"Please enter max pump ON time.",Toast.LENGTH_SHORT).show();

                }
                else if(pump_cool_time_min.equals("")|| pump_cool_time_min.equals("0")){
                    Toast.makeText(myContext,"Please enter min pump cool time.",Toast.LENGTH_SHORT).show();

                }
                else{

                    Myspinner myspinner=(Myspinner)  spinner_tank_setting.getSelectedItem();
                    String tank_type =  myspinner.getspinnerVal();

                    Myspinner myspinner1=(Myspinner)  spinner_tankType.getSelectedItem();
                    String tank_function_state =  myspinner1.getspinnerVal();


                    Intent intent=new Intent();
                    intent.putExtra("tank_type",tank_type);
                    intent.putExtra("tank_function_state",tank_function_state);
                    intent.putExtra("Upper_level_depth",Upper_level_depth);
                    intent.putExtra("Upper_level_water_capacity",Upper_level_water_capacity);
                    intent.putExtra("pump_on_time_max",pump_on_time_max);
                    intent.putExtra("pump_cool_time_min",pump_cool_time_min);

                    setResult(UTIL.Water_controller_SETTINGS,intent);
                    finish();//finishing activity

                }


            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
