package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.MsgConst;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegisteredGroup extends UserGroup {

    protected Map<SelectionKey, User> _users;

    public RegisteredGroup() {
        this._users = new HashMap<>();
    }

    @Override
    protected Map<SelectionKey, User> _users() {
        return _users;
    }

    public void notifyAllUsers(String message) {
        for (User user : _users.values()) {
            _sendMessageToUser(message, user);
        }
    }

    public boolean containsUserWith(String username) {
        for (Map.Entry<SelectionKey, User> client: _users.entrySet()) {
            if (client.getValue().getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeUsersWithClosedConnection() {
        Iterator<Map.Entry<SelectionKey, User>> iterator = _users.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SelectionKey, User> pair = iterator.next();
            SelectionKey key = pair.getKey();
            User user = pair.getValue();

            if (!key.isValid()) {
                String username = user.getUsername();
                String message = username + MsgConst.USER_LEFT_POSTFIX;
                iterator.remove();

                notifyAllUsers(message);
                //TODO: server event listener
                System.out.println(username + " sign out. Total: " + count());
            }
        }
    }
}
