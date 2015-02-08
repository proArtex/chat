package me.proartex.test.vitamin.chat.nio;

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

    private InetAddress hostAddress;
    private int port;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private SelectionKey serverKey;
    private ByteBuffer buffer                                 = ByteBuffer.allocate(512);
    private HashMap<SelectionKey, LinkedList<byte[]>> clients = new HashMap<>();

    public Server(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
    }

    public static void main(String[] args) {
        try {
            new Server(null, 9993).start();
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
            //changing interestOps to write if need
            for (Map.Entry<SelectionKey, LinkedList<byte[]>> client: clients.entrySet()) {
                if (client.getValue().size() > 0) {
                    client.getKey().interestOps(SelectionKey.OP_WRITE);
                }
            }

            try {
                System.out.println("selecting...");
                selector.select();

                for (SelectionKey key : selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if(key.isAcceptable()) {
                        System.out.println("isAcceptable");
                        accept();
                    }
                    else if(key.isReadable()) {
                        System.out.println("isReadable");
                        read(key);
                    }
                    else if(key.isWritable()) {
                        System.out.println("isWritable");
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

    private void accept() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        //register channel to read
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);

        clients.put(key, new LinkedList<byte[]>());
    }

    private void read(SelectionKey key) throws IOException { //TODO: inside?
        SocketChannel socketChannel = (SocketChannel) key.channel();

        int numRead;
        StringBuilder serializedCommand = new StringBuilder();

        try {
            while ((numRead = socketChannel.read((ByteBuffer) buffer.clear())) > 0) {
                serializedCommand.append(new String(buffer.array(), 0, numRead));
                System.out.println("read: " + numRead);
            }
        }
        catch (IOException e) {
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            key.channel().close();
            key.cancel();
            return;
        }

        // Process data
//        System.out.println(serializedCommand);
        ArrayList<Executable> commands = Protocol.deserialize(this, serializedCommand.toString().trim());
        for (Executable command : commands) {
            command.execute(key);
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        LinkedList<byte[]> queue = clients.get(key);

        //write everything are in queue
        for (byte[] message : queue) {
            int send = socketChannel.write(ByteBuffer.wrap(addLineSeparator(message)));
            System.out.println("write " + send);
        }

        queue.clear();

        key.interestOps(SelectionKey.OP_READ);
    }

    private byte[] addLineSeparator(byte[] message) {
        byte[] separatedMessage = new byte[message.length + 1];
        System.arraycopy(message, 0, separatedMessage, 0, message.length);
        separatedMessage[separatedMessage.length - 1] = Character.LINE_SEPARATOR;

        return separatedMessage;
    }

    public HashMap<SelectionKey, LinkedList<byte[]>> getClients() {
        return clients;
    }
}
