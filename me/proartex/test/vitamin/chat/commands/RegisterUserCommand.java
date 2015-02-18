package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.server.Connection;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class RegisterUserCommand implements Executable, Validatable {

    private final Server server;
    private byte[] userName;


    public RegisterUserCommand(byte[] userName) {
        this.server = null;
        this.userName = userName;
    }

    public RegisterUserCommand(Server server, byte[] userName) {
        this.server = server;
        this.userName = userName;
    }

    @Override
    public void execute(SelectionKey key) {
        String message,userName;
        Connection connection;

        //already registered case
        if (!isValidUser(key)) {
            connection = server.getClients().get(key);
            userName   = connection.getUserName();
            message    = MsgConst.ALREADY_REGISTERED_PREFIX + userName;

            connection.getMessageQueue().add(message.getBytes());
            return;
        }

        userName   = new String(this.userName);
        connection = server.getNotRegisteredClients().get(key);

        //new one case
        if (server.isFreeUserName(userName)) {
            message = userName + MsgConst.USER_SIGN_POSTFIX;
            connection.setUserName(userName);

            // open session
            if (server.getClients().size() == 0)
                server.openSession();

            server.getNotRegisteredClients().remove(key);
            server.getClients().put(key, connection);

            //notify server
            System.out.println(userName + " sign in. Total: " + server.getClients().size());

            //say it to everyone exclude itself
            for (Map.Entry<SelectionKey, Connection> client: server.getClients().entrySet()) {
                //accept msg to itself
                if (client.getKey() == key) {
                    client.getValue().getMessageQueue().add(MsgConst.REGISTER_SUCCESS.getBytes());
                    continue;
                }

                client.getValue().getMessageQueue().add(message.getBytes());
            }
        }
        else {
            connection.getMessageQueue().add(MsgConst.REGISTER_FAIL.getBytes());
        }
    }

    @Override
    public boolean isValidUser(SelectionKey key) {
        return server.getNotRegisteredClients().containsKey(key);
    }
}
