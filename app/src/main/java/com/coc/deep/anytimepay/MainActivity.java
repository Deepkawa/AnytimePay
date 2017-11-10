package com.coc.deep.anytimepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final String user =sharedPreferences.getString("user",null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user!=null ){
                    startActivity(new Intent(MainActivity.this,Homepage.class));
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                }else{
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                    finish();}
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

            }
        }, 1000);
    }
}
