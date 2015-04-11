package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;
import java.util.List;

public class ShowMessageHistoryCommand implements Executable, Validatable {

    private ServerCommandHandler handler;
    private SelectionKey key;

    public ShowMessageHistoryCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            handler.sendMessageToUser(MsgConst.INVALID_MESSAGE_HISTORY_COMMAND, key);
            return;
        }

        handler.sendMessageHistoryToUser(key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInRegisteredUserGroup(key);
    }
}
