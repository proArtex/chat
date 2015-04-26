package me.proartex.test.vitamin.chat;

public abstract class MsgConst {
    //TODO: divide onto server/client
    public static final String BYE_BYE                         = "[SYSTEM] BYE";

    public static final String REGISTER_SUCCESS                = "You have joined the chat. Use '/commands' to look at chat commands";
    public static final String INVALID_USERNAME_POSTFIX        = " is incorrect, try to use this rules: ...";
    public static final String TAKEN_USERNAME_POSTFIX          = " is already in use, try another one:";

    public static final String USER_SIGN_POSTFIX               = " has joined the chat";
    public static final String USER_LEFT_POSTFIX               = " has left the chat";
    public static final String TOTAL_USERS_PREFIX              = "Total users: ";

    public static final String ASK_FOR_USERNAME                = "Type username please:";
    public static final String UNKNOWN_COMMAND_PREFIX          = "Unknown command ";
    public static final String SEND_FAIL_PREFIX                = "There was a problem sending the command ";

    public static final String CONNECTION_FAIL                 = "Failed to connect the server";
    public static final String CONNECTION_CLOSED               = "Connection closed";
    public static final String UNREACHABLE_HOST_PREFIX         = "Can't connect to the host ";

    public static final String INVALID_EXIT_COMMAND            = "Invalid 'exit' command";
    public static final String INVALID_REGISTER_COMMAND        = "Invalid 'register' command";
    public static final String INVALID_MESSAGE_COMMAND         = "Invalid 'message' command";
    public static final String INVALID_CLIENT_NUM_COMMAND      = "Invalid 'total' command";
    public static final String INVALID_COMMAND_LIST_COMMAND    = "Invalid 'commands' command";
    public static final String INVALID_MESSAGE_HISTORY_COMMAND = "Invalid 'history' command";
}
