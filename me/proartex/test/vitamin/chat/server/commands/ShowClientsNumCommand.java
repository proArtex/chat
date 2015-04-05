package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class ShowClientsNumCommand implements Executable, Validatable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        int total           = server.getUsers().count();
        String totalMessage = MsgConst.TOTAL_USERS_PREFIX + String.valueOf(total);

        server.getUsers().getUsersMessageQueue(key).add(totalMessage);
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
