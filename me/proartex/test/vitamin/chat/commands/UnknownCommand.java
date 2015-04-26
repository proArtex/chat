package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.commands2.SystemMessageCommand;
import me.proartex.test.vitamin.chat.commands2.UserMessageCommand;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class UnknownCommand implements Executable, ServerCommand, Serializable {

    public static final int id = Command.UNKNOWN;
    private ServerCommandHandler handler;
    private SelectionKey key;
    private String context;

    public UnknownCommand() {}

    public UnknownCommand(String context) {
        this.context = context;
    }

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        String message = MsgConst.UNKNOWN_COMMAND_PREFIX + "'" + context + "'";
        Executable messageCommand = new SystemMessageCommand(message);
        handler.sendCommandToUser(messageCommand, user);
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
