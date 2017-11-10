package com.coc.deep.anytimepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    TextView  name;
    SharedPreferences pref;
    LinearLayout linearLayout,faq,logout;
   /* RecyclerView recyclerView;
    List<TransactionObject> transactionObjectsList =new ArrayList<>();
    TransactionAdapter transactionAdapter;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

       /* recyclerView = (RecyclerView)findViewById(R.id.recycler_view) ;
        transactionAdapter = new TransactionAdapter(transactionObjectsList);*/
       name =(TextView)findViewById(R.id.hi);
        pref= getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        name.setText("HI "+pref.getString("user","")+"!");
        faq = (LinearLayout) findViewById(R.id.faq);

        logout = (LinearLayout)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit();
                pref.clear();
                pref.commit();
                startActivity(new Intent(Homepage.this,Login.class));
                finish();

            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Homepage.this,FAQ.class));
            }
        });
        linearLayout = (LinearLayout) findViewById(R.id.command);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(Homepage.this,Command.class);
                Log.d("jj", "onClick:jjj ");
                startActivity(intent);

            }
        });
       /* RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(transactionAdapter);
        prepareData();
*/

    }

   /* private void prepareData() {
        TransactionObject transactionObject = new TransactionObject(500,1,"Deep","Mervin","12:50");
        transactionObjectsList.add(transactionObject);

        transactionObject = new TransactionObject(100,2,"Mervin","Deep","12:55");
        transactionObjectsList.add(transactionObject);

        transactionObject = new TransactionObject(100,2,"Mervin","Deep","12:55");
        transactionObjectsList.add(transactionObject);

        transactionAdapter.notifyDataSetChanged();


    }

*/
}
