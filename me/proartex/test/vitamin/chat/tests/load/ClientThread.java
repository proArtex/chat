package me.proartex.test.vitamin.chat.tests.load;

import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.client.exceptions.ClientException;

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
            client.start();
            client.showMessageHistoryToUser(); //TODO: before register?
//            client.runInboundMessageListener();

            client.sendMessage("/register User" + userNum);

            while (new Date().getTime() < startTime + lifeTime) {
                Thread.sleep(LoadTesting.randomInRange(1000, 5000));
                client.sendMessage("message" + String.valueOf(++messageNum)); //TODO: use commands also
            }

            client.sendMessage("/exit");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            //NOP
        }
    }
}
