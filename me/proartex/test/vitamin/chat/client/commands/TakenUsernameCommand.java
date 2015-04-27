package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;

public class TakenUsernameCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.TAKEN_NAME;
    private Client client;
    private String username;

    public TakenUsernameCommand() {}

    public TakenUsernameCommand(String username) {
        this.username = username;
    }

    @Override
    public void execute() {
        client.print(username + TextConst.TAKEN_USERNAME_POSTFIX);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
