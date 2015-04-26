package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.commands2.SystemMessageCommand;
import me.proartex.test.vitamin.chat.commands2.InvalidCommand;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class ShowMessageHistoryCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.HISTORY;
    private ServerCommandHandler handler;
    private SelectionKey key;

    public ShowMessageHistoryCommand() {}

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        if (!isValidCommand()) {
            Executable invalidCommand = new InvalidCommand(MsgConst.INVALID_MESSAGE_HISTORY_COMMAND);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        String message = handler.getMessageHistory();
        Executable messageCommand = new SystemMessageCommand(message);
        handler.sendCommandToUser(messageCommand, user);
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
