package com.voiceassistent.messageView;

import com.voiceassistent.dataBase.MessageEntity;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
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
    public Message(MessageEntity entity){
        this.text = entity.text;
        this.isSend = entity.isSend==1;
        this.date = new Date(Long.parseLong(entity.date));
    }
}
