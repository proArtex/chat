package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Connection;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class RegisterUserCommand implements Executable, Validatable {

    private Server server;
    private String username;

    @Override
    public void execute(SelectionKey key) {
        String message,username;
        Connection connection;

        //already registered case
        if (!isValidUser(key)) {
            connection = server.getClients().getConnectionWith(key);
            username   = connection.getUsername();
            message    = MsgConst.ALREADY_REGISTERED_PREFIX + username;

            connection.getMessageQueue().add(message);
            return;
        }

        connection = server.getNotRegisteredClients().getConnectionWith(key);

        //new one case
        if (server.isFreeUserName(this.username)) {
            message = this.username + MsgConst.USER_SIGN_POSTFIX;
            connection.setUsername(this.username);

            // open session
            if (server.getClients().count() == 0)
                server.openSession();

            server.getNotRegisteredClients().dismiss(key);
            server.getClients().add(key, connection);

            //notify server
            System.out.println(this.username + " sign in. Total: " + server.getClients().count());

            //say it to everyone exclude itself
            for (Map.Entry<SelectionKey, Connection> client: server.getClients().getUsers().entrySet()) {
                //accept msg to itself
                if (client.getKey() == key) {
                    client.getValue().getMessageQueue().add(MsgConst.REGISTER_SUCCESS);
                    continue;
                }

                client.getValue().getMessageQueue().add(message);
            }
        }
        else {
            connection.getMessageQueue().add(MsgConst.REGISTER_FAIL);
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
