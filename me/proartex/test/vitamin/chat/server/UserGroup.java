package me.proartex.test.vitamin.chat.server;

import java.nio.channels.SelectionKey;
import java.util.Map;

public abstract class UserGroup {

    protected abstract Map<SelectionKey, User> _users();

    public abstract void removeUsersWithClosedConnection();

    public void add(SelectionKey key, User user) {
        _users().put(key, user);
    }

    public void remove(SelectionKey key) {
        _users().remove(key);
    }

    public int count() {
        return _users().size();
    }

    public boolean containsUserWith(SelectionKey key) {
        return getUserWith(key) != null;
    }

    public User getUserWith(SelectionKey key) {
        return _users().get(key);
    }

    protected void _sendCommandToUser(String command, User user) {
        user.addCommandToQueue(command);
    }

    public Map<SelectionKey, User> getUsers() {
        return _users();
    }

}
