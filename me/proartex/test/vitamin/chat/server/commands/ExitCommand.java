package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class ExitCommand implements Executable, Validatable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        server.cancelKey(key);
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public boolean isValid() {
//        return server.containsUserWith(key);
        return true;
    }

    public boolean isValidUser(SelectionKey key) {
        return server.containsUserWith(key);
    }
}
