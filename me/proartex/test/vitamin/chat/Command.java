package me.proartex.test.vitamin.chat;

public abstract class Command {
    //server
    public static final int INVALID        = -1;
    public static final int UNKNOWN        = 0;
    public final static int MESSAGE        = 1;
    public final static int REGISTER       = 2;
    public final static int HISTORY        = 3;
    public final static int COMMANDS       = 4;
    public final static int TOTAL          = 5;
    public final static int EXIT           = 6;

    //client
    public final static int ACCEPT         = 7;
    public final static int INVALID_NAME   = 8;
    public final static int TAKEN_NAME     = 9;
    public final static int SYSTEM_MESSAGE = 10;
    public final static int USER_MESSAGE   = 11;

}
