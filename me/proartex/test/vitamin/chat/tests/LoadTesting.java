//package me.proartex.test.vitamin.chat.tests;
//
//import me.proartex.test.vitamin.chat.client.Client;
//
//import java.util.Random;
//
//public class LoadTesting {
//
//    private static int CLIENT_LIMIT = 1000;
//    private static int LOAD_LOW     = 5000;
//    private static int LOAD_AVERAGE = 2000;
//    private static int LOAD_HIGH    = 500;
//
//    public static void main(String[] args) {
//
//        for (int i = 0; i < CLIENT_LIMIT; i++) {
//            new ClientThread(new Client()).start();
//
//            try {
//                Thread.sleep(randomInRange(300, LOAD_AVERAGE));
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    protected static int randomInRange(int min, int max) {
//        return new Random().nextInt(max - min + 1) + min;
//    }
//}
