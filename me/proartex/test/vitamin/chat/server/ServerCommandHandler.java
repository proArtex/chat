package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.CommandHandler;
import me.proartex.test.vitamin.chat.MsgConst;
import me.proartex.test.vitamin.chat.Utils;

import java.nio.channels.SelectionKey;
import java.util.Date;

public class ServerCommandHandler implements CommandHandler {

    private Server server;
    private Session session;
    private RegisteredGroup registeredUsers;
    private UserGroup notRegisteredUsers;

    public ServerCommandHandler(Server server, Session session, RegisteredGroup registeredUsers, NotRegisteredGroup notRegisteredUsers) {
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

    //TODO: divide?
    public void sendMessageToUser(String message, SelectionKey key) {
        UserGroup group = server.getClientGroup(key);
        group.notifyUserWithKey(message, key);
    }

    public void sendUserMessageToAllRegistered(SelectionKey key, String message) {
        User user = registeredUsers.getUserWith(key);
        String username = user.getUsername();
        Message messageObj = new Message(new Date(), username + ": " + message);

        registeredUsers.notifyAllUsers(messageObj.toString());
        session.noteMessage(messageObj);
    }

    public void dropUser(SelectionKey key) {
        server.cancelKey(key);
    }

    public void registerUser(SelectionKey key, String username) {
        if (registeredUsers.containsUserWith(username)) {
            notRegisteredUsers.notifyUserWithKey(MsgConst.REGISTER_FAIL, key);
            return;
        }

        String message = username + MsgConst.USER_SIGN_POSTFIX;
        User user = notRegisteredUsers.getUserWith(key);
        user.setUsername(username);

        registeredUsers.notifyAllUsers(message);
        notRegisteredUsers.remove(key);
        registeredUsers.add(key, user);

        //TODO: server event listener
        System.out.println(user.getUsername() + " sign in. Total: " + registeredUsers.count());

        registeredUsers.notifyUserWithKey(MsgConst.REGISTER_SUCCESS, key);
    }

    public void sendRegisteredCountToUser(SelectionKey key) {
        int total = registeredUsers.count();
        String message = MsgConst.TOTAL_USERS_PREFIX + String.valueOf(total);
        registeredUsers.notifyUserWithKey(message, key);
    }

    public void sendAvailableCommandsToUser(SelectionKey key) {
        String[] commands = new String[] {
                "/exit - leave the chat",
                "/total - show number of users in chat",
        };

        String message = Utils.implodeStringArray(commands, Utils.LINE_SEPARATOR);
        registeredUsers.notifyUserWithKey(message, key);
    }

    public void sendMessageHistoryToUser(SelectionKey key) {
        //TODO: solve empty case
        String[] messages = session.getFormattedMessageHistory();
        String message = Utils.implodeStringArray(messages, Utils.LINE_SEPARATOR);
        registeredUsers.notifyUserWithKey(message, key);
    }
}
