package me.proartex.test.vitamin.chat.tests.load;

import me.proartex.test.vitamin.chat.client.Client;
//import me.proartex.test.vitamin.chat.client.Receiver;

import java.util.Random;

public class LoadTesting {

    private static int CLIENT_LIMIT       = 1000;
    private static int LOAD_LOW           = 5000;
    private static int LOAD_AVERAGE       = 500;
    private static int LOAD_HIGH          = 100;
    public static volatile int clientNum  = 0;

    public static void main(String[] args) {

        for (int i = 0; i < CLIENT_LIMIT; i++) {
            ClientThread clientThread =  new ClientThread(new Client("localhost", 9993), ++clientNum);
            clientThread.start();

            try {
                Thread.sleep(randomInRange(0, LOAD_HIGH));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected static int randomInRange(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
