package me.proartex.test.vitamin.chat.commands;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.commands2.*;
import me.proartex.test.vitamin.chat.server.ServerCommandHandler;
import me.proartex.test.vitamin.chat.server.User;

import java.nio.channels.SelectionKey;

public class RegisterUserCommand implements Executable, Validatable, ServerCommand, Serializable {

    public static final int id = Command.REGISTER;
    private ServerCommandHandler handler;
    private SelectionKey key;
    private String username;

    public RegisterUserCommand() {}

    public RegisterUserCommand(String username) {
        this.username = username;
    }

    @Override
    public void execute() {
        User user = handler.getUserWith(key);

        //TODO: compact
        if (!isValidCommand()) {
            Executable invalidCommand = new InvalidCommand(MsgConst.INVALID_REGISTER_COMMAND);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        if (!isValidUsername()) {
            Executable invalidCommand = new InvalidUsernameCommand(username);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        if (isTakenUsername()) {
            Executable invalidCommand = new TakenUsernameCommand(username);
            handler.sendCommandToUser(invalidCommand, user);
            return;
        }

        Executable acceptCommand = new AcceptCommand();
        handler.sendCommandToUser(acceptCommand, user);

        String message = username + MsgConst.USER_SIGN_POSTFIX;
        Executable messageCommand = new SystemMessageCommand(message);
        handler.sendCommandToAllRegistered(messageCommand);

        user.setUsername(username);
        handler.registerUser(user, key);
    }

    @Override
    public boolean isValidCommand() {
        return handler.isInNotRegisteredUserGroup(key);
    }

    @Override
    public void setHandler(ServerCommandHandler handler) {
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
