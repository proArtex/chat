package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class ShowClientsNumCommand implements Executable, Validatable {

    private Server server;
    private SelectionKey key;

    public ShowClientsNumCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            server.sendMessageToUser(MsgConst.INVALID_CLIENT_NUM_COMMAND, key);
            return;
        }

        int total = server.getUsers().count();
        String message = MsgConst.TOTAL_USERS_PREFIX + String.valueOf(total);
        server.sendMessageToUser(message, key);
    }

    @Override
    public boolean isValidCommand() {
        return server.getUsers().containsUserWith(key);
    }
}
