package me.proartex.test.vitamin.chat.tests.unit;


import junit.framework.Assert;
import me.proartex.test.vitamin.chat.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProtocolTest extends Assert {

    private Socket socket;
    BufferedReader in;
    PrintWriter out;

    @Before
    public void open() {
        try {
            connect();
        }
        catch (IOException e) {
            System.exit(-1);
        }
    }

    @Test
    public void testInvalidString() throws IOException {
        String response = sendRequest("abrakadabra=1;");
        assertEquals("Unknown command 'null'", response);
    }

    @Test
    public void testUnknownCommand() throws IOException {
        String response = sendRequest("id=0;context=/shutdown;");
        assertEquals("Unknown command '/shutdown'", response);
    }

    private void connect() throws IOException {
        socket = new Socket(Server.DEFAULT_HOST, Server.DEFAULT_PORT);
        out    = new PrintWriter(socket.getOutputStream(), true);
        in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private String sendRequest(String request) throws IOException {
        out.println(request);
        String response = in.readLine();

        System.out.println(response);
        return response;
    }
//
//    public void testDeserializeInvalidString() {
//        String command = "abrakadabra=1;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof UnknownCommand);
//    }
//
//    public void testDeserializeNoId() {
//        String command = "<SYS_COMMAND>a=2;username=123;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof UnknownCommand);
//    }
//
//    public void testDeserializeBigId() {
//        String command = "<SYS_COMMAND>id=999999999999999999;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof UnknownCommand);
//    }
//
//    public void testDeserializeWrongId() {
//        String command = "<SYS_COMMAND>id=&=1+2;username=123;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof UnknownCommand);
//    }
//
//    public void testRegisterUserCommand() {
//        serializedCommands.clear();
//
//        serializedCommands.add(0, "<SYS_COMMAND>id=2;username=123;");
//        serializedCommands.add(1, "<SYS_COMMAND>id=2;username=;");
//        serializedCommands.add(2, "<SYS_COMMAND>id=2;username=123;username=555;");
//        serializedCommands.add(3, "<SYS_COMMAND>id=2;");
//        serializedCommands.add(4, "<SYS_COMMAND>id=2;id=3;");
//
//        assertTrue(getByIndexAndDeserialize(0) instanceof RegisterUserCommand);
//        assertTrue(getByIndexAndDeserialize(1) instanceof RegisterUserCommand);
//        assertTrue(getByIndexAndDeserialize(2) instanceof RegisterUserCommand);
//        assertTrue(getByIndexAndDeserialize(3) instanceof RegisterUserCommand);
//        assertTrue(getByIndexAndDeserialize(4) instanceof ShowMessageHistoryCommand);
//    }
//
//    public void testDeserializeRegisterNoUsername() {
//        String command = "<SYS_COMMAND>id=2;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof RegisterUserCommand);
//    }
//
//    public void testDeserializeInvalidCommand() {
//        String command = "<SYS_COMMAND>id=-1;username=123;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof UnknownCommand);
//    }
//
//    public void testDeserializeCommandListOk() {
//        String command = "<SYS_COMMAND>id=4;";
//        Executable executableCommand = Protocol.deserialize(command).get(0);
//        assertTrue(executableCommand instanceof ShowCommandListCommand);
//    }
//
//    private Executable getByIndexAndDeserialize(int index) {
//        return Protocol.deserialize(serializedCommands.get(index)).get(0);
//    }

    @After
    public void close() {
        try {
            socket.close();
            in.close();
            out.close();
        }
        catch (Throwable ignore) {/*NOP*/}
    }
}
