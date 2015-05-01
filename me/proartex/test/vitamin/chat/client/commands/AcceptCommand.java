package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.Client;

public class AcceptCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.ID_ACCEPT;
    private Client client;

    @Override
    public void execute() {
        client.setRegistered(true);
        client.print(TextConst.REGISTER_SUCCESS);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
