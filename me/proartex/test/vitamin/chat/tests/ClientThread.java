//package me.proartex.test.vitamin.chat.tests;
//
//import me.proartex.test.vitamin.chat.client.Client;
//
//import java.util.Date;
//
//public class ClientThread extends Thread {
//
//    private Client client;
//    private long startTime;
//
//    public ClientThread(Client client) {
//        this.client = client;
//        this.startTime = new Date().getTime();
//    }
//
//    @Override
//    public void run() {
//        long lifeTime = LoadTesting.randomInRange(5000, 100000);
//        client.debug = true;
//        client.start();
//
//        while (new Date().getTime() < startTime + lifeTime) {
//            try {
//                Thread.sleep(LoadTesting.randomInRange(1000, 5000));
//                client.sendMsg("aaaaa"); //TODO: use commands also
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        client.sendMsg("/exit");
//    }
//}
