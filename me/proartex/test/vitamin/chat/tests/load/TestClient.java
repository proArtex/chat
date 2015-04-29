package me.proartex.test.vitamin.chat.tests.load;

import me.proartex.test.vitamin.chat.client.Client;
import me.proartex.test.vitamin.chat.client.exceptions.ClientException;

import java.io.IOException;
import java.util.Date;

class TestClient extends Client {
    private int userNum;
    private long startTime;
    private int messageNum;

    public TestClient(int userNum) {
        super("localhost", 9993);
        this.userNum = userNum;
        this.startTime = new Date().getTime();
    }

    @Override
    public void registerUser() {
        sendMessage("/register User" + userNum);
    }

    @Override
    public void runInboundMessageListener() {
        //non-read overflow?
        new Thread() {
            @Override
            public void run() {
                try {
                    while ((_readFromSocketChannel()) != null) {/*NOP*/}
                }
                catch (IOException ignore) {/*NOP*/}
            }
        }.start();
    }

    @Override
    protected void readAndSendUserMessageInLoop() {
        long lifeTime = LoadTesting.randomInRange(100000, 100000);

        try {
            while (new Date().getTime() < startTime + lifeTime) {
                Thread.sleep(LoadTesting.randomInRange(1000, 3000));
                sendMessage("message" + String.valueOf(++messageNum)); //TODO: use commands also
            }

            sendMessage("/exit");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ClientException ignore) {/*NOP*/}
    }
}