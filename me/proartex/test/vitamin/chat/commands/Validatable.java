package me.proartex.test.vitamin.chat.commands;

import java.nio.channels.SelectionKey;

public interface Validatable {
    /**
     * check user's permissions to send command
     */
    boolean isValidUser(SelectionKey key);
}
