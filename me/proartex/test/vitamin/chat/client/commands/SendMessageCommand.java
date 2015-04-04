package me.proartex.test.vitamin.chat.client.commands;


import me.proartex.test.vitamin.chat.Command;

public class SendMessageCommand implements Serializable {

    private static int id = Command.MESSAGE;
    private String message;
//    private final int[] a = {1,2,3};

    public SendMessageCommand(String message) {
        this.message = message;
    }
}
