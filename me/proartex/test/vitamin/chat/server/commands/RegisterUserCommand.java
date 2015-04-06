package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.User;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class RegisterUserCommand implements Executable, Validatable {

    private Server server;
    private String username;

    @Override
    public void execute(SelectionKey key) {
        String message,username;
        User user;

        //already registered case
        if (!isValidUser(key)) {
            user = server.getUsers().getUserWith(key);
            username   = user.getUsername();
            message    = MsgConst.ALREADY_REGISTERED_PREFIX + username;

            server.sendMessageToUser(message, key);
            return;
        }



        user = server.getNotRegisteredUsers().getUserWith(key);

        //new one case
        if (server.alreadyContainsUsername(this.username)) {
            server.sendMessageToUser(MsgConst.REGISTER_FAIL, key);
            return;
        }

        message = this.username + MsgConst.USER_SIGN_POSTFIX;
        user.setUsername(this.username);

        server.sendMessageToAllUsers(message);
        server.registerUser(key);
        server.sendMessageToUser(MsgConst.REGISTER_SUCCESS, key);
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    public boolean isValidUser(SelectionKey key) {
        return server.getNotRegisteredUsers().containsUserWith(key);
    }

    @Override
    public boolean isValid() {
        return username != null;
    }
}
