package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.protocol.Protocol;
import me.proartex.test.vitamin.chat.commands.Serializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

    public static final String DEFAULT_HOST = "proartex.me";
    public static final int DEFAULT_PORT    = 9993;
    private int maxConnectTries             = 10;
    private int curConnectTries;
    private Receiver receiver;
    private String host;
    private int port;
    private BufferedReader stdin;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public Client() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        Client client =  new Client("localhost", 9993);
        client.start();
    }

//    public void start() {
////        init()
//    }
//
//    public void stop() {
//
//    }

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

    public void connectToServer() {
        if (!connectionEstablished()) {
            System.out.println(MsgConst.CONNECTION_FAIL);
            throw new ClientException();
        }
    }

    private void registerUser() {
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

    private void readAndSendUserMessage() {
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
            init();
            return true;
        }
        catch (UnknownHostException e) {
            return false;
        }
        catch (IOException e) {
            if (++curConnectTries < maxConnectTries)
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

    private void init() throws IOException {
        socket   = new Socket(host, port);
        out      = new PrintWriter(socket.getOutputStream(), true);
        in       = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdin    = new BufferedReader(new InputStreamReader(System.in));
        receiver = new Receiver(in);
    }

    private boolean tryToRegister() throws IOException {
        String response = null;

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
            if ("/exit".equals(myMessage)) {
                break;
            }
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
