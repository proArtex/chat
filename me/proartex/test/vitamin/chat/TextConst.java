package me.proartex.test.vitamin.chat;

public abstract class TextConst {
    //client
    public static final String ASK_FOR_USERNAME         = "Type username please:";
    public static final String REGISTER_SUCCESS         = "You have joined the chat. Use '/commands' to look at chat commands";
    public static final String CONNECTION_FAIL          = "Failed to connect the server";
    public static final String CONNECTION_CLOSED        = "Connection closed";
    public static final String INVALID_USERNAME_POSTFIX = " is incorrect, try to use this rules: ...";
    public static final String TAKEN_USERNAME_POSTFIX   = " is already in use, try another one:";
    public static final String INVALID_COMMAND_PREFIX   = "Invalid command ";

    //server
    public static final String SERVER_OVERLOADED        = "Server is overloaded, try again later";
    public static final String TOTAL_USERS_PREFIX       = "Total users: ";
    public static final String UNKNOWN_COMMAND_PREFIX   = "Unknown command ";
    public static final String USER_SIGN_POSTFIX        = " has joined the chat";
    public static final String USER_LEFT_POSTFIX        = " has left the chat";

    //TODO: Another class
    public static final String EXIT_COMMAND             = "exit";
    public static final String REGISTER_COMMAND         = "register";
    public static final String MESSAGE_COMMAND          = "message";
    public static final String CLIENT_NUM_COMMAND       = "total";
    public static final String COMMAND_LIST_COMMAND     = "commands";
    public static final String MESSAGE_HISTORY_COMMAND  = "history";
}
