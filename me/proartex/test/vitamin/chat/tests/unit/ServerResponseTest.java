package me.proartex.test.vitamin.chat.tests.unit;


import junit.framework.Assert;
import me.proartex.test.vitamin.chat.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerResponseTest extends Assert {

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
    public void testForbiddenFieldsCommand() throws IOException {
        String response = sendRequest("key=null;handler=null");
        assertEquals("Unknown command 'null'", response);
    }

    @Ignore
    @Test
    public void testBrokenMessageCommand() throws IOException {
        sendRequest("id=2;username=Mike;");
        String response = sendRequest("id=1;message=a;qqq=1;key=3;");
        assertTrue(response.contains("Mike: a;qqq=1;key=3;"));
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

    @Test
    public void testRegisterInvalidCommand() throws IOException {
        String response = sendRequest("id=2;username=null;");
        assertEquals("Invalid 'register' command", response);
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
