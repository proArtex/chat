package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.CommandHandler;

import java.nio.channels.SelectionKey;

public class ServerCommandHandler implements CommandHandler {

    Server server;

    public ServerCommandHandler(Server server) {
        this.server = server;
    }

    public void exit(SelectionKey key) {
        server.cancelKey(key);
    }
}
