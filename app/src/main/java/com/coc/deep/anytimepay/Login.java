package com.coc.deep.anytimepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Deep on 04/11/2017.
 */

import org.json.JSONException;
import org.json.JSONObject;
public class Login extends AppCompatActivity {
    EditText phoneNo, password;
    Button login;
    TextView signUp;
    String user, pass;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneNo = (EditText) findViewById(R.id.login_phone);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login);
        signUp = (TextView) findViewById(R.id.signup_link);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, SignUp.class);

                startActivity(i);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = phoneNo.getText().toString();
                pass = password.getText().toString();
                if (user.equals("")) {
                    phoneNo.setError("can't be blank");
                } else if (pass.equals("")) {
                    password.setError("can't be blank");
                } else {
                    String url = "https://anytime-pay.firebaseio.com/users.json";
                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (s.equals("null"))
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                    } else if (obj.getJSONObject(user).getString("password").equals(pass)) {

                                        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=pref.edit();

                                        editor.putString("user",user);
                                        editor.apply();
                                        Intent i = new Intent(Login.this, Homepage.class);

                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);

                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);

                }


            }
        });
    }
}
