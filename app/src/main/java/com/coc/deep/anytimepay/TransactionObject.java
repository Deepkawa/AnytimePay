package com.coc.deep.anytimepay;

import java.sql.Time;

/**
 * Created by Deep on 04/11/2017.
 */

public class TransactionObject {
    int amount;
    String source, destination;
    String time;

    public TransactionObject(int amount, String source, String destination, String time) {
        this.amount = amount;
        this.source = source;
        this.destination = destination;
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
