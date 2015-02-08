package me.proartex.test.vitamin.chat.nio;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver extends Thread {

    private BufferedReader in;

    Receiver(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        int character;

        try {
            while ((character = in.read()) != 1) {
                if (character == Character.LINE_SEPARATOR) {
                    System.out.println(builder.toString());
                    builder.setLength(0);
                    continue;
                }

//                System.out.println("read: " + character);
                builder.append((char)character);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
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
