package me.proartex.test.vitamin.chat.server;

import java.util.LinkedList;

public class Connection {

    private String username;
    private LinkedList<String> messageQueue = new LinkedList<>();

    public String getUsername() {
        return username;
    }

    public LinkedList<String> getMessageQueue() {
        return messageQueue;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
