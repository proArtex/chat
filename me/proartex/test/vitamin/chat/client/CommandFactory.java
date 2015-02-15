package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.commands.*;

public class CommandFactory {

    public static Executable getCommand(String message) {

        if (message.length() == 0 || message.charAt(0) != '/')
            return new SendMessageCommand(message.getBytes());

        message = message.substring(1);

        if (message.length() > 9 && "register ".equals(message.substring(0, 9)))
            return new RegisterUserCommand(message.substring(9).getBytes());

        switch (message) {
            case "history":
                return new ShowMessageHistoryCommand();

            case "commands":
                return new ShowCommandListCommand();

            case "total":
                return new ShowClientsNumCommand();

            case "exit":
                return new ExitCommand();

            default:
                return null;
        }
    }
}
