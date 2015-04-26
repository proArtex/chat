package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.commands.Executable;

import java.util.LinkedList;
import java.util.List;

public class User {

    private String username;
//    private List<String> inboundMessageQueue = new LinkedList<>();
    private List<Executable> outboundCommandQueue = new LinkedList<>();

//    public void addMessageToQueue(String message) {
//        inboundMessageQueue.add(message);
//    }

    public void addCommandToQueue(Executable command) {
        outboundCommandQueue.add(command);
    }

    public String getUsername() {
        return username;
    }

    public List<Executable> getOutboundCommandQueue() {
        return outboundCommandQueue;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
