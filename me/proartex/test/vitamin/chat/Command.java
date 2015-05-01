package me.proartex.test.vitamin.chat;

public abstract class Command {
    //server
    public static final int ID_UNKNOWN        = 0;
    public static final int ID_MESSAGE        = 1;
    public static final int ID_REGISTER       = 2;
    public static final int ID_HISTORY        = 3;
    public static final int ID_COMMANDS       = 4;
    public static final int ID_TOTAL          = 5;
    public static final int ID_EXIT           = 6;

    //client
    public static final int ID_INVALID        = -1;
    public static final int ID_ACCEPT         = 7;
    public static final int ID_INVALID_NAME   = 8;
    public static final int ID_TAKEN_NAME     = 9;
    public static final int ID_SYSTEM_MESSAGE = 10;
    public static final int ID_USER_MESSAGE   = 11;
}
