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
            connection = server.getClients().get(key);
            username   = connection.getUsername();
            message    = MsgConst.ALREADY_REGISTERED_PREFIX + username;

            connection.getMessageQueue().add(message);
            return;
        }

        connection = server.getNotRegisteredClients().get(key);

        //new one case
        if (server.isFreeUserName(this.username)) {
            message = this.username + MsgConst.USER_SIGN_POSTFIX;
            connection.setUsername(this.username);

            // open session
            if (server.getClients().size() == 0)
                server.openSession();

            server.getNotRegisteredClients().remove(key);
            server.getClients().put(key, connection);

            //notify server
            System.out.println(this.username + " sign in. Total: " + server.getClients().size());

            //say it to everyone exclude itself
            for (Map.Entry<SelectionKey, Connection> client: server.getClients().entrySet()) {
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
        return server.getNotRegisteredClients().containsKey(key);
    }

    @Override
    public boolean isValid() {
        return username != null;
    }
}
