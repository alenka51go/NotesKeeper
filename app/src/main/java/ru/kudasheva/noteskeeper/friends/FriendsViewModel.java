package ru.kudasheva.noteskeeper.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.function.Consumer;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;

public class FriendsViewModel extends ViewModel {
    private static final String TAG = FriendsViewModel.class.getSimpleName();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> command = new MutableLiveData<>();
    public MutableLiveData<List<FriendInfoCard>> friends = new MutableLiveData<>();
    public MutableLiveData<Boolean> progressIsVisible = new MutableLiveData<>();

    public void onFindFriendsButtonClicked() {
        command.setValue(Commands.OPEN_DIALOG);
    }

    public void tryToAddFriend(String username, Consumer<Boolean> consumer) {
        progressIsVisible.setValue(true);
        DBManager.getInstance().tryToAddFriend(username, (result) -> {
            boolean isFriendAdded = false;
            switch (result) {
                case SUCCESS: {
                    isFriendAdded = true;
                    break;
                }
                case DOESNT_EXIT: {
                    snackBarMessage.postValue("Can't find user with name " + username);
                    break;
                }
                case ALREADY_ADDED: {
                    snackBarMessage.postValue(username + " already added to friend list");
                    break;
                }
                case ERROR: {
                    snackBarMessage.postValue("Can't add user to friend list");
                    break;
                }
            }
            consumer.accept(isFriendAdded);
        });
        updateData();
    }

    public void updateData() {
        progressIsVisible.setValue(true);
        DBManager.getInstance().getFriendsInfoCard((friendsInfo) ->{
            friends.postValue(friendsInfo);
            progressIsVisible.postValue(false);
        });
    }

    enum Commands {
        OPEN_DIALOG
    }

    public enum ResultFriendAddition {
        SUCCESS,
        ALREADY_ADDED,
        DOESNT_EXIT,
        ERROR
    }
}
