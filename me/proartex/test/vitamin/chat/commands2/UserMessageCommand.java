package me.proartex.test.vitamin.chat.commands2;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.commands.Serializable;

public class UserMessageCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.USER_MESSAGE;
    private Client client;
    private String sender;
    private String message;

    public UserMessageCommand() {}

    public UserMessageCommand(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void execute() {
        client.print(sender + ": " + message);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
