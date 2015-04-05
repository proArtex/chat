package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.MsgConst;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserGroup {

    private Map<SelectionKey, User> users;

    public UserGroup() {
        users = new HashMap<>();
    }

    public void add(SelectionKey key) {
        add(key, new User());
    }

    public void add(SelectionKey key, User user) {
        users.put(key, user);
    }

    public void dismiss(SelectionKey key) {
        users.remove(key);
    }

    public int count() {
        return users.size();
    }

    public void notifyAllUsers(String message) {
        for (Map.Entry<SelectionKey, User> client: users.entrySet()) {
            client.getValue().getMessageQueue().add(message);
        }
    }

    public boolean containsUserWith(SelectionKey key) {
        return users.get(key) != null;
    }

    public boolean containsUserWith(String username) {
        for (Map.Entry<SelectionKey, User> client: users.entrySet()) {
            if (client.getValue().getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    public String getNameOfUserWith(SelectionKey key) {
        return users.get(key).getUsername();
    }

    public void removeUsersWithCanceledConnection() {
        Iterator<Map.Entry<SelectionKey, User>> iterator = users.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<SelectionKey, User> pair = iterator.next();
            SelectionKey key = pair.getKey();

            if (!key.isValid()) {
                String username = getNameOfUserWith(key);
                String message = username + MsgConst.USER_LEFT_POSTFIX;
                iterator.remove();

                //TODO: solve registered problem
                if (true) {
                    notifyAllUsers(message);
                    System.out.println(username + " sign out. Total: " + count());
                }
            }
        }
    }

    //TMP
    public Map<SelectionKey, User> getUsers() {
        return users;
    }

    public List<String> getUsersMessageQueue(SelectionKey key) {
        return users.get(key).getMessageQueue();
    }

    public User getConnectionWith(SelectionKey key) {
        return users.get(key);
    }
}
