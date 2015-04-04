package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.protocol.Protocol;
import me.proartex.test.vitamin.chat.server.commands.Executable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @since 1.7
 */
public class Server implements Runnable {

    public static String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT    = 9993;
    public int messageHistoryLimit    = 100;
    private volatile boolean isInterrupted;
    private long sessionId;
    private InetSocketAddress socketAddress;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer                                      = ByteBuffer.allocate(512);
    private HashMap<SelectionKey, Connection> clients              = new HashMap<>();
    private HashMap<SelectionKey, Connection> notRegisteredClients = new HashMap<>();
    private LinkedList<Message> messageHistory                     = new LinkedList<>();

    public Server() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Server(String host, int port) throws ServerException {
        try {
            socketAddress = new InetSocketAddress(host, port);
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            configureChannel();
        }
        catch (IOException e) {
            throw new ServerException("Server initialization failed: " + e.getMessage());
        }
        finally {
            //TODO: close resources
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        System.out.println("Starting server");
        new Thread(this).start();
    }

    public void stop() {
        System.out.println("Stopping server");
        isInterrupted = true;
    }

    @Override
    public void run() {
        while (!isInterrupted) {
            switchOpsToWriteIfNecessary(notRegisteredClients);
            switchOpsToWriteIfNecessary(clients);

            try {
                Set<SelectionKey> keys = listenForNewActivitiesWithTimeout(1000);
                for (SelectionKey key : keys) {

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) {
                        acceptConnection();
                    }
                    else if (key.isReadable()) {
                        String serializedCommands = readFromChannelOf(key);
                        deserializeAndExecute(serializedCommands, key);
                    }
                    else if (key.isWritable()) {
                        write(key);
                    }
                }
            }
            catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    public void openSession() {
        sessionId = new Date().getTime();
        System.out.println("Session " + sessionId + " has been opened");
    }

    public void closeSession() {
        System.out.println("Session " + sessionId + " has been closed");
        sessionId = 0;
        messageHistory.clear();
    }

    private void configureChannel() throws IOException {
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(socketAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private Set<SelectionKey> listenForNewActivitiesWithTimeout(long timeout) throws IOException {
        Set<SelectionKey> keys = selector.selectedKeys();
        keys.clear();
        selector.select(timeout);

        return keys;
    }

    private void acceptConnection() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        //register channel to read
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);

        notRegisteredClients.put(key, new Connection());
    }

    private String readFromChannelOf(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        int numRead;
        StringBuilder context = new StringBuilder();

        try {
            while ((numRead = socketChannel.read((ByteBuffer) buffer.clear())) > 0) {
                context.append(new String(buffer.array(), 0, numRead, StandardCharsets.UTF_8));
            }
        }
        catch (IOException e) {
            closeConnection(key, null);
            throw e;
        }

//        if (numRead == -1) {
//            closeConnection(key, null);
//        }

        return context.toString();
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Connection connection       = getClientGroup(key).get(key);
        LinkedList<String> queue    = connection.getMessageQueue();

        try {
            Iterator<String> iterator = queue.iterator();
            while (iterator.hasNext()) {
                String message = addLineSeparator(iterator.next());
                ByteBuffer bufferedMessage = ByteBuffer.wrap(message.getBytes());
                socketChannel.write(bufferedMessage);
                iterator.remove();
            }
        }
        catch (IOException e) {
            closeConnection(key, null);
            throw e;
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void switchOpsToWriteIfNecessary(HashMap<SelectionKey, Connection> clientGroup) {
        Iterator<Map.Entry<SelectionKey, Connection>> iterator = clientGroup.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SelectionKey, Connection> pair = iterator.next();
            SelectionKey curKey = pair.getKey();

            //seems client left
            if (!curKey.isValid()) {
                closeConnection(curKey, clientGroup, iterator);
                continue;
            }

            if (pair.getValue().getMessageQueue().size() > 0) {
                curKey.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    private void deserializeAndExecute(String serializedCommands, SelectionKey key) {
        ArrayList<Executable> commands = Protocol.deserialize(serializedCommands);
//        System.out.println("found commands: "+ commands);
        for (Executable command : commands) {
            command.setServer(this);
            command.execute(key);
        }
    }





    public void closeConnection(SelectionKey key, Iterator<Map.Entry<SelectionKey, Connection>> iterator) {
        closeConnection(key, getClientGroup(key), iterator);
    }

    public void closeConnection(SelectionKey key,
                                HashMap<SelectionKey, Connection> clientGroup,
                                Iterator<Map.Entry<SelectionKey, Connection>> iterator) {

        SocketChannel socketChannel = (SocketChannel) key.channel();
        String message              = clientGroup.get(key).getUsername() + MsgConst.USER_LEFT_POSTFIX;
        boolean registeredUser      = clientGroup == clients;

        //notify server
        if (registeredUser)
            System.out.println(clientGroup.get(key).getUsername() + " sign out. Total: " + (clientGroup.size()-1));

        //ConcurrentModificationException safe remove
        if (iterator == null)
            clientGroup.remove(key);
        else
            iterator.remove();

        try {
            socketChannel.close();
            key.cancel();

            //forget about not registered user
            if (!registeredUser)
                return;

            //close session
            if (clients.size() == 0) {
                closeSession();
                return;
            }

            //say it to everyone
            for (Map.Entry<SelectionKey, Connection> client: clients.entrySet()) {
                client.getValue().getMessageQueue().add(message);
                client.getKey().interestOps(SelectionKey.OP_WRITE);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String addLineSeparator(String message) {
        return message + System.getProperty("line.separator");
    }

    public boolean isFreeUserName(String userName) {
        boolean isFree = true;

        for (Map.Entry<SelectionKey, Connection> client: clients.entrySet()) {
            if (client.getValue().getUsername().equals(userName)) {
                isFree = false;
                break;
            }
        }

        return isFree;
    }

    public HashMap<SelectionKey, Connection> getClientGroup(SelectionKey key) {
        return clients.get(key) != null ? clients : notRegisteredClients;
    }

    public HashMap<SelectionKey, Connection> getNotRegisteredClients() {
        return notRegisteredClients;
    }

    public HashMap<SelectionKey, Connection> getClients() {
        return clients;
    }

    public LinkedList<Message> getMessageHistory() {
        return messageHistory;
    }
}
