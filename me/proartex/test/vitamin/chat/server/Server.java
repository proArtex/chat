package me.proartex.test.vitamin.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {

    private static boolean DEBUG = true;
//    private static String LOG_PATH = "/var/log/chat/server.log";

    protected static int MSG_SYSTEM                  = 1;
    protected static int MSG_CLIENT                  = 2;
    protected static SimpleDateFormat dateFormat     = new SimpleDateFormat("HH:mm:ss");
    private static long SESSION_ID;
    private static int LAST_MESSAGE_LIMIT            = 100;
    private static final int PORT                    = 9993;
    private static ArrayList<Connection> CONNECTIONS = new ArrayList<>();
    private static ArrayList<Message> LAST_MESSAGES  = new ArrayList<>();

    public static void main(String[] args) {
        //TODO: controller
        Connection connection;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Starting server on port " + PORT);
            while (listening) {
                new Connection(serverSocket.accept()).start();
//                System.gc();
            }
        }
        catch (IOException e) {
            log("Could not listen on port " + PORT + e.getMessage());
        }
        catch (Throwable e) {
            log("Boss, we have a BIG problem!" + e.getMessage());
        }
    }

    protected static synchronized boolean addConnection(Connection connection) {
        //crazy but thread-safe add
        boolean uniqueUsername = true;

        for (Connection curConnection : CONNECTIONS) {
            if (curConnection.getUserName().equals(connection.getUserName())) {
                uniqueUsername = false;
                break;
            }
        }

        if (uniqueUsername) {
            if (CONNECTIONS.size() == 0) {
                createNewSession();
            }
            CONNECTIONS.add(connection);
        }
        else {
            connection.getOut().println("rejected");
        }

        return uniqueUsername;
    }

    protected  static synchronized void deleteConnection(Connection connection) {
        CONNECTIONS.remove(CONNECTIONS.indexOf(connection));

        if (CONNECTIONS.size() == 0) {
            deleteSession();
            CONNECTIONS.trimToSize(); //TODO: trim delay
        }
    }

    protected static synchronized void sendMsg(Socket socket, int messageType, String message) {
        Date date = new Date();

        for (Connection connection : CONNECTIONS) {
            //do not send to itself
            if (connection.getSocket() != socket) {
                connection.getOut().println( new StringBuilder()
                                                .append("[")
                                                .append(dateFormat.format(date))
                                                .append("] ")
                                                .append(message).toString());
            }
        }

        if (Server.MSG_CLIENT == messageType) {
            if (LAST_MESSAGES.size() == LAST_MESSAGE_LIMIT) {
                LAST_MESSAGES.remove(0);
            }
            //TODO: client's timezone
            LAST_MESSAGES.add(new Message(date, message));
        }
    }

    private static void createNewSession() {
        SESSION_ID = new Date().getTime();
        System.out.println("Session " + SESSION_ID + " has been created");
    }

    private static void deleteSession() {
        System.out.println("Session " + SESSION_ID + " has been removed");
        SESSION_ID = 0;
        LAST_MESSAGES.clear();
        LAST_MESSAGES.trimToSize();
    }

    protected static synchronized void log(String message) {
        if (DEBUG) System.out.println(message);
        //TODO: log everything
    }

    protected static ArrayList<Connection> getConnections() {
        return CONNECTIONS;
    }

    protected static ArrayList<Message> getLastMessages() {
        return new ArrayList<>(LAST_MESSAGES);
    }
}