package ru.kudasheva.noteskeeper.data.models;

import java.util.List;

public class User {
    private String _id;
    private String _rev;
    private String firstname;
    private String lastname;
    private List<String> friends;

    public User() {}

    public User(String firstname) {
        _id = firstname;
        this.firstname = firstname;
    }

    public User(String firstname, String lastname) {
        _id = firstname + ' ' + lastname;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User(String _id, String _rev, String firstname, String lastname, List<String> friends) {
        this._id = _id;
        this._rev = _rev;
        this.firstname = firstname;
        this.lastname = lastname;
        this.friends = friends;
    }

    public String getUsername() {
        return _id;
    }

    public String getFullUsername() {
        String username = firstname;
        if (lastname != null && !lastname.isEmpty()) {
            username += ' ' + lastname;
        }
        return username;
    }

    public List<String> getFriends() {
        return friends;
    }
}
