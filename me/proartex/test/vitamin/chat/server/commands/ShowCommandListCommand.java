package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.client.commands.SystemMessageCommand;
import me.proartex.test.vitamin.chat.client.commands.InvalidCommand;
import me.proartex.test.vitamin.chat.server.CommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class ShowCommandListCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.COMMANDS;
    private CommandHandler handler;
    private SelectionKey key;

    public ShowCommandListCommand() {}

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        if (!isValidCommand()) {
            Executable invalidCommand = new InvalidCommand(TextConst.COMMAND_LIST_COMMAND);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        String message = handler.getAvailableCommands();
        Executable messageCommand = new SystemMessageCommand(message);
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
