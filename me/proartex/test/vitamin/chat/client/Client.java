package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.protocol.Protocol;
import me.proartex.test.vitamin.chat.client.commands.Serializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

    private String host        = "127.0.0.1";
//    private final String host  = "proartex.me";
    private int port           = 9993;
    private int maxTries       = 10;
    private int tries          = 0;
    private Receiver receiver;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader stdin;
    private BufferedReader in;

    public Client(Receiver receiver) {
        this.receiver = receiver;
    }

    public static void main(String[] args) {
        new Client(new Receiver()).start();
    }

    @Override
    public void run() {
        try {
            connectToServer();
            registerUser();
            showMessageHistoryToUser();
            runIncomingMessageListener();
            readAndSendUserMessage();
        }
        catch (ClientException e) {
            //NOP
        }
        finally {
            closeResourcesIgnoringExceptions();
        }
    }

    public void connectToServer() throws ClientException {
        if (!connectionEstablished()) {
            System.out.println(MsgConst.CONNECTION_FAIL);
            throw new ClientException();
        }
    }

    private void registerUser() throws ClientException {
        if (!registeredUser()) {
            System.out.println(MsgConst.CONNECTION_FAIL);
            throw new ClientException();
        }
    }

    public void showMessageHistoryToUser() {
        sendMessage("/history");
    }

    public void runIncomingMessageListener() {
        receiver.setIn(in);
        receiver.start();
    }

    private void readAndSendUserMessage() throws ClientException {
        try {
            tryToReadAndSendUserMessage();
        }
        catch (IOException e) {
            System.out.println(MsgConst.CONNECTION_CLOSED);
            throw new ClientException();
        }
    }

    private void closeResourcesIgnoringExceptions() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (stdin != null)
                stdin.close();
            if (socket != null)
                socket.close();
        } catch (Throwable ignore) {/*NOP*/}
    }
    
    private boolean connectionEstablished() {
        try {
            tryToConnect();
            return true;
        }
        catch (UnknownHostException e) {
            return false;
        }
        catch (IOException e) {
            if (++tries < maxTries)
                return connectionEstablished();
        }
        
        return false;
    }
    
    private boolean registeredUser() {
        try {
            return tryToRegister();
        }
        catch (IOException e) {
            return false;
        }
    }

    private void tryToConnect() throws IOException {
        socket = new Socket(host, port);
        out    = new PrintWriter(socket.getOutputStream(), true);
        in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private boolean tryToRegister() throws IOException {
        String response = null;
        stdin = new BufferedReader(new InputStreamReader(System.in));

        System.out.println(MsgConst.ASK_FOR_USERNAME);

        do {
            if (response != null)
                System.out.println(response);

            String userName = readUntilUsernameIsValid();
            sendMessage("/register " + userName);
        }
        while (!MsgConst.REGISTER_SUCCESS.equals(response = in.readLine()) && response != null);

        if (response == null)
            return false;

        System.out.println(response);
        return true;
    }

    private void tryToReadAndSendUserMessage() throws IOException {
        String myMessage;

        while ((myMessage = stdin.readLine()) != null) {
            sendMessage(myMessage);

            //костылище для теста: в отсутствие System.exit() - уходим так
            if ("/exit".equals(myMessage))
                break;
        }
    }

    public void sendMessage(String context) {
        Serializable command = ClientCommandFactory.getInstanceFor(context);
        String serializedCommand = Protocol.serialize(command);
        send(serializedCommand);
    }

    private String readUntilUsernameIsValid() throws IOException {
        String userName;

        while ((userName = stdin.readLine()) != null && !isValidUserName(userName)) {
            System.out.println(MsgConst.INVALID_USERNAME);
        }
        return userName;
    }

    private static boolean isValidUserName(String userName) {
        return userName.matches("[A-z0-9_]+");
    }

    private void send(String serializedCommand) {
        out.println(serializedCommand);
    }
}
