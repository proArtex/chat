package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.client.Client;

import java.text.SimpleDateFormat;

public class UserMessageCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.ID_USER_MESSAGE;
    private Client client;
    private String sender;
    private String message;
    private long time;

    public UserMessageCommand() {}

    public UserMessageCommand(String sender, String message, Long time) {
        this.sender = sender;
        this.message = message;
        this.time = time;
    }

    @Override
    public void execute() {
        String message = "[" + formatTime() + "] " + sender + ": " + this.message;
        client.print(message);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    private String formatTime() {
        return new SimpleDateFormat("HH:mm:ss").format(time);
    }
}
