package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class UnknownCommand implements Executable {

    private Server server;
    private SelectionKey key;
    private String context;

    public UnknownCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        String message = MsgConst.UNKNOWN_COMMAND_PREFIX + "'" + context + "'";
        server.sendMessageToUser(message, key);
    }
}
