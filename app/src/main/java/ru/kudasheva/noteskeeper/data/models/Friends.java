package ru.kudasheva.noteskeeper.data.models;

import java.util.List;

public class Friends {
    private String id;
    private String rev;
    private String username;
    private List<String> friends;

    public String getId() {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAllFriends() {
        return friends;
    }
}
