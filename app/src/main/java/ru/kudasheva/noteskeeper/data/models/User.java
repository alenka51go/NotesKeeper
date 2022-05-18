package ru.kudasheva.noteskeeper.data.models;

import java.util.List;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private List<String> friendsId;

    public User() {}

    public User(String firstName) {
        id = firstName;
        this.firstName = firstName;
    }

    public User(String firstName, String lastName) {
        id = firstName + ' ' + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        String username = firstName;
        if (lastName != null && !lastName.isEmpty()) {
            username += ' ' + lastName;
        }
        return username;
    }

    public List<String> getFriendsId() {
        return friendsId;
    }
}
