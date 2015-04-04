package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.Connection;
import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class SendMessageCommand implements Executable, Validatable {

    private Server server;
    private String message;

    @Override
    public void execute(SelectionKey key) {
        if (!isValidUser(key))
            return;

        //msg with server handler time
        Date date        = new Date();
        String userName  = server.getClients().get(key).getUsername();
        Message message  = new Message(date, concatMessage(userName, this.message.getBytes()));

        for (Map.Entry<SelectionKey, Connection> client: server.getClients().entrySet()) {
            client.getValue().getMessageQueue().add(message.toString());
        }

        //add into history list
        LinkedList<Message> history = server.getMessageHistory();

        if (history.size() == server.messageHistoryLimit)
            history.remove(0);

        server.getMessageHistory().add(message);
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public boolean isValidUser(SelectionKey key) {
        return server.getClients().containsKey(key);
    }

    private byte[] concatMessage(String userName, byte[] message) {
        userName += ": ";
        byte[] resultMessage = new byte[userName.length() + message.length];

        System.arraycopy(userName.getBytes(), 0, resultMessage, 0, userName.length());
        System.arraycopy(message, 0, resultMessage, userName.length(), message.length);

        return resultMessage;
    }
}
