package ru.kudasheva.noteskeeper.data.models;

import java.util.List;

public class UsersBase {
    private String id;
    private String rev;
    private List<User> usernames;

    public boolean contains(String username) {
        if (username == null) {
            return false;
        }
        // TODO тут возможно будет проходить какая-то другая проверка?
        for (User user : usernames) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String getId () {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public List<User> getAllUsers() {
        return usernames;
    }
}
