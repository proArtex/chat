package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.MsgConst;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver extends Thread {

    private Client client;
    private BufferedReader in;

    public Receiver(Client client) {
        this.client = client;
        this.in = client.getIn();
    }

    @Override
    public void run() {
        String line;

        try {
            while ((line = in.readLine()) != null && !MsgConst.BYE_BYE.equals(line)) {
                System.out.println(line);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            System.out.println(MsgConst.CONNECTION_CLOSED);
//            System.exit(1);
        }
    }
}
