package me.proartex.test.vitamin.chat.server.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.commands.*;
import me.proartex.test.vitamin.chat.server.CommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class RegisterUserCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.ID_REGISTER;
    private CommandHandler handler;
    private SelectionKey key;
    private String username;

    public RegisterUserCommand() {}

    public RegisterUserCommand(String username) {
        this.username = username;
    }

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        if (!isValidCommand()) {
            Serializable invalidCommand = new InvalidCommand(TextConst.REGISTER_COMMAND);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        if (handler.isOverloadedServer()) {
            Serializable shutdownCommand = new ShutdownCommand(TextConst.SERVER_OVERLOADED);
            handler.sendCommandToUser(shutdownCommand, user);
            return;
        }

        if (!isValidUsername()) {
            Serializable invalidCommand = new InvalidUsernameCommand(username);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        if (isTakenUsername()) {
            Serializable invalidCommand = new TakenUsernameCommand(username);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        Serializable acceptCommand = new AcceptCommand();
        handler.sendCommandToUser(acceptCommand, user);

        String message = username + TextConst.USER_SIGN_POSTFIX;
        Serializable messageCommand = new SystemMessageCommand(message);
        handler.sendCommandToAllRegistered(messageCommand);

        user.setUsername(username);
        handler.registerUser(user, key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInNotRegisteredUserGroup(key);
    }

    @Override
    public void setHandler(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }

    private boolean isValidUsername() {
        return username != null && username.matches("[A-z0-9_]+");
    }

    private boolean isTakenUsername() {
        return handler.isTakenUsername(username);
    }
}
