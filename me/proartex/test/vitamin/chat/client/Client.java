package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.Protocol;

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
        String myMessage;

        try {
            stdin = new BufferedReader(new InputStreamReader(System.in));

            //try to connect few times
            if (!connect()) {
                System.out.println(MsgConst.CONNECTION_FAIL);
                return;
            }

            //set username
            if (!registerMe()) {
                System.out.println(MsgConst.CONNECTION_FAIL);
                return;
            }

            //show server history
            showMessageHistory();

            //start listen
            new Receiver(this).start();

            //typing
            while ((myMessage = stdin.readLine()) != null) {
                sendMsg(myMessage);

                //костылище для теста: в отсутствие System.exit() - уходим так
                if ("/exit".equals(myMessage))
                    break;
            }
        }
        catch (UnknownHostException e) {
            System.out.println(MsgConst.UNREACHABLE_HOST_PREFIX + host);
        }
        catch (IOException e) {
            System.out.println(MsgConst.CONNECTION_CLOSED);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
                out.close();
                stdin.close();
                socket.close();
            } catch (Throwable t) {
                //NOP
            }
        }
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out    = new PrintWriter(socket.getOutputStream(), true);
            in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            return true;
        }
        catch (IOException e) {
            if (++tries < maxTries)
                return connect();
        }

        return false;
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

    private boolean registerMe() throws IOException {
        String userName, response = null;

        System.out.println(MsgConst.ASK_FOR_USERNAME);

        do {
            if (response != null)
                System.out.println(response);

            while ((userName = stdin.readLine()) != null && !isValidUserName(userName)) {
                System.out.println(MsgConst.INVALID_USERNAME);
            }

            sendMsg("/register " + userName);
        }
        while (!MsgConst.REGISTER_SUCCESS.equals(response = in.readLine()) && response != null);

        if (response == null)
            return false;

        System.out.println(response);
        return true;
    }

    private static boolean isValidUserName(String userName) {
        return userName.matches("[A-z0-9_]+");
    }

    public void showMessageHistory() {
        sendMsg("/history");
    }

    public BufferedReader getIn() {
        return in;
    }
}
