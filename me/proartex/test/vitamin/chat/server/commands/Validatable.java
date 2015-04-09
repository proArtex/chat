package me.proartex.test.vitamin.chat.server.commands;

import java.nio.channels.SelectionKey;

public interface Validatable {
    boolean isValid(SelectionKey key);
}
