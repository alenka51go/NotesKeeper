package ru.kudasheva.noteskeeper.domain.models;

import java.util.ArrayList;
import java.util.List;

import ru.kudasheva.noteskeeper.data.models.UserDocument;
import ru.kudasheva.noteskeeper.presentation.models.FriendCard;

public class User {
    private final String username;
    private final String fullName;
    private final List<String> friends;

    public User(UserDocument user) {
        username = user.getUsername();
        fullName = user.getFullUsername();
        friends = user.getFriends();
    }

    public User() {
        username = "Null";
        fullName = "Full Null";
        friends = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getFriends() {
        return friends;
    }

    public FriendCard createFriendInfoCard() {
        return new FriendCard(getFullName());
    }
}
