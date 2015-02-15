package me.proartex.test.vitamin.chat.commands;

import java.nio.channels.SelectionKey;

public interface Executable {
    void execute(SelectionKey key);
}
