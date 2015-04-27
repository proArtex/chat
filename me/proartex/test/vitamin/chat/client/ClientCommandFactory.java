package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.server.commands.*;

public class ClientCommandFactory {

    public static Serializable getInstanceFor(String context) {
        if (isMessageCommand(context))
            return new SendMessageCommand(context);

        if (isRegisterCommand(context)) {
            context = removeContextFromBeginningOfMessage("/register", context);
            return new RegisterUserCommand(context);
        }

        context = removeContextFromBeginningOfMessage("/", context);
        switch (context) {
            case "history":
                return new ShowMessageHistoryCommand();

            case "commands":
                return new ShowCommandListCommand();

            case "total":
                return new ShowClientsNumCommand();

            case "exit":
                return new ExitCommand();

            default:
                return new UnknownCommand(context);
        }
    }

    private static boolean isMessageCommand(String message) {
        return message.length() == 0 || message.charAt(0) != '/';
    }

    private static boolean isRegisterCommand(String message) {
        return message.length() > 9 && "/register".equals(message.substring(0, 9));
    }

    private static String removeContextFromBeginningOfMessage(String context, String message) {
        int index = context.length();
        return message.substring(index).trim();
    }
}
