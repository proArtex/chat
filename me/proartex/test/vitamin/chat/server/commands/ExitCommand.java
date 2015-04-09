package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class ExitCommand implements Executable, Validatable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {
        if (!isValid(key)) {
            //TODO: send invalid back
            String message = "bla bla bla is invalid";
            server.sendMessageToUser(message, key);
            return;
        }

        server.cancelKey(key);
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public boolean isValid(SelectionKey key) {
        return server.containsUserWith(key);
    }
}
