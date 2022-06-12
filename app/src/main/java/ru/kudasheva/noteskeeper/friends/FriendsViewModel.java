package ru.kudasheva.noteskeeper.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.SingleLiveEvent;
import ru.kudasheva.noteskeeper.models.presentermodels.FriendInfoCard;
import ru.kudasheva.noteskeeper.models.vmmodels.User;

public class FriendsViewModel extends ViewModel {
    private static final String TAG = FriendsViewModel.class.getSimpleName();

    private final User user = DBManager.getInstance().getUser();

    public SingleLiveEvent<String> snackBarMessage = new SingleLiveEvent<>();
    public MutableLiveData<Commands> command = new MutableLiveData<>();
    public MutableLiveData<List<FriendInfoCard>> friendList = new MutableLiveData<>();
    public MutableLiveData<Boolean> progressIsVisible = new MutableLiveData<>();

    public void onFindFriendsButtonClicked() {
        command.setValue(Commands.OPEN_DIALOG);
    }

    public void tryToAddFriend(String username, Consumer<Boolean> consumer) {
        if (username.equals(user.getUsername())) {
            snackBarMessage.postValue("Can't add yourself to friend list");
            consumer.accept(false);
            return;
        }

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
        DBManager.getInstance().getFriends((friends) ->{
            friendList.postValue(convertToFriendsInfoCard(friends));
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

    private List<FriendInfoCard> convertToFriendsInfoCard(List<User> friends) {
        List<FriendInfoCard> friendInfoCards = new ArrayList<>();
        for (User friend : friends) {
            friendInfoCards.add(friend.createFriendInfoCard());
        }
        return friendInfoCards;
    }
}
