package me.proartex.test.vitamin.chat.client;

import me.proartex.test.vitamin.chat.CommandPlacement;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.client.commands.ClientCommand;
import me.proartex.test.vitamin.chat.client.exceptions.ClientException;
import me.proartex.test.vitamin.chat.protocol.Protocol;
import me.proartex.test.vitamin.chat.Serializable;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client implements Runnable {

    public static final String DEFAULT_HOST = "proartex.me";
    public static final int DEFAULT_PORT    = 9993;
    private int maxConnectTries             = 10;
    private int curConnectTries;

    private boolean isRegistered;

    private volatile Thread clientThread;
    private Receiver receiver;
    private String host;
    private int port;
    private BufferedReader stdin;
    private BufferedReader in; //TODO: volatile?
    private PrintWriter out;
    private Socket socket;

    public Client() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        receiver = new Receiver();
    }

    public static void main(String[] args) throws InterruptedException {
        ArgumentParser parser = new ArgumentParser(args);
        Client client =  new Client(parser.getHost(), parser.getPort());
        client.start();
    }

    public void start() throws ClientException {
        connectToServer();
        clientThread = new Thread(this);
        clientThread.start();
    }

    public void stop() throws ClientException {
        clientThread.interrupt();
//        throw new UnsupportedOperationException("blocking-read() interrupting impossible");
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void run() {
        try {
            registerUser();
            runInboundMessageListener(); //TODO: do not listen until history shown (put logic into register command?)
            showMessageHistoryToUser();
            readAndSendUserMessageInLoop();
        }
        catch (ClientException e) {
            print(e.getMessage());
        }
        finally {
            closeResourcesIgnoringExceptions();
        }
    }

    public void registerUser() {
        try {
            tryToRegister();
        }
        catch (IOException e) {
            throw new ClientException(TextConst.CONNECTION_CLOSED);
        }
    }

    public void sendMessage(String context) {
        Serializable command = ClientCommandFactory.getInstanceFor(context);
        String serializedCommand = Protocol.serialize(command);
        send(serializedCommand);
    }

    public void showMessageHistoryToUser() {
        sendMessage("/history");
    }

    public void runInboundMessageListener() {
        new Thread(receiver).start();
    }

    private void connectToServer() {
        while (++curConnectTries < maxConnectTries) {
            try {
                initializeResources();
                return;
            }
            catch (UnknownHostException e) {
                break;
            }
            catch (IOException ignore) {
                /*NOP*/
            }
            catch (Throwable t) {
                break;
            }
        }

        throw new ClientException(TextConst.CONNECTION_FAIL);
    }

    private void initializeResources() throws IOException {
        socket = new Socket(host, port);
        out    = new PrintWriter(socket.getOutputStream(), true);
        in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdin  = new BufferedReader(new InputStreamReader(System.in));
    }

    protected void readAndSendUserMessageInLoop() {
        try {
            tryToReadAndSendUserMessageInLoop();
        }
        catch (IOException e) {
            throw new ClientException(TextConst.CONNECTION_CLOSED);
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
        }
        catch (Throwable ignore) {/*NOP*/}
    }

    private void tryToRegister() throws IOException {
        String response;
        print(TextConst.ASK_FOR_USERNAME);

        do {
            String username = readFromConsole();

            //read while stopping? wtf
//            if (username == null)
//                System.exit(-1);

            sendMessage("/register " + username);
            response = _readFromSocketChannel();
            deserializeAndExecute(response);
        }
        while (!clientThread.isInterrupted() && response != null && !isRegistered);

        if (response == null)
            throw new ClientException(TextConst.CONNECTION_CLOSED);
    }

    private void tryToReadAndSendUserMessageInLoop() throws IOException {
        String message;

        while (!clientThread.isInterrupted() && (message = readFromConsole()) != null) {
            sendMessage(message);

            //TODO const?
            if ("/exit".equals(message))
                break;
        }
    }

    private void send(String serializedCommand) {
        out.println(serializedCommand);
    }

    private String readFromConsole() throws IOException {
        return stdin.readLine();
    }

    //TODO: is already sync?
    protected String _readFromSocketChannel() throws IOException {
        return in.readLine();
    }

    void deserializeAndExecute(String serializedCommands) {
        List<Executable> commands = Protocol.deserialize(serializedCommands, CommandPlacement.CLIENT);

        for (Executable command : commands) {
            ((ClientCommand) command).setClient(this);
            command.execute();
        }
    }

    private class Receiver implements Runnable {
        @Override
        public void run() {
            try {
                tryToReadAndPrintServerResponseInLoop();
            }
            catch (IOException ignore) {
                //NOP
            }
            finally {
//                System.exit(1);
                clientThread.interrupt();
                System.out.println(TextConst.CONNECTION_CLOSED);
            }
        }

        private void tryToReadAndPrintServerResponseInLoop() throws IOException {
            String response;

            while (!clientThread.isInterrupted() && (response = _readFromSocketChannel()) != null) {
                deserializeAndExecute(response);
            }
        }
    }
}
