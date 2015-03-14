package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.Protocol;
import me.proartex.test.vitamin.chat.exceptions.ClientException;

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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader stdin;
    private BufferedReader in;

    public static void main(String[] args) {
        new Client().start();
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
            closeInvolvedResources();
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
        sendMsg("/history");
    }

    public void runIncomingMessageListener() {
        new Receiver(this).start();
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

    private void closeInvolvedResources() {
        try {
            in.close();
            out.close();
            stdin.close();
            socket.close();
        } catch (Throwable t) {
            //NOP
        }
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
            sendMsg("/register " + userName);
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
            sendMsg(myMessage);

            //костылище для теста: в отсутствие System.exit() - уходим так
            if ("/exit".equals(myMessage))
                break;
        }
    }

    public void sendMsg(String message) {
        String serializedCommand;
        Executable command = CommandFactory.getCommand(message);

        if (command == null) {
            System.out.println(MsgConst.UNKNOWN_COMMAND_PREFIX + message);
            return;
        }

        if ( (serializedCommand = Protocol.serialize(command)) == null ) {
            System.out.println(MsgConst.SEND_FAIL_PREFIX + message);
            return;
        }

        out.println(serializedCommand);
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

    public BufferedReader getIn() {
        return in;
    }
}
