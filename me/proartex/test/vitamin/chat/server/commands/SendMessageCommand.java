package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.User;
import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.server.UserGroup;

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
        UserGroup users = server.getUsers();
        User user = users.getUserWith(key);
        String username  = user.getUsername();
        Message message  = new Message(date, username + ": " + this.message);

        server.sendMessageToAllUsers(message.toString());
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
