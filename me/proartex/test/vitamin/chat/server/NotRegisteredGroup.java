package me.proartex.test.vitamin.chat.server;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NotRegisteredGroup extends UserGroup {

    protected Map<SelectionKey, User> _users;

    public NotRegisteredGroup() {
        this._users = new HashMap<>();
    }

    @Override
    protected Map<SelectionKey, User> _users() {
        return _users;
    }

    @Override
    public void removeUsersWithClosedConnection() {
        Iterator<SelectionKey> iterator = _users.keySet().iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();

            if (!key.isValid())
                iterator.remove();
        }
    }
}
