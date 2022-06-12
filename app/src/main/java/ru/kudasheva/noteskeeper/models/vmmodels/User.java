package ru.kudasheva.noteskeeper.models.vmmodels;

import java.util.List;

import ru.kudasheva.noteskeeper.models.datamodels.UserData;
import ru.kudasheva.noteskeeper.models.presentermodels.FriendInfoCard;

public class User {
    private final String username;
    private final String fullName;
    private final List<String> friends;

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
