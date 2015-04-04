package me.proartex.test.vitamin.chat.tests.unit;


import junit.framework.TestCase;
import me.proartex.test.vitamin.chat.protocol.Protocol;
import me.proartex.test.vitamin.chat.server.commands.*;

import java.util.ArrayList;
import java.util.List;

public class ProtocolTest extends TestCase {

    private List<String> serializedCommands = new ArrayList<>();

    public void testDeserializeInvalidString() {
        String command = "abrakadabra=1;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand);
    }

    public void testDeserializeNoId() {
        String command = "<SYS_COMMAND>a=2;username=123;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand);
    }

    public void testDeserializeBigId() {
        String command = "<SYS_COMMAND>id=999999999999999999;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand);
    }

    public void testDeserializeWrongId() {
        String command = "<SYS_COMMAND>id=&=1+2;username=123;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand);
    }

    public void testRegisterUserCommand() {
        serializedCommands.clear();

        serializedCommands.add(0, "<SYS_COMMAND>id=2;username=123;");
        serializedCommands.add(1, "<SYS_COMMAND>id=2;username=;");
        serializedCommands.add(2, "<SYS_COMMAND>id=2;username=123;username=555;");
        serializedCommands.add(3, "<SYS_COMMAND>id=2;");
        serializedCommands.add(4, "<SYS_COMMAND>id=2;id=3;");

        assertTrue(getByIndexAndDeserialize(0) instanceof RegisterUserCommand);
        assertTrue(getByIndexAndDeserialize(1) instanceof RegisterUserCommand);
        assertTrue(getByIndexAndDeserialize(2) instanceof RegisterUserCommand);
        assertTrue(getByIndexAndDeserialize(3) instanceof InvalidCommand);
        assertTrue(getByIndexAndDeserialize(4) instanceof ShowMessageHistoryCommand);
    }

    public void testDeserializeRegisterNoUsername() {
        String command = "<SYS_COMMAND>id=2;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand);
    }

    public void testDeserializeInvalidCommand() {
        String command = "<SYS_COMMAND>id=-1;username=123;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand);
    }

    public void testDeserializeCommandListOk() {
        String command = "<SYS_COMMAND>id=4;";
        Executable executableCommand = Protocol.deserialize(command).get(0);
        assertTrue(executableCommand instanceof ShowCommandListCommand);
    }

    private Executable getByIndexAndDeserialize(int index) {
        return Protocol.deserialize(serializedCommands.get(index)).get(0);
    }
}
