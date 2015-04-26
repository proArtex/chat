package me.proartex.test.vitamin.chat.commands2;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.commands.Serializable;

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
        client.print(username + MsgConst.TAKEN_USERNAME_POSTFIX);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
