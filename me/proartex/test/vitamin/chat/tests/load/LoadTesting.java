package me.proartex.test.vitamin.chat.tests.load;

import java.util.Random;

public class LoadTesting {

    private static int CLIENT_LIMIT       = 1000;
    private static int LOAD_LOW           = 5000;
    private static int LOAD_AVERAGE       = 500;
    private static int LOAD_HIGH          = 100;
    public static volatile int clientNum  = 0;

    public static void main(String[] args) {

        for (int i = 0; i < CLIENT_LIMIT; i++) {
            TestClient client = new TestClient(++clientNum);
            client.start();

            try {
                Thread.sleep(randomInRange(0, LOAD_HIGH));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int randomInRange(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
