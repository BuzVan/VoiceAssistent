package com.voiceassistent.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    public String text;
    public Date date;
    public Boolean isSend;

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }
}
