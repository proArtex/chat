package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.Executor;

import java.nio.channels.SelectionKey;

public class CommandExecutor implements Executor {

    Server server;

    public CommandExecutor(Server server) {
        this.server = server;
    }

    public void exit(SelectionKey key) {
        server.cancelKey(key);
    }
}
