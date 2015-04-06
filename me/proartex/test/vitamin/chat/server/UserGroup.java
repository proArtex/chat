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

    public void remove(SelectionKey key) {
        users.remove(key);
    }

    public int count() {
        return users.size();
    }

    public User getUserWith(SelectionKey key) {
        return users.get(key);
    }

    public void notifyUserWithKey(String message, SelectionKey key) {
        User user = getUserWith(key);
        sendMessageToUser(message, user);
    }

    public void notifyAllUsers(String message) {
        for (User user : users.values()) {
            sendMessageToUser(message, user);
        }
    }

//    public void notifyAllUsersExcludes(String message, SelectionKey key) {
//        for (Map.Entry<SelectionKey, User> pair: users.entrySet()) {
//            SelectionKey userKey = pair.getKey();
//            User user = pair.getValue();
//
//            if (userKey == key)
//                continue;
//
//            sendMessageToUser(message, user);
//        }
//    }

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

    //-
    public String getNameOfUserWith(SelectionKey key) {
        return users.get(key).getUsername();
    }

    //abstract
    public void removeUsersWithClosedConnection() {
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

    private void sendMessageToUser(String message, User user) {
        user.addMessageToQueue(message);
    }

    //TMP
    public Map<SelectionKey, User> getUsers() {
        return users;
    }

    public List<String> getUsersMessageQueue(SelectionKey key) {
        return users.get(key).getInboundMessageQueue();
    }
}
