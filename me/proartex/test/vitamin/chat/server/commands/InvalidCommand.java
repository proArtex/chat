package me.proartex.test.vitamin.chat.server.commands;


import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class InvalidCommand implements Executable {

    private Server server;

    @Override
    public void execute(SelectionKey key) {

    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }
}
