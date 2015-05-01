package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.CommandPlacement;
import me.proartex.test.vitamin.chat.Executable;
import me.proartex.test.vitamin.chat.Utils;
import me.proartex.test.vitamin.chat.protocol.Protocol;
import me.proartex.test.vitamin.chat.server.commands.ServerCommand;
import me.proartex.test.vitamin.chat.server.exceptions.ReadException;
import me.proartex.test.vitamin.chat.server.exceptions.ServerException;
import me.proartex.test.vitamin.chat.server.exceptions.WriteException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server implements Runnable {

    public static final String DEFAULT_HOST = "0.0.0.0";
    public static final int DEFAULT_PORT    = 9993;
    private volatile Thread serverThread;
    private Session session;
    private RegisteredGroup registeredUsers;
    private NotRegisteredGroup notRegisteredUsers;
    private CommandHandler commandHandler;
    private InetSocketAddress socketAddress;
    private ByteBuffer buffer;
    private ServerSocketChannel serverChannel;
    private Selector selector;

    public Server() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Server(String host, int port) {
        session            = new Session();
        registeredUsers    = new RegisteredGroup();
        notRegisteredUsers = new NotRegisteredGroup();
        commandHandler     = new CommandHandler(this, session, registeredUsers, notRegisteredUsers);
        socketAddress      = new InetSocketAddress(host, port);
        buffer             = ByteBuffer.allocate(512);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
//        Thread.sleep(10000);
//        server.stop();
    }

    public void start() throws ServerException {
        try {
            initializeAndConfigureResources();
            serverThread = new Thread(this);
            serverThread.start();
            System.out.println("Server started at " + socketAddress.toString());
        }
        catch (Throwable t) {
            closeResourcesIgnoringExceptions();
            throw new ServerException("Server starting failed: " + t.getMessage());
        }
    }

    public void stop() throws ServerException {
        try {
            if (serverThread == null)
                return;

            serverThread.interrupt();
            while (serverThread.isAlive()) {
                Thread.sleep(500);
            }
            System.out.println("Server stopped");
        }
        catch (Throwable t) {
            throw new ServerException("Server stopping failed: " + t.getMessage());
        }
    }

    @Override
    public void run() {
        while (!serverThread.isInterrupted()) {
            System.out.println("new iteration");

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
                        writeToChannelOf(key);
                    }
                }
            }
            catch (ReadException | WriteException ignore) {
                /*NOP*/
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Throwable t) {
                t.printStackTrace();
                break;
            }

            removeUsersWithClosedConnection();
            switchUserOperations();
            processSession();
        }

        closeResourcesIgnoringExceptions();
    }

    private void initializeAndConfigureResources() throws IOException {
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(socketAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void closeResourcesIgnoringExceptions() {
        try {
            if (serverChannel != null)
                serverChannel.close();
            if (selector != null)
                selector.close();
        }
        catch (Throwable ignore) {/*NOP*/}
    }

    private Set<SelectionKey> listenForNewActivitiesWithTimeout(long timeout) throws IOException {
        Set<SelectionKey> keys = selector.selectedKeys();
        keys.clear();
        selector.select(/*timeout*/);

        return keys;
    }

    private void acceptConnection() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ/* | SelectionKey.OP_WRITE */);

        notRegisteredUsers.add(key, new User());
    }

    private String readFromChannelOf(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        StringBuilder context = new StringBuilder();
        int numRead;

        try {
            while ((numRead = socketChannel.read((ByteBuffer) buffer.clear())) > 0) {
                String data = new String(buffer.array(), 0, numRead, StandardCharsets.UTF_8);
                context.append(data);
            }
        }
        catch (IOException e) {
            cancelKey(key);
            throw new ReadException();
        }

        if (numRead == -1) {
            cancelKey(key);
            throw new ReadException();
        }

        return context.toString();
    }

    private void writeToChannelOf(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        User user = getUserWith(key);
        List<String> commandQueue = user.getOutboundCommandQueue();

        try {
            Iterator<String> iterator = commandQueue.iterator();
            while (iterator.hasNext()) {
                String command = Utils.addLineSeparator(iterator.next());
                ByteBuffer bufferedMessage = ByteBuffer.wrap(command.getBytes());
                socketChannel.write(bufferedMessage);
                iterator.remove();
            }
        }
        catch (IOException e) {
            cancelKey(key);
            throw new WriteException();
        }
    }

    private void removeUsersWithClosedConnection() {
        registeredUsers.removeUsersWithClosedConnection();
        notRegisteredUsers.removeUsersWithClosedConnection();
    }

    private void switchUserOperations() {
        switchUserOperations(registeredUsers);
        switchUserOperations(notRegisteredUsers);
    }

    private void switchOpsForKey(int ops, SelectionKey key) {
        key.interestOps(ops);
    }

    private void switchUserOperations(UserGroup userGroup) {
        for (Map.Entry<SelectionKey, User> pair: userGroup.getUsers().entrySet()) {
            SelectionKey key = pair.getKey();
            User user = pair.getValue();

            if (user.getOutboundCommandQueue().size() > 0) {
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
        List<Executable> commands = Protocol.deserialize(serializedCommands, CommandPlacement.SERVER);
//        System.out.println("found commands: "+ commands);
        for (Executable command : commands) {
            ((ServerCommand) command).setHandler(commandHandler);
            ((ServerCommand) command).setSelectionKey(key);
            command.execute();
        }
    }

    private boolean timeToOpenSession() {
        return registeredUsers.count() > 0 && !session.isOpened();
    }

    private boolean timeToCloseSession() {
        return registeredUsers.count() == 0 && session.isOpened();
    }

    void cancelKey(SelectionKey key) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.close();
            key.cancel();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    User getUserWith(SelectionKey key) {
        return registeredUsers.containsUserWith(key)
               ? registeredUsers.getUserWith(key)
               : notRegisteredUsers.getUserWith(key);
    }
}
