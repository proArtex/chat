package me.proartex.test.vitamin.chat;


import junit.framework.TestCase;
import me.proartex.test.vitamin.chat.commands2.Executable2;
import me.proartex.test.vitamin.chat.commands2.InvalidCommand2;
import me.proartex.test.vitamin.chat.commands2.RegisterUserCommand2;

public class TestProtocol extends TestCase {

    public void testDeserializeNoId() {
        String command = "<SYS_COMMAND>a=2;username=123;";
        Executable2 executableCommand = Protocol.deserialize2(command).get(0);
        assertTrue(executableCommand instanceof InvalidCommand2);
    }

    public void testDeserializeRegister() {
        String command = "<SYS_COMMAND>id=2;username=123;";
        Executable2 executableCommand = Protocol.deserialize2(command).get(0);
        assertTrue(executableCommand instanceof RegisterUserCommand2);
    }
}
