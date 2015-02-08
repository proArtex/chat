package me.proartex.test.vitamin.chat.nio;

import java.nio.channels.SelectionKey;

public class ShowClientsNumCommand implements Executable {

    private final Server server;

    public ShowClientsNumCommand() {
        this.server = null;
    }

    public ShowClientsNumCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(SelectionKey key) {
        int total = server.getClients().size();
        server.getClients().get(key).add(String.valueOf(total).getBytes());
//        System.out.println("add msg");
    }
}
