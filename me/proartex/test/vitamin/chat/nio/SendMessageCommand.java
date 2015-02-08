package me.proartex.test.vitamin.chat.nio;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.Map;

public class SendMessageCommand implements Executable {

    private final Server server;
    private byte[] message;

    public SendMessageCommand(byte[] message) {
        this.server = null;
        this.message = message;
    }

    public SendMessageCommand(Server server, byte[] message) {
        this.server = server;
        this.message = message;
    }

    @Override
    public void execute(SelectionKey key) {
        for (Map.Entry<SelectionKey, LinkedList<byte[]>> client: server.getClients().entrySet()) {
            client.getValue().add(message);
//            System.out.println("add msg");
        }
    };
}
