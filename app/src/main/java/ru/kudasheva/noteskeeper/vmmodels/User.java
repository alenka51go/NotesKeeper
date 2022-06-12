package ru.kudasheva.noteskeeper.vmmodels;

import java.util.List;

import ru.kudasheva.noteskeeper.data.models.UserData;
import ru.kudasheva.noteskeeper.friends.FriendInfoCard;

public class User {
    private String username;
    private String fullName;
    private List<String> friends;

    public User(UserData user) {
        username = user.getUsername();
        fullName = user.getFullUsername();
        friends = user.getFriends();
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

    public FriendInfoCard createFriendInfoCard() {
        return new FriendInfoCard(getFullName());
    }
}
