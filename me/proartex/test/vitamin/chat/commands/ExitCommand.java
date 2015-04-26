package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.commands2.InvalidCommand;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class ExitCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.EXIT;
    private ServerCommandHandler handler;
    private SelectionKey key;

    public ExitCommand() {}

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        if (!isValidCommand()) {
            Executable invalidCommand = new InvalidCommand(MsgConst.INVALID_EXIT_COMMAND);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        handler.dropUserWith(key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInRegisteredUserGroup(key);
    }

    @Override
    public void setHandler(ServerCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }
}
