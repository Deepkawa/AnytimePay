package com.coc.deep.anytimepay;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deep on 04/11/2017.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    List<TransactionObject> Transactions =new ArrayList<>();
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public TransactionAdapter(List<TransactionObject> transactions) {
        this.Transactions = transactions;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TransactionObject transactionObject = Transactions.get(position);
        holder.Source.setText(transactionObject.getSource());
        holder.Destination.setText(transactionObject.getDestination());
        if(transactionObject.getAmount()==0)
            holder.Amount.setText("amt");
        else
            holder.Amount.setText(String.valueOf(transactionObject.getAmount()));
        if(transactionObject.getTime()==null) {
            holder.Timing.setText("Time");
        }else
            holder.Timing.setText(transactionObject.getTime());
    }

    @Override
    public int getItemCount() {
        return Transactions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Source,Destination,Amount,Timing;
        public MyViewHolder(View view) {
            super(view);
            Source = (TextView) view.findViewById(R.id.source);
            Destination = (TextView) view.findViewById(R.id.destination);
            Amount = (TextView) view.findViewById(R.id.amount);
            Timing = (TextView) view.findViewById(R.id.timing);


        }
    }
}
