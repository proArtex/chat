package me.proartex.test.vitamin.chat.nio;

import java.nio.channels.SelectionKey;

public interface Executable {
    void execute(SelectionKey key);
}
