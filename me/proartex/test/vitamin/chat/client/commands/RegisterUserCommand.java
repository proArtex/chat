package me.proartex.test.vitamin.chat.client.commands;

import me.proartex.test.vitamin.chat.Command;

public class RegisterUserCommand implements Serializable {

    private static int id = Command.REGISTER;
    private String username;

    public RegisterUserCommand(String username) {
        this.username = username;
    }
}
