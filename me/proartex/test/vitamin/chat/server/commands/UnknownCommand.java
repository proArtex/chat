package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class UnknownCommand implements Executable {

    private ServerCommandHandler handler;
    private SelectionKey key;
    private String context;

    public UnknownCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        String message = MsgConst.UNKNOWN_COMMAND_PREFIX + "'" + context + "'";
        handler.sendMessageToUser(message, key);
    }
}
