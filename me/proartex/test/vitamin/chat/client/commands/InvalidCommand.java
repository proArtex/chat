package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.Client;

public class InvalidCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.ID_INVALID;
    private Client client;
    private String command;

    public InvalidCommand() {}

    public InvalidCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute() {
        //TODO '' if null?
        String message = TextConst.INVALID_COMMAND_PREFIX + "'" + command + "'";
        client.print(message);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
