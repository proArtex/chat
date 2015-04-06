package me.proartex.test.vitamin.chat.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Session {

    private long sessionId;
    private int messageHistoryLimit = 100;
    private List<Message> messageHistory;

    public Session() {
        messageHistory = new LinkedList<>();
    }

    public void open() {
        sessionId = new Date().getTime();
        System.out.println("Session " + sessionId + " has been opened");
    }

    public void close() {
        System.out.println("Session " + sessionId + " has been closed");
        sessionId = 0;
        clearMessageHistory();
    }

    public boolean isOpened() {
        return sessionId != 0;
    }

    public void noteMessage(Message message) {
        if (messageHistory.size() == messageHistoryLimit)
            messageHistory.remove(0);

        messageHistory.add(message);
    }

    private void clearMessageHistory() {
        messageHistory.clear();
    }

    public String[] getMessageHistory() {
        String[] messages = new String[ messageHistory.size() ];

        for (int i = 0; i < messages.length; i++) {
            messages[i] = messageHistory.get(i).toString();
        }

        return messages;
    }
}
