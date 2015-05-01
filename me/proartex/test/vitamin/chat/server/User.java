package me.proartex.test.vitamin.chat.server;

import java.util.LinkedList;
import java.util.List;

public class User {

    private String username;
    private List<String> outboundCommandQueue = new LinkedList<>();

    public void addCommandToQueue(String command) {
        outboundCommandQueue.add(command);
    }

    public String getUsername() {
        return username;
    }

    public List<String> getOutboundCommandQueue() {
        return outboundCommandQueue;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
