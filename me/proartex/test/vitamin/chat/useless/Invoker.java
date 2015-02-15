package me.proartex.test.vitamin.chat.useless;

import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.client.CommandFactory;
import me.proartex.test.vitamin.chat.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class Invoker extends Thread {

    private SelectionKey key;
    private LinkedList<byte[]> queue;

    public Invoker(SelectionKey key, LinkedList<byte[]> queue) {
        this.key = key;
        this.queue = queue;
    }

    @Override
    public void run() {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String clientMessage;

        try {
            while ((clientMessage = stdin.readLine()) != null) {
                send(clientMessage);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message) {
        String serializedCommand;

        Executable command = CommandFactory.getCommand(message);
        if (command == null)
            return;

        if ( (serializedCommand = Protocol.serialize(command)) != null ) {
            queue.add(serializedCommand.getBytes()); //TODO: sync
            key.interestOps(SelectionKey.OP_WRITE); //WTF??
            System.out.println(key);
        }
    }
}
