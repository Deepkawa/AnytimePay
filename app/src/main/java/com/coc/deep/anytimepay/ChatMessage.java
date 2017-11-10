package com.coc.deep.anytimepay;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String phoneNo;

    public ChatMessage(String messageText, String phoneNo) {
        this.messageText = messageText;
        this.phoneNo = phoneNo;
    }

    public ChatMessage(){

    }

    public String getphoneNo() {
        return phoneNo;
    }

    public void setphoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


}