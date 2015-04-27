package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Utils;
import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.protocol.Protocol;

public class SystemMessageCommand implements Executable, ClientCommand, Serializable {

    public static final int id = Command.SYSTEM_MESSAGE;
    private Client client;
    private String message;

    public SystemMessageCommand() {}

    public SystemMessageCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        String[] messages = Utils.explodeString(message, Protocol.RESPONSE_DELIMITER);
        for (String message : messages) {
            if (!message.isEmpty())
                client.print(message);
        }
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}

