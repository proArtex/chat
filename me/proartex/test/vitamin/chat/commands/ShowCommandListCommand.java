package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class ShowCommandListCommand implements Executable, Validatable, Serializable {

    public static final int id = Command.COMMANDS;
    private ServerCommandHandler handler;
    private SelectionKey key;

    public ShowCommandListCommand() {}

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
