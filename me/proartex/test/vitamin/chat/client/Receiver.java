package me.proartex.test.vitamin.chat.client;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver extends Thread {

    private BufferedReader in;

    Receiver(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        String message;

        try {
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        }
        catch (IOException e) {
            System.out.println("Connection closed");
        }
        catch (Throwable e) {
            //TODO: put log somewhere
            e.printStackTrace();
        }
        finally {
//            System.exit(1);
        }
    }
}
