package ru.kudasheva.noteskeeper.friends;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;

public class FriendsViewModel extends ViewModel {
    private static final String TAG = FriendsViewModel.class.getSimpleName();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> command = new MutableLiveData<>();
    public MutableLiveData<List<FriendInfoCard>> friends = new MutableLiveData<>(loadFriends());

    public void onFindFriendsButtonClicked() {
        command.setValue(Commands.OPEN_DIALOG);
    }

    public boolean tryToAddFriend(String username) {
        // TODO дргуая база
        /*if (dataRepo.checkIfUserExist(username)) {
            if (!dataRepo.addNewFriend(username)) {
                Log.d(TAG, "Can't add friend");
            }
            friends.setValue(loadFriends());
            return true;
        } else {
            snackBarMessage.setValue("Can't find user with name " + username);
            return false;
        }*/
        return false;
    }

    private List<FriendInfoCard> loadFriends() {
        List<FriendInfoCard> friendInfoCards = new ArrayList<>();
        // TODO другая база
        /*List<String> rawFriends = dataRepo.getFriends();*/
        List<String> rawFriends = null;

        if (rawFriends != null) {
            for (String friendInfo : rawFriends) {
                FriendInfoCard friendInfoCard = new FriendInfoCard(friendInfo);
                friendInfoCards.add(friendInfoCard);
            }
        }


        return friendInfoCards;
    }

    enum Commands {
        OPEN_DIALOG
    }
}
