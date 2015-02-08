package me.proartex.test.vitamin.chat.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class Client2 extends Thread {

    private InetAddress hostAddress;
    private int port;
    private Selector selector;
    private SelectionKey clientKey;
    private ByteBuffer buffer        = ByteBuffer.allocate(512);
    private LinkedList<byte[]> queue = new LinkedList<>();

    public Client2(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = initSelector();
        new Invoker(clientKey, queue).start();
    }

    public static void main(String[] args) {
        try {
            new Client2(null, 9993).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Selector initSelector() throws IOException {
        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));
        clientKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);
        System.out.println(clientKey);

        return selector;
    }

    @Override
    public void run() {
        while (true) {

//            if (queue.size() > 0) {
//                clientKey.interestOps(SelectionKey.OP_WRITE);
//            }

            try {
                System.out.println("selecting...");
                this.selector.select();

                for (SelectionKey key : selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }
                    
                    if (key.isConnectable()) {
                        System.out.println("isConnectable");
                        connect(key);
                        System.out.println(key);
                    }
                    else if (key.isReadable()) {
                        System.out.println("isReadable");
                        read(key);
                    }
                    else if (key.isWritable()) {
                        System.out.println("isWritable");
                        write(key);
                    }
                }

                selector.selectedKeys().clear();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        try {
            socketChannel.finishConnect();
        }
        catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            return;
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        int numRead;
        StringBuilder response = new StringBuilder();

        try {
            while ((numRead = socketChannel.read((ByteBuffer) buffer.clear())) > 0) {
                response.append(new String(buffer.array(), 0, numRead));
                System.out.println("read: " + numRead);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            key.channel().close();
            key.cancel();
            return;
        }

        // Print the response
        System.out.println(response.toString());
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        //write everything are in queue
        for (byte[] message : queue) {
            int send = socketChannel.write(ByteBuffer.wrap(message));
            System.out.println("write " + send);
        }

        queue.clear();

        key.interestOps(SelectionKey.OP_READ);
    }
}
