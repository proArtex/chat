package me.proartex.test.vitamin.chat.server;

public class Protocol {

    protected static int CODE_EXIT            = -1;
    protected static int CODE_MESSAGE         = 0;
    protected static int CODE_SHOW_COMMANDS   = 1;
    protected static int CODE_UNKNOWN_COMMAND = 2;
    protected static int CODE_USER_COUNT      = 3;
    protected static int CODE_USER_NAME       = 4;

    protected static int handle(String message) {
        if (message.length() == 0 || message.charAt(0) != '/')
            return CODE_MESSAGE;

        switch (message.substring(1)) {
            case "exit":
                return CODE_EXIT;

            case "commands":
                return CODE_SHOW_COMMANDS;

            case "total":
                return CODE_USER_COUNT;

            case "name":
                return CODE_USER_NAME;

            default:
                return CODE_UNKNOWN_COMMAND;
        }
    }

    protected static String[] getAvailableCommands() {
        //TODO: refactor hardcode?
        return new String[] {
                "/exit - leave the chat",
                "/total - show number of users in chat",
                "/name - show my username",
        };
    }
}
