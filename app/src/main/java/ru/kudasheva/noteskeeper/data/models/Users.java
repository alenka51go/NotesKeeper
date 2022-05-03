package ru.kudasheva.noteskeeper.data.models;

import java.util.List;

public class Users {
    private String id;
    private String rev;
    private List<String> usernames;

    public boolean contains(String username) {
        if (username == null) {
            return false;
        }
        return usernames.contains(username);
    }

    public String getId () {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public List<String> getAllUsers() {
        return usernames;
    }
}
