package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.List;

public class ShowCommandListCommand implements Executable, Validatable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {

        System.out.println("123");
        if (!isValidUser(key))
            return;

        List<String> clientQueue = server.getUsers().getUsersMessageQueue(key);

        String[] commands = new String[] {
                "/exit - leave the chat",
                "/total - show number of users in chat",
        };

        Collections.addAll(clientQueue, commands);
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public boolean isValidUser(SelectionKey key) {
        return server.getUsers().containsUserWith(key);
    }
}
