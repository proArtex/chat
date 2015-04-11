package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class ExitCommand implements Executable, Validatable {

    private ServerCommandHandler handler;
    private SelectionKey key;

    public ExitCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            handler.sendMessageToUser(MsgConst.INVALID_EXIT_COMMAND, key);
            return;
        }

        handler.dropUser(key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInRegisteredUserGroup(key);
    }
}
