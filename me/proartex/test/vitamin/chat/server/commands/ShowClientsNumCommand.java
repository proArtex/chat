package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.commands.InvalidCommand;
import me.proartex.test.vitamin.chat.client.commands.SystemMessageCommand;
import me.proartex.test.vitamin.chat.server.CommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class ShowClientsNumCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.ID_TOTAL;
    private CommandHandler handler;
    private SelectionKey key;

    public ShowClientsNumCommand() {}

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        if (!isValidCommand()) {
            Serializable invalidCommand = new InvalidCommand(TextConst.CLIENT_NUM_COMMAND);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        int total = handler.getRegisteredUsersCount();
        String message = TextConst.TOTAL_USERS_PREFIX + String.valueOf(total);
        Serializable messageCommand = new SystemMessageCommand(message);
        handler.sendCommandToUser(messageCommand, user);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInRegisteredUserGroup(key);
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
