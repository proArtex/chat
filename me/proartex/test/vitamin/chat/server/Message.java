package me.proartex.test.vitamin.chat.server;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @since 1.7
 */
public class Message {

    private final String message;
    private final Date date;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Message(Date date, String message) {
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        return "[" + dateFormat.format(date) + "] " + message;
    }
}
