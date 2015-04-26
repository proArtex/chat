package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.server.ServerCommandHandler;

import java.nio.channels.SelectionKey;

public interface ServerCommand {
    void setHandler(ServerCommandHandler handler);
    void setSelectionKey(SelectionKey key);
}
