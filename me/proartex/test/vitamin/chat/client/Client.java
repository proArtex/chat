package me.proartex.test.vitamin.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client extends Thread {

    //TODO: private
    public Client() {
        //TODO:get timezone
    }

    public boolean debug = false;

    private final String host = "127.0.0.1";
//    private final String host  = "proartex.me";
    private final int port     = 9993;
    private PrintWriter out;
    private BufferedReader in, stdin;

    public static void main(String[] args) {
        new Client().start();
    }

    @Override
    public void run() {
        String myMessage;
        Socket socket = null;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));

            setUserName();

            //start listen
            Receiver receiver = new Receiver(in);
            receiver.start();

            while ((myMessage = stdin.readLine()) != null) {
                sendMsg(myMessage);

                //костылище для теста: в отсутствие System.exit() - уходим так
                if ("/exit".equals(myMessage))
                    break;
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Can't connect to the host " + host);
        }
        catch (IOException e) {
            System.out.println("Connection closed");
        }
        catch (Throwable e) {
            e.printStackTrace();
            //TODO: put log somewhere
        }
        finally {
            try {
                in.close();
                out.close();
                stdin.close();
                socket.close();
            } catch (Throwable e) {
                //NOP
            }
        }
    }

    //TODO: private
    public void sendMsg(String msg) {
        out.println(msg);
    }

    private void setUserName() throws IOException {
        //TODO: max tries?
        String userName, history;
        int tries = 0;

        System.out.println("Type username please:");

        do {
            if (tries++ > 0) {
                System.out.println("Username is already in use, try another one:");
            }

            while (
                    !isValidUserName(
                            userName = debug ? String.valueOf(Math.random()) : stdin.readLine()
                    )
            ) {
                System.out.println("Username is incorrect, try to use this rules: (...)");
            }

            sendMsg(userName);
        }
        while (!"connected".equals(userName = in.readLine()) && userName != null);

        //do not listen until last messages are shown
        while (!"accessed".equals(history = in.readLine()) && history != null) {
            System.out.println(history);
        }

        if (!out.checkError()) {
            System.out.println("You have joined the chat. Use '/commands' to look at chat commands.");
        }
    }

    private static boolean isValidUserName(String userName) {
        Pattern p = Pattern.compile("[A-z0-9_.]+");
        Matcher m = p.matcher(userName);
        return m.matches();
    }
}
