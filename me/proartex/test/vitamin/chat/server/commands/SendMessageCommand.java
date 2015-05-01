package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.commands.InvalidCommand;
import me.proartex.test.vitamin.chat.client.commands.UserMessageCommand;
import me.proartex.test.vitamin.chat.server.CommandHandler;
import me.proartex.test.vitamin.chat.server.HistoryMessage;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;
import java.util.Date;

public class SendMessageCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.ID_MESSAGE;
    private CommandHandler handler;
    private SelectionKey key;
    private String message;

    public SendMessageCommand() {}

    public SendMessageCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        Date now = new Date();
        User user = handler.getUserWith(key);
        String username = user.getUsername();

        if (!isValidCommand()) {
            Serializable clientCommand = new InvalidCommand(TextConst.MESSAGE_COMMAND);
            handler.sendCommandToUser(clientCommand, user);
            return;
        }

        HistoryMessage historyMessage = new HistoryMessage(username, message, now);
        handler.addMessageToHistory(historyMessage);

        Serializable messageCommand = new UserMessageCommand(username, message, now.getTime());
        handler.sendCommandToAllRegistered(messageCommand);
    }

    @Override
    public boolean isValidCommand() {
        return message != null && handler.isInRegisteredUserGroup(key);
    }

    @Override
    public void setHandler(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }
}
