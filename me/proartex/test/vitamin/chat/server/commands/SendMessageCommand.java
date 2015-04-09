package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.User;
import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.server.UserGroup;

import java.nio.channels.SelectionKey;
import java.util.Date;
import java.util.Map;

public class SendMessageCommand implements Executable, Validatable {

    private Server server;
    private SelectionKey key;
    private String message;

    public SendMessageCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            server.sendMessageToUser(MsgConst.INVALID_MESSAGE_COMMAND, key);
            return;
        }

        //msg with server handler time
        Date date = new Date();
        UserGroup users = server.getUsers();
        User user = users.getUserWith(key);
        String username = user.getUsername();
        Message message = new Message(date, username + ": " + this.message);

        server.sendMessageToAllUsers(message.toString());
        server.addMessageToHistory(message);
    }

    @Override
    public boolean isValidCommand() {
        return message != null && server.getUsers().containsUserWith(key);
    }
}
