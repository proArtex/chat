package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.MsgConst;

import java.io.IOException;

public class Receiver implements Runnable {

    private volatile Client client;

    public Receiver(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            tryToReadAndPrintServerResponseInLoop();
        }
        catch (IOException ignore) {
            //NOP
        }
        finally {
            System.out.println(MsgConst.CONNECTION_CLOSED);
//            System.exit(1);
        }
    }

    private void tryToReadAndPrintServerResponseInLoop() throws IOException {
        String response;

        while ((response = client.readFromSocketChannel()) != null && !MsgConst.BYE_BYE.equals(response)) {
            client.deserializeAndExecute(response);
        }
    }
}
