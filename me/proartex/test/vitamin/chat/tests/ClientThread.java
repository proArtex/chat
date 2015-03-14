package me.proartex.test.vitamin.chat.tests;

import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.client.Receiver;
import me.proartex.test.vitamin.chat.exceptions.ClientException;

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
            client.connectToServer();
            client.showMessageHistoryToUser();
            client.runIncomingMessageListener();

            client.sendMsg("/register User" + userNum);

            while (new Date().getTime() < startTime + lifeTime) {
                Thread.sleep(LoadTesting.randomInRange(1000, 5000));
                client.sendMsg("message" + String.valueOf(++messageNum)); //TODO: use commands also
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            //NOP
        }

        client.sendMsg("/exit");
    }
}
