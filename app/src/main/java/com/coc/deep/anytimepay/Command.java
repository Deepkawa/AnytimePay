package com.coc.deep.anytimepay;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Command extends AppCompatActivity {

    EditText messageArea;
    ImageView send;
    TextView reply, commandDisplay;
    SharedPreferences pref;
    TransactionAdapter transAdp;
    List<TransactionObject> transactionObjectsList =new ArrayList<>();

    RecyclerView recycle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        messageArea = (EditText) findViewById(R.id.messageArea);
        send = (ImageView) findViewById(R.id.sendButton);
        reply = (TextView) findViewById(R.id.reply);
        commandDisplay = (TextView) findViewById(R.id.command_display);
        pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        recycle= (RecyclerView)findViewById(R.id.recycler_view);
        transAdp = new TransactionAdapter(transactionObjectsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(mLayoutManager);
        recycle.setAdapter(transAdp);
        final String user = pref.getString("user","");
        final boolean[] flag = {true,true};

        final int[] amountSource = new int[1];
        final int[] amountDestination = new int[1];

        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!messageArea.getText().toString().equals("")){
                        send.setImageResource(R.drawable.send);

                    }else{
                        send.setImageResource(R.drawable.mic);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String msg = messageArea.getText().toString();
                messageArea.setText("");
                if (msg.equals("")) {
                    promptSpeechInput();

                } else {
                    final String[] msgsplit = msg.split(" ");
                    commandDisplay.setText(msg);
                    if (msg.contains("pay") ) {
                         int amt=0 ;
                        if(!msg.replaceAll("[\\D]", "").equals("")) {
                             amt = Integer.parseInt(msg.replaceAll("[\\D]", ""));
                        }
                        reply.setText("OK sir, " + amt + " will be transferred to " + msgsplit[msgsplit.length - 1]);
                        final String urlTransaction = "https://anytime-pay.firebaseio.com/transactions.json";
                        final String urlUser = "https://anytime-pay.firebaseio.com/users.json";
                        final String urlTransactionDestination = "https://anytime-pay.firebaseio.com/transactionDestination.json";

                        final RequestQueue rrQueue = Volley.newRequestQueue(Command.this);
                        final int finalAmt = amt;
                        final StringRequest request = new StringRequest(Request.Method.GET, urlTransaction, new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {

                                final String tempresponse =response;
                                Log.d("reds", "onResponse: " + flag[0]);
                                if (flag[0]) {

                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/users");
                                    final DatabaseReference tempRef = reference;
                                    reference.child(user).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            amountSource[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                                            if (amountSource[0] - finalAmt < 0) {
                                                reply.setText("insufficient balance");

                                            } else {
                                                tempRef.child(user).child("amount").setValue(String.valueOf(amountSource[0] - finalAmt));


                                                DatabaseReference tempreference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/transactions");
                                                DatabaseReference referenceDestination = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/transactionDestination");
                                                String ti = DateFormat.getDateTimeInstance().format(new Date());
                                                if (tempresponse.equals("null")) {

                                                    tempreference.child(user).child(msgsplit[msgsplit.length - 1]).child(ti).child("amount").setValue(finalAmt);
                                                    referenceDestination.child(msgsplit[msgsplit.length - 1]).child(user).child(ti).child("amount").setValue(finalAmt);

                                                } else {
                                                    try {
                                                        JSONObject obj = new JSONObject(tempresponse);
                                                        if (!obj.has(user)) {

                                                            tempreference.child(user).child(msgsplit[msgsplit.length - 1]).child(ti).child("amount").setValue(finalAmt);

                                                            referenceDestination.child(msgsplit[msgsplit.length - 1]).child(user).child(ti).child("amount").setValue(finalAmt);

                                                        } else {
                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/transactions/" + user);

                                                            ref.child(msgsplit[msgsplit.length - 1]).child(ti).child("amount").setValue(finalAmt);
                                                            referenceDestination.child(msgsplit[msgsplit.length - 1]).child(user).child(ti).child("amount").setValue(finalAmt);


                                                        }


                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    tempreference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/users");
                                        /*    reference.child(user).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    amountSource[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                                                    if (amountSource[0] - Integer.parseInt(msgsplit[1]) < 0) {
                                                        reply.setText("insufficient balance");
                                                        flag[1] = false;
                                                    } else {
                                                        tempRef.child(user).child("amount").setValue(String.valueOf(amountSource[0] - Integer.parseInt(msgsplit[1])));


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });*/
                                                    Log.d("tagagga", "onDataChange: "+flag[1]);
                                                    tempreference.child(msgsplit[msgsplit.length - 1]).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                                amountDestination[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                                                                tempRef.child(msgsplit[msgsplit.length - 1]).child("amount").setValue(String.valueOf(amountDestination[0] + finalAmt));
                                                                reply.setText("transaction successful");
                                                                Toast.makeText(Command.this, "transaction successful", Toast.LENGTH_SHORT).show();
                                                                Log.d("taggu", "onDataChange: " + Integer.parseInt(dataSnapshot.getValue().toString()));

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                } else {
                                    flag[0] = true;
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                            }
                        });


                        StringRequest userValidation = new StringRequest(Request.Method.GET, urlUser, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d("redsoo", "onResponse: " + flag[0]);


                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.has(msgsplit[msgsplit.length - 1]) || user.equals(msgsplit[msgsplit.length - 1])) {
                                        flag[0] = false;
                                        reply.setText("transaction cant be processed");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                rrQueue.add(request);
                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                            }
                        });
                        rrQueue.add(userValidation);


                    } else if (msg.contains("balance")) {

                        DatabaseReference refer = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/users/" + user);
                        refer.child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int balance = Integer.parseInt(dataSnapshot.getValue().toString());
                                reply.setText("Your balance is : Rs." + balance);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else if (msg.contains("history")) {
                        transactionObjectsList.clear();
                        TransactionObject to = new TransactionObject(0,"source","destination",null);

                        transactionObjectsList.add(to);
                        transAdp.notifyDataSetChanged();



                        final RequestQueue rrQueue = Volley.newRequestQueue(Command.this);
                        int duration =0;
                        if(!msg.replaceAll("[\\D]", "").equals(""))
                          duration = Integer.parseInt(msg.replaceAll("[\\D]", ""));

                        if( msg.replaceAll("[\\D]", "").equals("")  ) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://anytime-pay.firebaseio.com/transactions/" + user);

                            if(msg.contains("transactions")){
                            final StringRequest newrequest = new StringRequest(Request.Method.GET, "https://anytime-pay.firebaseio.com/transactions/" + user + "/" + msgsplit[msgsplit.length - 1] + ".json", new Response.Listener<String>() {


                                @Override
                                public void onResponse(String response) {

                                    if (response.equals(null)) {

                                        reply.setText("No transaction history");
                                    } else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Iterator itr = jsonObject.keys();
                                            JSONArray jsonarray = new JSONArray();
                                            int k = 0;
                                            while ((itr.hasNext())) {
                                                String ket = (String) itr.next();
                                                jsonarray.put(jsonObject.get(ket));
                                                String am = jsonarray.getJSONObject(k).getString("amount");
                                                k++;
                                                TransactionObject transactionObj = new TransactionObject(Integer.valueOf(am), user, msgsplit[msgsplit.length - 1], ket);
                                                transactionObjectsList.add(transactionObj);
                                                transAdp.notifyDataSetChanged();


                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            });

                            rrQueue.add(newrequest);


                            final StringRequest newrequestreceive = new StringRequest(Request.Method.GET, "https://anytime-pay.firebaseio.com/transactionsDestination/" + msgsplit[msgsplit.length - 1] + "/" + user + ".json", new Response.Listener<String>() {


                                @Override
                                public void onResponse(String response) {

                                    if (response.equals(null)) {

                                        reply.setText("No transaction history");
                                    } else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Iterator itr = jsonObject.keys();
                                            JSONArray jsonarray = new JSONArray();
                                            int k = 0;
                                            while ((itr.hasNext())) {
                                                String ket = (String) itr.next();
                                                jsonarray.put(jsonObject.get(ket));
                                                String am = jsonarray.getJSONObject(k).getString("amount");
                                                k++;
                                                TransactionObject transactionObj = new TransactionObject(Integer.valueOf(am), msgsplit[msgsplit.length - 1], user, ket);
                                                transactionObjectsList.add(transactionObj);
                                                transAdp.notifyDataSetChanged();


                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            });
                            rrQueue.add(newrequestreceive);
                        }else if(msg.contains("transfers")){
                                final StringRequest newrequest = new StringRequest(Request.Method.GET, "https://anytime-pay.firebaseio.com/transactions/" + user + "/" + msgsplit[msgsplit.length - 1] + ".json", new Response.Listener<String>() {


                                    @Override
                                    public void onResponse(String response) {

                                        if (response.equals(null)) {

                                            reply.setText("No transaction history");
                                        } else {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Iterator itr = jsonObject.keys();
                                                Log.d("taggg", "onResponse: "+jsonObject.toString());;
                                                JSONArray jsonarray = new JSONArray();
                                                int k = 0;
                                                while ((itr.hasNext())) {
                                                    String ket = (String) itr.next();
                                                    jsonarray.put(jsonObject.get(ket));
                                                    String am = jsonarray.getJSONObject(k).getString("amount");
                                                    k++;
                                                    TransactionObject transactionObj = new TransactionObject(Integer.valueOf(am), user, msgsplit[msgsplit.length - 1], ket);
                                                    transactionObjectsList.add(transactionObj);
                                                    transAdp.notifyDataSetChanged();


                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                    }
                                });

                                rrQueue.add(newrequest);

                            }else if(msg.contains("received")){
                                final StringRequest newrequestreceive = new StringRequest(Request.Method.GET, "https://anytime-pay.firebaseio.com/transactionDestination/" + user + "/" + msgsplit[msgsplit.length - 1] + ".json", new Response.Listener<String>() {


                                    @Override
                                    public void onResponse(String response) {

                                        if (response.equals(null)) {

                                            reply.setText("No transaction history");
                                        } else {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Iterator itr = jsonObject.keys();
                                                JSONArray jsonarray = new JSONArray();
                                                int k = 0;
                                                while ((itr.hasNext())) {
                                                    String ket = (String) itr.next();
                                                    jsonarray.put(jsonObject.get(ket));
                                                    String am = jsonarray.getJSONObject(k).getString("amount");
                                                    k++;
                                                    TransactionObject transactionObj = new TransactionObject(Integer.valueOf(am), msgsplit[msgsplit.length - 1], user, ket);
                                                    transactionObjectsList.add(transactionObj);
                                                    transAdp.notifyDataSetChanged();


                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                    }
                                });
                                rrQueue.add(newrequestreceive);

                            }
                        }

                          else if (msg.contains("hours")) {
                            final int finalDuration = duration;
                            final StringRequest request = new StringRequest(Request.Method.GET, "https://anytime-pay.firebaseio.com/transactions/"+user+".json", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("taggu", "onResponse: ");
                                    if(response.equals(null)){
                                        reply.setText("No transaction history");
                                    }else{
                                        try {
                                            JSONObject objTra = new JSONObject(response);
                                            Log.d("taggu", "onResponse: "+objTra.toString());

                                            JSONArray array=new JSONArray();
                                            Iterator keys = objTra.keys();
                                            int i=0;
                                            while (keys.hasNext()){
                                                String key = (String) keys.next();
                                                array.put(objTra.get(key));
                                                JSONObject Tobject= array.getJSONObject(i);
                                                i++;
                                                Iterator temkeys = Tobject.keys();
                                                JSONArray temarray = new JSONArray();
                                                int j=0;
                                                while (temkeys.hasNext()){
                                                    String temkey = (String) temkeys.next();

                                                    temarray.put(Tobject.get(temkey));
                                                    JSONObject tobj = temarray.getJSONObject(j);
                                                    int amt =Integer.parseInt( tobj.getString("amount"));
                                                    Log.d("taggu", "onResponse: "+amt);
                                                    if(timeDiff(temkey)< finalDuration) {
                                                        TransactionObject transactionObj = new TransactionObject(amt, user, key, temkey);
                                                        transactionObjectsList.add(transactionObj);
                                                        transAdp.notifyDataSetChanged();
                                                    }
                                                    j++;
                                                }

                                            }

                                           /*
                                            JSONObject Tobject = array.getJSONObject(0);
                                            array=new JSONArray();
                                            keys = Tobject.keys();
                                            int i=0;
                                            while (keys.hasNext()){
                                                String key = (String) keys.next();
                                                array.put(Tobject.get(key));
                                                JSONObject tranobj = array.getJSONObject(i);
                                                Iterator transit = array.getJSONObject(i).keys();
                                                JSONArray tarray = new JSONArray();
                                                int j=0;
                                                while(transit.hasNext()){
                                                    String tkey = (String) transit.next();
                                                    tarray.put(tranobj.get(tkey));
                                                    String amt =tarray.getJSONObject(j).getString("amount");
                                                   if(timeDiff(tkey)< finalDuration) {
                                                        TransactionObject transactionObj = new TransactionObject(Integer.parseInt(amt), user, key, tkey);
                                                        transactionObjectsList.add(transactionObj);
                                                        transAdp.notifyDataSetChanged();
                                                   }
                                                    j++;
                                                }

                                                i++;

                                            }

*/



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                }


                            }, new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            });
                            rrQueue.add(request);

                              final StringRequest receiverrequest = new StringRequest(Request.Method.GET, "https://anytime-pay.firebaseio.com/transactionDestination/"+user+".json", new Response.Listener<String>() {
                                  @Override
                                  public void onResponse(String response) {
                                      Log.d("taggu", "onResponse: ");

                                      if(response.equals(null)){
                                          reply.setText("No transaction history");
                                      }else{
                                          try {
                                              JSONObject objTra = new JSONObject(response);
                                              Log.d("taggu", "onResponse: "+objTra.toString());

                                              JSONArray array=new JSONArray();
                                              Iterator keys = objTra.keys();
                                              int i=0;
                                              while (keys.hasNext()){
                                                  String key = (String) keys.next();
                                                  array.put(objTra.get(key));
                                                  JSONObject Tobject= array.getJSONObject(i);
                                                  i++;
                                                  Iterator temkeys = Tobject.keys();
                                                  JSONArray temarray = new JSONArray();
                                                  int j=0;
                                                  while (temkeys.hasNext()){
                                                      String temkey = (String) temkeys.next();

                                                      temarray.put(Tobject.get(temkey));
                                                      JSONObject tobj = temarray.getJSONObject(j);
                                                      int amt =Integer.parseInt( tobj.getString("amount"));
                                                      Log.d("taggu", "onResponse: "+amt);
                                                      if(timeDiff(temkey)< finalDuration) {
                                                          TransactionObject transactionObj = new TransactionObject(amt, key, user, temkey);
                                                          transactionObjectsList.add(transactionObj);
                                                          transAdp.notifyDataSetChanged();
                                                      }
                                                      j++;
                                                  }

                                              }
                                              /*JSONObject objTra = new JSONObject(response);
                                              Log.d("taggu", "onResponse: "+objTra.toString());
                                              JSONArray array=new JSONArray();
                                              Iterator keys = objTra.keys();
                                              while (keys.hasNext()){
                                                  String key = (String) keys.next();
                                                  array.put(objTra.get(key));
                                              }
                                              JSONObject Tobject = array.getJSONObject(0);
                                              array=new JSONArray();
                                              keys = Tobject.keys();
                                              int i=0;
                                              while (keys.hasNext()){
                                                  String key = (String) keys.next();
                                                  array.put(Tobject.get(key));
                                                  JSONObject tranobj = array.getJSONObject(i);
                                                  Iterator transit = array.getJSONObject(i).keys();
                                                  JSONArray tarray = new JSONArray();
                                                  int j=0;
                                                  while(transit.hasNext()){
                                                      String tkey = (String) transit.next();
                                                      tarray.put(tranobj.get(tkey));
                                                      String amt =tarray.getJSONObject(j).getString("amount");
                                                      if(timeDiff(tkey)< finalDuration) {
                                                          TransactionObject transactionObj = new TransactionObject(Integer.parseInt(amt), key, user, tkey);
                                                          transactionObjectsList.add(transactionObj);
                                                          transAdp.notifyDataSetChanged();
                                                      }
                                                      j++;
                                                  }

                                                  i++;

                                              }




*/
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }


                                      }

                                  }


                              }, new Response.ErrorListener(){
                                  @Override
                                  public void onErrorResponse(VolleyError volleyError) {
                                  }
                              });
                              rrQueue.add(receiverrequest);



                          }


                    }
                }
            }
        });
    }
public float timeDiff(String date){
    String dateStart = date;
    String dateStop = DateFormat.getDateTimeInstance().format(new Date());

// Custom date format
    SimpleDateFormat format = new SimpleDateFormat(("MMM dd,yyyy hh:mm:ss aa"));

    Date d1 = null;
    Date d2 = null;
    try {
        d1 = format.parse(dateStart);
        d2 = format.parse(dateStop);
    } catch (ParseException e) {
        e.printStackTrace();
    }


// Get msec from each, and subtract.
    long diff = d2.getTime() - d1.getTime();
    Log.d("did2", "timeDiff: "+diff+ " "+d1.getTime()+"   "+d2.getTime());
    long diffSeconds = diff / 1000 % 60;
    long diffMinutes = diff / (60 * 1000) % 60;
    float diffHours = (float)diff / (60 * 60 * 1000);
    System.out.println("Time in seconds: " + diffSeconds + " seconds.");
    System.out.println("Time in minutes: " + diffMinutes + " minutes.");
    System.out.println("Time in hours: " + diffHours + " hours.");
    Log.d("did2", "timeDiff: "+diffHours);
    return diffHours;
}

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    messageArea.setText(result.get(0));
                }
                break;
            }

        }
    }

}

