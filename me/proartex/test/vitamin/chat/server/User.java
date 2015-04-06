package me.proartex.test.vitamin.chat.server;

import java.util.LinkedList;

public class User {

//    public static int OP_READ  = SelectionKey.OP_READ;
//    public static int OP_WRITE = SelectionKey.OP_WRITE;
//    private SelectionKey key;
    private String username;
    private LinkedList<String> inboundMessageQueue = new LinkedList<>();

    public void addMessageToQueue(String message) {
        inboundMessageQueue.add(message);
    }


    public void changeStateTo() {
//        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public String getUsername() {
        return username;
    }

    public LinkedList<String> getInboundMessageQueue() {
        return inboundMessageQueue;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
