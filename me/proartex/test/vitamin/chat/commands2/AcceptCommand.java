package me.proartex.test.vitamin.chat.commands2;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.commands.Serializable;

public class AcceptCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.ACCEPT;
    private Client client;

    @Override
    public void execute() {
        client.setRegistered(true);
        client.print(MsgConst.REGISTER_SUCCESS);
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
