package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.User;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class RegisterUserCommand implements Executable, Validatable {

    private Server server;
    private SelectionKey key;
    private String username;

    public RegisterUserCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            server.sendMessageToUser(MsgConst.INVALID_REGISTER_COMMAND, key);
            return;
        }

        if (server.alreadyContainsUsername(username)) {
            server.sendMessageToUser(MsgConst.REGISTER_FAIL, key);
            return;
        }

        String message = username + MsgConst.USER_SIGN_POSTFIX;
        User user = server.getNotRegisteredUsers().getUserWith(key);
        user.setUsername(username);

        server.sendMessageToAllUsers(message);
        server.registerUser(key);
        server.sendMessageToUser(MsgConst.REGISTER_SUCCESS, key);
    }

    @Override
    public boolean isValidCommand() {
        return username != null && server.getNotRegisteredUsers().containsUserWith(key);
    }
}
