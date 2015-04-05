package me.proartex.test.vitamin.chat.server;

import java.util.Date;

public class Session {

    private long sessionId;

    public void open() {
        sessionId = new Date().getTime();
        System.out.println("Session " + sessionId + " has been opened");
    }

    public void close() {
        System.out.println("Session " + sessionId + " has been closed");
        sessionId = 0;
    }

    public boolean isOpened() {
        return sessionId != 0;
    }
}
