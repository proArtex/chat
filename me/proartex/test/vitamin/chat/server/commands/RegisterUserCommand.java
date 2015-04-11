package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class RegisterUserCommand implements Executable, Validatable {

    private ServerCommandHandler handler;
    private SelectionKey key;
    private String username;

    public RegisterUserCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            handler.sendMessageToUser(MsgConst.INVALID_REGISTER_COMMAND, key);
            return;
        }

        handler.registerUser(key, username);
    }

    @Override
    public boolean isValidCommand() {
        return username != null && handler.isInNotRegisteredUserGroup(key);
    }
}
