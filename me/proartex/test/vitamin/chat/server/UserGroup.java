package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.Executable;

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

//    public void notifyUserWithKey(String message, SelectionKey key) {
//        User user = getUserWith(key);
//        _sendMessageToUser(message, user);
//    }

    //NEW
    public void addCommandToUserWithKey(Executable command, SelectionKey key) {
        User user = getUserWith(key);
        _sendCommandToUser(command, user);
    }

//    protected void _sendMessageToUser(String message, User user) {
//        user.addMessageToQueue(message);
//    }

    //NEW
    protected void _sendCommandToUser(Executable command, User user) {
        user.addCommandToQueue(command);
    }


    //TMP
    public Map<SelectionKey, User> getUsers() {
        return _users();
    }

}
