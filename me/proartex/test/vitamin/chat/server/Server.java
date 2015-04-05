package me.proartex.test.vitamin.chat.server;

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
    private Session session;
    private InetSocketAddress socketAddress;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer              = ByteBuffer.allocate(512);
    private UserGroup users = new UserGroup();
    private UserGroup notRegisteredClients = new UserGroup();
    private volatile boolean isInterrupted;

    public Server() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Server(String host, int port) throws ServerException {
        try {
            session = new Session();
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
            System.out.println("new iteration");

            try {
                Set<SelectionKey> keys = listenForNewActivitiesWithTimeout(1000);
                for (SelectionKey key : keys) {

                    if (!key.isValid()) {
                        System.out.println("INVALID 1");
                        continue;
                    }

                    if (key.isAcceptable()) {
                        acceptConnection();
                    }
                    else if (key.isReadable()) {
                        String serializedCommands = readFromChannelOf(key);
                        deserializeAndExecute(serializedCommands, key);
                    }
                    else if (key.isWritable()) {
                        writeToChannelOf(key);
                    }
                }
            }
            catch (IOException e) {
//                e.printStackTrace();
            }

            users.removeUsersWithCanceledConnection();
            notRegisteredClients.removeUsersWithCanceledConnection();
            switchOps(notRegisteredClients);
            switchOps(users);
            processSession();
        }
    }

    private void configureChannel() throws IOException {
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(socketAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private Set<SelectionKey> listenForNewActivitiesWithTimeout(long timeout) throws IOException {
        Set<SelectionKey> keys = selector.selectedKeys();
        keys.clear();
        selector.select();
//        selector.select(timeout);

        return keys;
    }

    private void acceptConnection() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        //register channel to read
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);

//        notRegisteredClients.put(key, new Connection());
        notRegisteredClients.add(key);
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
            System.out.println("INVALID ON READ");
            cancelKey(key);
            throw e;
        }

        if (numRead == -1) {
            System.out.println("-1 READ INVALID!!!");
        }

        return context.toString();
    }

    private void writeToChannelOf(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
//        Connection connection       = getClientGroup(key).get(key);
//        LinkedList<String> queue    = connection.getMessageQueue();

        List<String> messageQueue = getClientGroup(key).getUsersMessageQueue(key);

        try {
            Iterator<String> iterator = messageQueue.iterator();
            while (iterator.hasNext()) {
                String message = addLineSeparator(iterator.next());
                ByteBuffer bufferedMessage = ByteBuffer.wrap(message.getBytes());
                socketChannel.write(bufferedMessage);
                iterator.remove();
            }
        }
        catch (IOException e) {
            System.out.println("INVALID ON WRITE");
            cancelKey(key);
            throw e;
        }
    }

    private void switchOps(UserGroup clientGroup) {
        for (Map.Entry<SelectionKey, User> pair: clientGroup.getUsers().entrySet()) {
            SelectionKey key = pair.getKey();
            if (pair.getValue().getMessageQueue().size() > 0) {
                key.interestOps(SelectionKey.OP_WRITE);
            }
            else {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void processSession() {
        if (timeToOpenSession()) {
            session.open();
        }
        else if (timeToCloseSession()) {
            session.close();
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

    public void cancelKey(SelectionKey key) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.close();
            key.cancel();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean timeToOpenSession() {
        return users.count() > 0 && !session.isOpened();
    }

    private boolean timeToCloseSession() {
        return users.count() == 0 && session.isOpened();
    }

    private String addLineSeparator(String message) {
        return message + System.getProperty("line.separator");
    }

    public boolean isFreeUserName(String username) {
        return !users.containsUserWith(username);
    }

    public UserGroup getClientGroup(SelectionKey key) {
        return users.containsUserWith(key) ? users : notRegisteredClients;
    }

    public UserGroup getNotRegisteredClients() {
        return notRegisteredClients;
    }

    public UserGroup getUsers() {
        return users;
    }

    public List<Message> getMessageHistory() {
        return session.getMessageHistory();
    }

    public void addMessageToHistory(Message message) {
        session.noteMessage(message);
    }

    //FROM COMMANDS
    public boolean containsUserWith(SelectionKey key) {
        return users.containsUserWith(key);
    }
}
