package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Message;
import me.proartex.test.vitamin.chat.server.Server;

import java.nio.channels.SelectionKey;
import java.util.List;

public class ShowMessageHistoryCommand implements Executable, Validatable {

    private Server server;
    private SelectionKey key;

    public ShowMessageHistoryCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            server.sendMessageToUser(MsgConst.INVALID_MESSAGE_HISTORY_COMMAND, key);
            return;
        }

        //TODO: solve empty case
        String message = server.getMessageHistory();
        server.sendMessageToUser(message, key);
    }

    @Override
    public boolean isValidCommand() {
        return server.getUsers().containsUserWith(key);
    }
}
