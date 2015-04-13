package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class ShowClientsNumCommand implements Executable, Validatable, Serializable {

    public static final int id = Command.TOTAL;
    private ServerCommandHandler handler;
    private SelectionKey key;

    public ShowClientsNumCommand() {}

    public ShowClientsNumCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            handler.sendMessageToUser(MsgConst.INVALID_CLIENT_NUM_COMMAND, key);
            return;
        }

        handler.sendRegisteredCountToUser(key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInRegisteredUserGroup(key);
    }
}
