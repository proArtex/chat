package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.User;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class RegisterUserCommand implements Executable, Validatable {

    private Server server;
    private String username;

    @Override
    public void execute(SelectionKey key) {
        String message,username;
        User user;

        //already registered case
        if (!isValidUser(key)) {
            user = server.getUsers().getConnectionWith(key);
            username   = user.getUsername();
            message    = MsgConst.ALREADY_REGISTERED_PREFIX + username;

            user.getMessageQueue().add(message);
            return;
        }

        user = server.getNotRegisteredClients().getConnectionWith(key);

        //new one case
        if (server.isFreeUserName(this.username)) {
            message = this.username + MsgConst.USER_SIGN_POSTFIX;
            user.setUsername(this.username);

            server.getNotRegisteredClients().dismiss(key);
            server.getUsers().add(key, user);

            //notify server
            System.out.println(this.username + " sign in. Total: " + server.getUsers().count());

            //say it to everyone exclude itself
            for (Map.Entry<SelectionKey, User> client: server.getUsers().getUsers().entrySet()) {
                //accept msg to itself
                if (client.getKey() == key) {
                    client.getValue().getMessageQueue().add(MsgConst.REGISTER_SUCCESS);
                    continue;
                }

                client.getValue().getMessageQueue().add(message);
            }
        }
        else {
            user.getMessageQueue().add(MsgConst.REGISTER_FAIL);
        }
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    public boolean isValidUser(SelectionKey key) {
        return server.getNotRegisteredClients().containsUserWith(key);
    }

    @Override
    public boolean isValid() {
        return username != null;
    }
}
