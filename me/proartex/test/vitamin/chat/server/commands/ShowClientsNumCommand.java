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

        int total           = server.getClients().size();
        String totalMessage = MsgConst.TOTAL_USERS_PREFIX + String.valueOf(total);

        server.getClients().get(key).getMessageQueue().add(totalMessage);
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
        return server.getClients().containsKey(key);
    }
}
