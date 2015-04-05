package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.List;

public class ShowMessageHistoryCommand implements Executable, Validatable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        List<String> clientQueue = server.getUsers().getUsersMessageQueue(key);

        for (Message message : server.getMessageHistory()) {
            clientQueue.add(message.toString());
        }
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
