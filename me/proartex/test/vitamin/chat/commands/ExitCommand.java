package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class ExitCommand implements Executable, Validatable {

    private final Server server;

    public ExitCommand() {
        this.server = null;
    }

    public ExitCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        server.closeConnection(key, null);
    }

    @Override
    public boolean isValidUser(SelectionKey key) {
        return server.getClients().containsKey(key);
    }
}
