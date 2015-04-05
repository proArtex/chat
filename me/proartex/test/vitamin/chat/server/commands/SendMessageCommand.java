package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.User;
import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Date;
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
        String username  = server.getUsers().getNameOfUserWith(key);
        Message message  = new Message(date, username + ": " + this.message);

        for (Map.Entry<SelectionKey, User> client: server.getUsers().getUsers().entrySet()) {
            client.getValue().getMessageQueue().add(message.toString());
        }

        server.addMessageToHistory(message);
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
        return server.getUsers().containsUserWith(key);
    }
}
