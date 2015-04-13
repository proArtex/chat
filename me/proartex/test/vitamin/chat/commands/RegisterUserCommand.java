package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public class RegisterUserCommand implements Executable, Validatable, Serializable {

    public static final int id = Command.REGISTER;
    private ServerCommandHandler handler;
    private SelectionKey key;
    private String username;

    public RegisterUserCommand(String username) {
        this.username = username;
    }

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
