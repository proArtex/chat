package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class ShowCommandListCommand implements Executable, Validatable {

    private final Server server;

    public ShowCommandListCommand() {
        this.server = null;
    }

    public ShowCommandListCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        LinkedList<byte[]> clientQueue = server.getClients().get(key).getMessageQueue();

        String[] commands = new String[] {
            "/exit - leave the chat",
            "/total - show number of users in chat",
        };

        for (String command : commands) {
            clientQueue.add(command.getBytes());
        }
    }

    @Override
    public boolean isValidUser(SelectionKey key) {
        return server.getClients().containsKey(key);
    }
}
