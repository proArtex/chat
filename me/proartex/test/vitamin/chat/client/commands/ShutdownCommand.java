package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.client.Client;

public class ShutdownCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.ID_SHUTDOWN;
    private Client client;
    private String message;

    public ShutdownCommand() {}

    public ShutdownCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        client.print(message);
        client.stop();
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
