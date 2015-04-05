package me.proartex.test.vitamin.chat.server;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class User {

//    public static int OP_READ  = SelectionKey.OP_READ;
//    public static int OP_WRITE = SelectionKey.OP_WRITE;
//    private SelectionKey key;
    private String username;
    private LinkedList<String> messageQueue = new LinkedList<>();

    public void changeStateTo() {
//        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
    }

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
