package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.Utils;
import me.proartex.test.vitamin.chat.protocol.Protocol;

import java.nio.channels.SelectionKey;

//TODO: inner class?
public class CommandHandler {

    private Server server;
    private Session session;
    private RegisteredGroup registeredUsers;
    private UserGroup notRegisteredUsers;

    public CommandHandler(Server server, Session session, RegisteredGroup registeredUsers, NotRegisteredGroup notRegisteredUsers) {
        this.server = server;
        this.session = session;
        this.registeredUsers = registeredUsers;
        this.notRegisteredUsers = notRegisteredUsers;
    }

    public boolean isInRegisteredUserGroup(SelectionKey key) {
        return registeredUsers.containsUserWith(key);
    }

    public boolean isInNotRegisteredUserGroup(SelectionKey key) {
        return notRegisteredUsers.containsUserWith(key);
    }

    public User getUserWith(SelectionKey key) {
        UserGroup group = server.getClientGroup(key);
        return group.getUserWith(key);
    }

    public void sendCommandToUser(Serializable command, User user) {
        String serializedCommand = Protocol.serialize(command);
        user.addCommandToQueue(serializedCommand);
    }

    public void sendCommandToAllRegistered(Serializable command) {
        String serializedCommand = Protocol.serialize(command);
        registeredUsers.addCommandToAllUsers(serializedCommand);
    }

    public void addMessageToHistory(HistoryMessage message) {
        session.noteMessage(message);
    }

    public void dropUserWith(SelectionKey key) {
        server.cancelKey(key);
    }

    public boolean isTakenUsername(String username) {
        return registeredUsers.containsUserWith(username);
    }

    public void registerUser(User user, SelectionKey key) {
        notRegisteredUsers.remove(key);
        registeredUsers.add(key, user);

        //TODO: server event listener
        System.out.println(user.getUsername() + " sign in. Total: " + registeredUsers.count());
    }

    public int getRegisteredUsersCount() {
        return registeredUsers.count();
    }

    public String getAvailableCommands() {
        String[] commands = new String[] {
            "/exit - leave the chat",
            "/total - show number of users in chat",
        };

        return Utils.implodeStringArray(commands, Protocol.RESPONSE_DELIMITER);
    }

    public String getMessageHistory() {
        String[] messages = session.getFormattedMessageHistory();
        return Utils.implodeStringArray(messages, Protocol.RESPONSE_DELIMITER);
    }
}
