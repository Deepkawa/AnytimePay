package com.coc.deep.anytimepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatApp extends AppCompatActivity {
    ListView listView;
    ChatAdapter adapter ;
    ImageView send;
    int SIGN_IN_REQUEST_CODE=111;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_app);
        final EditText input = (EditText) findViewById(R.id.messageArea);
        listView = (ListView) findViewById(R.id.list);
        pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Log.d("CNSKAL", "onCreate: makLN+https://anytime-pay.firebaseio.com/message/"+pref.getString("user",null));
        send = (ImageView)findViewById((R.id.sendButton));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = "https://anytime-pay.firebaseio.com/message.json";
                Log.d("ugkuk", "onClick:-----here ");

                StringRequest request= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/message");
                        String messagesInput = input.getText().toString();

                        reference.child(pref.getString("user",null)).child("msg"). setValue(messagesInput);
                    }


                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
            }
        });

            // User is already signed in, show list of messages
           showAllOldMessages();

    }

    private void showAllOldMessages() {

        adapter = new ChatAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/message/"));
        listView.setAdapter(adapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showAllOldMessages();
            } else {
                finish();
            }
        }
    }



    public String getLoggedInUserName() {
        return pref.getString("user",null);
    }
}
