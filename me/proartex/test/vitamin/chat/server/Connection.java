package me.proartex.test.vitamin.chat.server;

import java.util.LinkedList;

public class Connection {

    private String userName;
    private LinkedList<byte[]> messageQueue = new LinkedList<>();

    public String getUserName() {
        return userName;
    }

    public LinkedList<byte[]> getMessageQueue() {
        return messageQueue;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
