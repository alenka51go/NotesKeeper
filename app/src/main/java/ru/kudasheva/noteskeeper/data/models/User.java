package ru.kudasheva.noteskeeper.data.models;

public class User {
    private String id;
    private String firstName;
    private String lastName;

    public User() {}

    public User(String firstName) {
        this.firstName = firstName;
    }

    public User(String firstName, String lastName) {
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
}
