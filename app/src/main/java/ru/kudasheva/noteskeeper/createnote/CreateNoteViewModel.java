package ru.kudasheva.noteskeeper.createnote;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;
import ru.kudasheva.noteskeeper.friends.FriendInfoCard;

public class CreateNoteViewModel extends ViewModel {
    private final List<String> selectedFriends = new ArrayList<>();
    private final String username = DBManager.getInstance().getUsername();
    private final String userFullName = DBManager.getInstance().getFullUsername();

    public String title;
    public String noteBody;

    public final String[]  contactList  = getContacts();
    public List<String> usernameContactList;
    public final boolean[] checkedItems = new boolean[contactList.length];

    public MutableLiveData<CreateNoteViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void onAddFriendButtonClicked() {
        activityCommand.setValue(Commands.OPEN_SELECTED_FRIEND_DIALOG);
    }

    public void onSaveNoteButtonClicked() {
        selectedFriends.add(username);
        Note note = new Note(username, title, noteBody,
                getCurrentDate(), selectedFriends);
        DBManager.getInstance().addNote(note);

        activityCommand.setValue(Commands.CLOSE_ACTIVITY);
    }

    private String[] getContacts() {
        List<User> friends = DBManager.getInstance().getFriends();

        String[] friendsUsername = new String[friends.size()];
        usernameContactList = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++) {
            User user = friends.get(i);
            usernameContactList.add(user.getUsername());
            friendsUsername[i] = user.getFullUsername();
        }
        return friendsUsername;
    }

    public void okClicked() {
        selectedFriends.clear();
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                selectedFriends.add(usernameContactList.get(i));
            }
        }
    }

    public void selectedAllClicked() {
        Arrays.fill(checkedItems, true);
    }

    public void setCheckedItem(int which, boolean isChecked) {
        checkedItems[which] = isChecked;
    }

    private String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' h:mm a", Locale.getDefault());
        return df.format(currentDate);
    }

    enum Commands {
        OPEN_SELECTED_FRIEND_DIALOG,
        CLOSE_ACTIVITY
    }
}
