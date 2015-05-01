package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.commands.SystemMessageCommand;
import me.proartex.test.vitamin.chat.server.CommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class UnknownCommand implements Executable, ServerCommand, Serializable {

    public static final int id = Command.ID_UNKNOWN;
    private CommandHandler handler;
    private SelectionKey key;
    private String context;

    public UnknownCommand() {}

    public UnknownCommand(String context) {
        this.context = context;
    }

    @Override
    public void execute() {
        User user = handler.getUserWith(key);
        String message = TextConst.UNKNOWN_COMMAND_PREFIX + "'" + context + "'";

        Serializable messageCommand = new SystemMessageCommand(message);
        handler.sendCommandToUser(messageCommand, user);
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
