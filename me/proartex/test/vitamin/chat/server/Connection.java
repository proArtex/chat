package me.proartex.test.vitamin.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread {

    private String userName;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;

    public Connection(Socket socket) {
        super("Connection");
        this.socket = socket;
    }

    @Override
    public void run() {
        String message;
        int code;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            do {
                userName = in.readLine();
            }
            while (!Server.addConnection(this));
            connected = true;
            sendMSg("connected");

            showLastMessages();
            sendMSg("accessed");
            Server.sendMsg(socket, Server.MSG_SYSTEM, userName + " has joined the chat");

            while ((message = in.readLine()) != null) {
                code = Protocol.handle(message);

                if (Protocol.CODE_MESSAGE == code) {
                    Server.sendMsg(socket, Server.MSG_CLIENT, userName + ": " + message);
                }
                else if (Protocol.CODE_UNKNOWN_COMMAND == code) {
                    sendMSg("Unknown command " + message);
                }
                else if (Protocol.CODE_SHOW_COMMANDS == code) {
                    for (String command : Protocol.getAvailableCommands()) {
                        sendMSg(command);
                    }
                }
                else if (Protocol.CODE_USER_COUNT == code) {
                    sendMSg(String.valueOf(Server.getConnections().size()));
                }
                else if (Protocol.CODE_USER_NAME == code) {
                    sendMSg(userName);
                }
                else if (Protocol.CODE_EXIT == code) {
                    break;
                }
                else {
                    //unreachable in theory
                }
            }
        }
        catch (IOException e) {
            if (connected)
                Server.log("Connection refused: " + userName);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        finally {
            if (connected) {
                Server.deleteConnection(this);
                Server.sendMsg(socket, Server.MSG_SYSTEM, userName + " has left the chat");
            }

            try {
                in.close();
                out.close();
                socket.close();
            }
            catch (Throwable e) {
                //NOP
            }
        }
    }

    private void sendMSg(String message) {
        out.println(message);
    }

    private void showLastMessages() {
        for (Message message : Server.getLastMessages()) {
            sendMSg(new StringBuilder()
                    .append("[")
                    .append(Server.dateFormat.format(message.getDate()))
                    .append("] ")
                    .append(message.getMsg()).toString());
        }
    }

    protected PrintWriter getOut() {
        return out;
    }

    protected Socket getSocket() {
        return socket;
    }

    protected String getUserName() {
        return userName;
    }
}
