//package me.proartex.test.vitamin.chat.server;
//
//import me.proartex.test.vitamin.chat.MsgConst;
//import me.proartex.test.vitamin.chat.protocol.Protocol;
//import me.proartex.test.vitamin.chat.server.commands.Executable;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public class ConnectionHandler {
//
//    public static String DEFAULT_HOST = "localhost";
//    public static int DEFAULT_PORT    = 9993;
//    private InetSocketAddress socketAddress;
//    private Selector selector;
//    private ServerSocketChannel serverChannel;
//    private ByteBuffer buffer;
//    private HashMap<SelectionKey, Connection> clients              = new HashMap<>();
//    private HashMap<SelectionKey, Connection> notRegisteredClients = new HashMap<>();
//
//    public ConnectionHandler() throws IOException {
//        this(DEFAULT_HOST, DEFAULT_PORT);
//    }
//
//    public ConnectionHandler(String host, int port) throws IOException {
//        buffer = ByteBuffer.allocate(512);
//        selector = Selector.open();
//        socketAddress = new InetSocketAddress(host, port);
//        serverChannel = ServerSocketChannel.open();
//        configureChannel();
//    }
//
//    public void switchOpsToWriteIfNecessary() {
//        switchOpsToWrite(notRegisteredClients);
//        switchOpsToWrite(clients);
//    }
//
//    public Set<SelectionKey> registerNewActivity() throws IOException {
//        Set<SelectionKey> keys = selector.selectedKeys();
//        keys.clear();
//        selector.select();
//        return keys;
//    }
//
//    public void accept() throws IOException {
//        SocketChannel socketChannel = serverChannel.accept();
//        socketChannel.configureBlocking(false);
//
//        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
//
//        notRegisteredClients.put(key, new Connection());
//    }
//
//    public void read(SelectionKey key) {
//        SocketChannel socketChannel = (SocketChannel) key.channel();
//
//        int numRead;
//        StringBuilder serializedCommand = new StringBuilder();
//
//        try {
//            while ((numRead = socketChannel.read((ByteBuffer) buffer.clear())) > 0) {
//                serializedCommand.append(new String(buffer.array(), 0, numRead, StandardCharsets.UTF_8));
//            }
//        }
//        catch (IOException e) {
////            e.printStackTrace();
//            closeConnection(key, null);
//            return;
//        }
//
//        if (numRead == -1) {
//            closeConnection(key, null);
//            return;
//        }
//
//        // Process data
//        ArrayList<Executable> commands = Protocol.deserialize(serializedCommand.toString());
////                        System.out.println("found commands: "+ commands);
//        for (Executable command : commands) {
//            command.setServer(this);
//            command.execute(key);
//        }
//    }
//
//    private void write(SelectionKey key) {
//        SocketChannel socketChannel = (SocketChannel) key.channel();
//        Connection connection       = getClientGroup(key).get(key);
//        LinkedList<byte[]> queue    = connection.getMessageQueue();
//
//        try {
//            //write everything are in queue
//            for (byte[] message : queue) {
//                int send = socketChannel.write(ByteBuffer.wrap(addLineSeparator(message)));
//            }
//        }
//        catch (IOException e) {
////            e.printStackTrace();
//            closeConnection(key, null);
//            return;
//        }
//
//        queue.clear();
//
//        key.interestOps(SelectionKey.OP_READ);
//    }
//
//    private void configureChannel() throws IOException {
//        serverChannel.configureBlocking(false);
//        serverChannel.socket().bind(socketAddress);
//        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
//    }
//
//    private void switchOpsToWrite(HashMap<SelectionKey, Connection> clientGroup) {
//        Iterator<Map.Entry<SelectionKey, Connection>> iterator = clientGroup.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            Map.Entry<SelectionKey, Connection> pair = iterator.next();
//            SelectionKey curKey = pair.getKey();
//
//            //seems client left
//            if (!curKey.isValid()) {
//                closeConnection(curKey, clientGroup, iterator);
//                continue;
//            }
//
//            if (pair.getValue().getMessageQueue().size() > 0) {
//                curKey.interestOps(SelectionKey.OP_WRITE);
//            }
//        }
//    }
//}
