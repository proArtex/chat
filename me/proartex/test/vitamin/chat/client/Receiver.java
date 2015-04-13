package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.MsgConst;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver extends Thread {

    private BufferedReader inStream;

    Receiver( BufferedReader inStream) {
        this.inStream = inStream;
    }

    @Override
    public void run() {
        try {
            tryToReadAndPrintMessageFromServer();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println(MsgConst.CONNECTION_CLOSED);
//            System.exit(1);
        }
    }

    void setIn(BufferedReader in) {
        this.inStream = in;
    }

    private void tryToReadAndPrintMessageFromServer() throws IOException {
        String line;

        while ((line = inStream.readLine()) != null && !MsgConst.BYE_BYE.equals(line)) {
            System.out.println(line);
        }
    }
}
