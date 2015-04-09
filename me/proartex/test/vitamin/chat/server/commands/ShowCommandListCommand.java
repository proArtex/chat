package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.Server;
import me.proartex.test.vitamin.chat.Utils;

import java.nio.channels.SelectionKey;

public class ShowCommandListCommand implements Executable, Validatable {

    private Server server;
    private SelectionKey key;

    public ShowCommandListCommand(Server server, SelectionKey key) {
        this.server = server;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            server.sendMessageToUser(MsgConst.INVALID_COMMAND_LIST_COMMAND, key);
            return;
        }

        String[] commands = new String[] {
                "/exit - leave the chat",
                "/total - show number of users in chat",
        };

        String message = Utils.implodeStringArray(commands, Utils.LINE_SEPARATOR);
        server.sendMessageToUser(message, key);
    }

    @Override
    public boolean isValidCommand() {
        return server.getUsers().containsUserWith(key);
    }
}
