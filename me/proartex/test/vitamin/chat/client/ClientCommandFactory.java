package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.server.commands.*;

public class ClientCommandFactory {

    public static Serializable getInstanceFor(String context) {
        if (isMessageCommand(context))
            return new SendMessageCommand(context);

        if (isRegisterCommand(context)) {
            context = cutSubstringFromBeginningOfContext("/register ", context);
            return new RegisterUserCommand(context);
        }

        context = cutSubstringFromBeginningOfContext("/", context);
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

    private static boolean isMessageCommand(String context) {
        return context.length() == 0 || context.charAt(0) != '/';
    }

    private static boolean isRegisterCommand(String context) {
        return context.length() >= 10 && "/register ".equals(context.substring(0, 10));
    }

    private static String cutSubstringFromBeginningOfContext(String context, String message) {
        int index = context.length();
        return message.substring(index);
    }
}
