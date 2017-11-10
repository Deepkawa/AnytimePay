package com.coc.deep.anytimepay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUp extends AppCompatActivity {

    EditText signupPass, signupUser;
    Button register;
    String user,pass;
    String url1 = "https://anytime-pay.firebaseio.com/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signupPass = (EditText)findViewById(R.id.sign_password);
        signupUser = (EditText)findViewById(R.id.sign_phone);
        register = (Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("bvsyu", "onClick-nsjnjn: ");
                user=signupUser.getText().toString();
                pass=signupPass.getText().toString();

                if(user.equals("")){
                    signupUser.setError("Cant be empty");

                }
                else if(pass.equals("")){
                    signupPass.setError("Cant be empty");

                }
                else{
                    final String url = "https://anytime-pay.firebaseio.com/users.json";
                    Log.d("ugkuk", "onClick:-----here ");

                    StringRequest request= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.d("dhbuwi", "onResponse: ===kl===");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/users");
                            //String userID = reference.push().getKey();
                            //Log.e("deep",""+userID);
                            if(s.equals("null")) {
                                reference.child(user).child("password").setValue(pass);
                                Toast.makeText(SignUp.this, "registration successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUp.this,Login.class));

                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        reference.child(user).child("password").setValue(pass);
                                        reference.child(user).child("amount").setValue("1000");

                                        Toast.makeText(SignUp.this, "registration successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignUp.this,Login.class));
                                    } else {
                                        Toast.makeText(SignUp.this, "username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(SignUp.this);
                    rQueue.add(request);
                }
            }
        });
    }
}
