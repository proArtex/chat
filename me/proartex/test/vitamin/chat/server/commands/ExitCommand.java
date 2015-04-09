package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;

public class ExitCommand implements Executable, Validatable {

    private Server server;
    private SelectionKey key;

    public ExitCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            server.sendMessageToUser(MsgConst.INVALID_EXIT_COMMAND, key);
            return;
        }

        server.cancelKey(key);
    }

    @Override
    public boolean isValidCommand() {
        return server.containsUserWith(key);
    }
}
