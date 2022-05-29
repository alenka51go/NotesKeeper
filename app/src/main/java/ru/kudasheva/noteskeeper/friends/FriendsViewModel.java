package ru.kudasheva.noteskeeper.friends;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.data.models.User;

public class FriendsViewModel extends ViewModel {
    private static final String TAG = FriendsViewModel.class.getSimpleName();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> command = new MutableLiveData<>();
    public MutableLiveData<List<FriendInfoCard>> friends = new MutableLiveData<>(loadFriends());

    public void onFindFriendsButtonClicked() {
        command.setValue(Commands.OPEN_DIALOG);
    }

    public boolean tryToAddFriend(String username) {
        if (DBManager.getInstance().checkIfUserExist(username)) {
            if (!DBManager.getInstance().addFriend(username)) {
                Log.d(TAG, "Can't add friend");
            }
            friends.setValue(loadFriends());
            return true;
        } else {
            snackBarMessage.setValue("Can't find user with name " + username);
            return false;
        }
    }

    private List<FriendInfoCard> loadFriends() {
        List<User> friends = DBManager.getInstance().getFriends();
        List<FriendInfoCard> friendInfoCards = new ArrayList<>();

        for (User user : friends) {
            FriendInfoCard friendInfoCard = new FriendInfoCard(user.getFullUsername());
            friendInfoCards.add(friendInfoCard);
        }

        return friendInfoCards;
    }

    public void update() {
        friends.setValue(loadFriends());
    }

    enum Commands {
        OPEN_DIALOG
    }
}
