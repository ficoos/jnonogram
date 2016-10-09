package org.bs.jnonogram.wui;

import java.util.HashSet;
import java.util.Set;

public class UserManager{
    private final HashSet<String> loggedInUsersSet = new HashSet<>();

    public boolean isLoggedIn(String username) {
        synchronized (loggedInUsersSet) {
            return loggedInUsersSet.contains(username);
        }
    }

    public boolean tryAddUser(String username) {
        synchronized (loggedInUsersSet) {
            if (isLoggedIn(username)) {
                return false;
            } else {
                loggedInUsersSet.add(username);
                return true;
            }
        }
    }

    public void removeUser(String username) {
        synchronized (loggedInUsersSet) {
            loggedInUsersSet.remove(username);
        }
    }

    public void reset() {
        loggedInUsersSet.clear();
    }

    public Set<String> getList() {
        return loggedInUsersSet;
    }
}
