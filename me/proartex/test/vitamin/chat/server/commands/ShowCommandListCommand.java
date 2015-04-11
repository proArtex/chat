package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.Utils;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class ShowCommandListCommand implements Executable, Validatable {

    private ServerCommandHandler handler;
    private SelectionKey key;

    public ShowCommandListCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            handler.sendMessageToUser(MsgConst.INVALID_COMMAND_LIST_COMMAND, key);
            return;
        }

        handler.sendAvailableCommandsToUser(key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInRegisteredUserGroup(key);
    }
}
