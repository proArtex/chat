package me.proartex.test.vitamin.chat.server;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @since 1.7
 */
public class Message {

    private final byte[] msg;
    private final Date date;

    public Message(Date date, byte[] msg) {
        this.msg = msg;
        this.date = date;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                   .append("[")
                   .append(new SimpleDateFormat("HH:mm:ss").format(date))
                   .append("] ")
                   .append(new String(msg, StandardCharsets.UTF_8)).toString();
    }
}
