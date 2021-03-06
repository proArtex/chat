package me.proartex.test.vitamin.chat.server;

import me.proartex.test.vitamin.chat.Serializable;
import me.proartex.test.vitamin.chat.TextConst;
import me.proartex.test.vitamin.chat.client.commands.SystemMessageCommand;
import me.proartex.test.vitamin.chat.protocol.Protocol;

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

    public void addCommandToAllUsers(String command) {
        for (User user : _users.values()) {
            _sendCommandToUser(command, user);
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
                String message = username + TextConst.USER_LEFT_POSTFIX;

                Serializable command = new SystemMessageCommand(message);
                String serializedCommand = Protocol.serialize(command);
                addCommandToAllUsers(serializedCommand);

                iterator.remove();

                //TODO: server event listener
                System.out.println(username + " sign out. Total: " + count());
            }
        }
    }
}
