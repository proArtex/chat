package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class UnknownCommand implements Executable {

    private Server server;
    private String context;

    @Override
    public void execute(SelectionKey key) {
        String message = MsgConst.UNKNOWN_COMMAND_PREFIX + "'" + context + "'";
        server.sendMessageToUser(message, key);
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }
}
