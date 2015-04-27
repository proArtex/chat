package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.server.CommandHandler;

import java.nio.channels.SelectionKey;

public interface ServerCommand {
    void setHandler(CommandHandler handler);
    void setSelectionKey(SelectionKey key);
}
