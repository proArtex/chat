package me.proartex.test.vitamin.chat.nio;

public class CommandFactory {

    static Executable getCommand(String message) {


        if (message.length() == 0 || message.charAt(0) != '/')
            return new SendMessageCommand(message.getBytes());

        switch (message.substring(1)) {
//            case "exit":
//                return CODE_EXIT;
//
//            case "commands":
//                return CODE_SHOW_COMMANDS;

            case "total":
                return new ShowClientsNumCommand();

//            case "name":
//                return CODE_USER_NAME;

            default:
                return null;
        }
    }
}
