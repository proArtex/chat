package me.proartex.test.vitamin.chat.server;

import java.util.Date;

public class Message {

    private final String msg;
    private final Date date;

    public Message(Date date, String msg) {
        this.msg = msg;
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public Date getDate() {
        return date;
    }
}
