package me.proartex.test.vitamin.chat.tests;

import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.client.Receiver;

import java.util.Date;

public class ClientThread extends Thread {

    private Client client;
    private int userNum;
    private long startTime;
    private int messageNum = 0;

    public ClientThread(Client client, int userNum) {
        this.client = client;
        this.userNum = userNum;
        this.startTime = new Date().getTime();
    }

    @Override
    public void run() {
        long lifeTime = LoadTesting.randomInRange(100000, 100000);

        try {
            if (!client.connect()) {
                System.out.println("FAILED TO CONNECT");
                return;
            }

            client.showMessageHistory();

            new Receiver(client).start();

            client.sendMsg("/register User" + userNum);

            while (new Date().getTime() < startTime + lifeTime) {
                Thread.sleep(LoadTesting.randomInRange(1000, 5000));
                client.sendMsg("message" + String.valueOf(++messageNum)); //TODO: use commands also
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.sendMsg("/exit");
    }
}
