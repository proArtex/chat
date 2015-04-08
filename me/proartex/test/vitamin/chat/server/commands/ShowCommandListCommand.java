package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.Utils;

import java.nio.channels.SelectionKey;

public class ShowCommandListCommand implements Executable, Validatable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        String[] commands = new String[] {
                "/exit - leave the chat",
                "/total - show number of users in chat",
        };

        String message = Utils.implodeStringArray(commands, Utils.LINE_SEPARATOR);
        server.sendMessageToUser(message, key);
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
