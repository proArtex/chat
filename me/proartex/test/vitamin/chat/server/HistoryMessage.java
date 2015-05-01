package me.proartex.test.vitamin.chat.server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryMessage {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final Date date;
    private final String sender;
    private final String message;

    public HistoryMessage(String sender, String message, Date date) {
        this.sender = sender;
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        return "[" + dateFormat.format(date) + "] " + sender + ": " + message;
    }
}
