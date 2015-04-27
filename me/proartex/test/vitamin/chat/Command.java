package me.proartex.test.vitamin.chat;

public abstract class Command {
    //server
    public static final int UNKNOWN        = 0;
    public static final int MESSAGE        = 1;
    public static final int REGISTER       = 2;
    public static final int HISTORY        = 3;
    public static final int COMMANDS       = 4;
    public static final int TOTAL          = 5;
    public static final int EXIT           = 6;

    //client
    public static final int INVALID        = -1;
    public static final int ACCEPT         = 7;
    public static final int INVALID_NAME   = 8;
    public static final int TAKEN_NAME     = 9;
    public static final int SYSTEM_MESSAGE = 10;
    public static final int USER_MESSAGE   = 11;
}
