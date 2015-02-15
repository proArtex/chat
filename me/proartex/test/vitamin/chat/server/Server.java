package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.Protocol;
import me.proartex.test.vitamin.chat.commands.Executable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server extends Thread {

    public int messageHistoryLimit = 100;

    private long sessionId;
    private InetAddress hostAddress;
    private int port;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer                                      = ByteBuffer.allocate(512);
    private HashMap<SelectionKey, Connection> clients              = new HashMap<>();
    private HashMap<SelectionKey, Connection> notRegisteredClients = new HashMap<>();
    private LinkedList<Message> messageHistory                     = new LinkedList<>();

    public Server(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
    }

    public static void main(String[] args) {
        try {
            new Server(null, 9993).start();
            System.out.println("Start server");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Selector initSelector() throws IOException {
        Selector selector = Selector.open();

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        return selector;
    }

    @Override
    public void run() {
        while (true) {

            // changing interestOps to write if need
            switchOpsToWrite(notRegisteredClients);
            switchOpsToWrite(clients);

            try {
                selector.select();

                for (SelectionKey key : selector.selectedKeys()) {

                    if (!key.isValid())
                        continue;

                    if(key.isAcceptable()) {
                        accept();
                    }
                    else if(key.isReadable()) {
                        read(key);
                    }
                    else if(key.isWritable()) {
                        write(key);
                    }
                }

                selector.selectedKeys().clear();
            }
            catch (IOException e) {
                e.printStackTrace();
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

    private void accept() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        //register channel to read
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);

        notRegisteredClients.put(key, new Connection());
    }

    private void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        int numRead;
        StringBuilder serializedCommand = new StringBuilder();

        try {
            while ((numRead = socketChannel.read((ByteBuffer) buffer.clear())) > 0) {
                serializedCommand.append(new String(buffer.array(), 0, numRead));
            }
        }
        catch (IOException e) {
//            e.printStackTrace();
            closeConnection(key, null);
            return;
        }

        if (numRead == -1) {
            closeConnection(key, null);
            return;
        }

        // Process data
        ArrayList<Executable> commands = Protocol.deserialize(this, serializedCommand.toString());
        for (Executable command : commands) {
            command.execute(key);
        }
    }

    private void write(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Connection connection       = getClientGroup(key).get(key);
        LinkedList<byte[]> queue    = connection.getQueue();

        try {
            //write everything are in queue
            for (byte[] message : queue) {
                int send = socketChannel.write(ByteBuffer.wrap(addLineSeparator(message)));
            }
        }
        catch (IOException e) {
//            e.printStackTrace();
            closeConnection(key, null);
            return;
        }

        queue.clear();

        key.interestOps(SelectionKey.OP_READ);
    }

    private void switchOpsToWrite(HashMap<SelectionKey, Connection> clientGroup) {
        Iterator<Map.Entry<SelectionKey, Connection>> iterator = clientGroup.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SelectionKey, Connection> pair = iterator.next();
            SelectionKey curKey = pair.getKey();

            //seems client left
            if (!curKey.isValid()) {
                closeConnection(curKey, clientGroup, iterator);
                continue;
            }

            if (pair.getValue().getQueue().size() > 0) {
                curKey.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    public void closeConnection(SelectionKey key, Iterator<Map.Entry<SelectionKey, Connection>> iterator) {
        closeConnection(key, getClientGroup(key), iterator);
    }

    public void closeConnection(SelectionKey key,
                                HashMap<SelectionKey, Connection> clientGroup,
                                Iterator<Map.Entry<SelectionKey, Connection>> iterator) {

        SocketChannel socketChannel = (SocketChannel) key.channel();
        String message              = clientGroup.get(key).getUserName() + MsgConst.USER_LEFT_POSTFIX;
        boolean registeredUser      = clientGroup == clients;

        //notify server
        if (registeredUser)
            System.out.println(clientGroup.get(key).getUserName() + " sign out. Total: " + (clientGroup.size()-1));

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
                client.getValue().getQueue().add(message.getBytes());
                client.getKey().interestOps(SelectionKey.OP_WRITE);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] addLineSeparator(byte[] message) {
        byte[] separatedMessage = new byte[message.length + 1];
        System.arraycopy(message, 0, separatedMessage, 0, message.length);
        separatedMessage[separatedMessage.length - 1] = Character.LINE_SEPARATOR;

        return separatedMessage;
    }

    public boolean isFreeUserName(String userName) {
        boolean isFree = true;

        for (Map.Entry<SelectionKey, Connection> client: clients.entrySet()) {
            if (client.getValue().getUserName().equals(userName)) {
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
