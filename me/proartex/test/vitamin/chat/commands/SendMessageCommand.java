package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.server.*;

import java.nio.channels.SelectionKey;

public class SendMessageCommand implements Executable, Validatable, Serializable {

    public static final int id = Command.MESSAGE;
    private ServerCommandHandler handler;
    private SelectionKey key;
    private String message;

    public SendMessageCommand(String message) {
        this.message = message;
    }

    public SendMessageCommand(ServerCommandHandler handler, SelectionKey key) {
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void execute() {
        if (!isValidCommand()) {
            handler.sendMessageToUser(MsgConst.INVALID_MESSAGE_COMMAND, key);
            return;
        }

        handler.sendUserMessageToAllRegistered(key, message);
    }

    @Override
    public boolean isValidCommand() {
        return message != null && handler.isInRegisteredUserGroup(key);
    }
}
