package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class ShowMessageHistoryCommand implements Executable, Validatable {

    private final Server server;

    public ShowMessageHistoryCommand() {
        this.server = null;
    }

    public ShowMessageHistoryCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        LinkedList<byte[]> clientQueue = server.getClients().get(key).getMessageQueue();

        for (Message message : server.getMessageHistory()) {
            clientQueue.add(message.toString().getBytes());
        }
    }

    @Override
    public boolean isValidUser(SelectionKey key) {
        return server.getClients().containsKey(key);
    }
}
