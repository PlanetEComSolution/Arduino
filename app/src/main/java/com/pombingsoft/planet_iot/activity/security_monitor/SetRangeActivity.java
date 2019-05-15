package com.pombingsoft.planet_iot.activity.security_monitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pombingsoft.planet_iot.R;

import com.pombingsoft.planet_iot.util.UTIL;

public class SetRangeActivity extends AppCompatActivity {

    EditText et_min_temp, et_max_temp, et_min_humidity, et_max_humidity;
    Context myContext;
    UTIL utill;
    Button btn_setRange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_range);

        utill = new UTIL(SetRangeActivity.this);
        myContext = SetRangeActivity.this;

        et_min_temp = findViewById(R.id.et_min_temp);
        et_max_temp = findViewById(R.id.et_max_temp);
        et_min_humidity = findViewById(R.id.et_min_humidity);
        et_max_humidity = findViewById(R.id.et_max_humidity);
        btn_setRange = findViewById(R.id.btn_setRange);



        btn_setRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String min_temp=et_min_temp.getText().toString().trim();
                String max_temp=et_max_temp.getText().toString().trim();
                String min_humidity=et_min_humidity.getText().toString().trim();
                String max_humidity=et_max_humidity.getText().toString().trim();



                if(min_temp.equals("") || min_temp.equals("0")){
                    Toast.makeText(myContext,"Please enter min temperature.",Toast.LENGTH_SHORT).show();

                }
                else if(max_temp.equals("")|| max_temp.equals("0")){
                    Toast.makeText(myContext,"Please enter max temperature.",Toast.LENGTH_SHORT).show();

                }
                else if(min_humidity.equals("")|| min_humidity.equals("0")){
                    Toast.makeText(myContext,"Please enter min humidity.",Toast.LENGTH_SHORT).show();

                }
                else if(max_humidity.equals("")|| max_humidity.equals("0")){
                    Toast.makeText(myContext,"Please enter max humidity.",Toast.LENGTH_SHORT).show();

                }
                else{



                    Intent intent=new Intent();
                    intent.putExtra("min_temp",min_temp);
                    intent.putExtra("max_temp",max_temp);
                    intent.putExtra("min_humidity",min_humidity);
                    intent.putExtra("max_humidity",max_humidity);
                    setResult( UTIL.Security_monitor_SETTINGS,intent);
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
