package ru.kudasheva.noteskeeper.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;

public class FriendsViewModel extends ViewModel {
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> command = new MutableLiveData<>();
    public MutableLiveData<List<FriendInfoCard>> friends = new MutableLiveData<>(loadFriends());

    public void onFindFriendsButtonClicked() {
        command.setValue(Commands.OPEN_DIALOG);
    }

    public boolean checkIfUserExist(String username) {
        if (dataRepo.checkIfUserExist(username)) {
            dataRepo.addNewFriend(new FriendInfoCard(username));
            friends.setValue(loadFriends());
            return true;
        } else {
            snackBarMessage.setValue("Can't find user with name " + username);
            return false;
        }
    }

    private List<FriendInfoCard> loadFriends() {
        return dataRepo.getListOfFriendInfoCards();
    }

    enum Commands {
        OPEN_DIALOG
    }
}
