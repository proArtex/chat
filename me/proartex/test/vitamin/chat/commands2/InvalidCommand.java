package me.proartex.test.vitamin.chat.commands2;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.commands.Serializable;

public class InvalidCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.INVALID;
    private Client client;
    private String message;

    public InvalidCommand() {}

    public InvalidCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        //TODO '' if null?
        client.print(message);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
