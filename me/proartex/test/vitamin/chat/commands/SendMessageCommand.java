package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.commands2.UserMessageCommand;
import me.proartex.test.vitamin.chat.commands2.InvalidCommand;
import me.proartex.test.vitamin.chat.server.*;

import java.nio.channels.SelectionKey;

public class SendMessageCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.MESSAGE;
    private ServerCommandHandler handler;
    private SelectionKey key;
    private String message;

    public SendMessageCommand() {}

    public SendMessageCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        User user = handler.getUserWith(key);
        String username = user.getUsername();

        if (!isValidCommand()) {
            Executable clientCommand = new InvalidCommand(MsgConst.INVALID_MESSAGE_COMMAND);
            handler.sendCommandToUser(clientCommand, user);
            return;
        }

        HistoryMessage historyMessage = new HistoryMessage(username, message);
        handler.addMessageToHistory(historyMessage);

        Executable messageCommand = new UserMessageCommand(username, message);
        handler.sendCommandToAllRegistered(messageCommand);
    }

    @Override
    public boolean isValidCommand() {
        return message != null && handler.isInRegisteredUserGroup(key);
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
