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

import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.data.models.Note;
import ru.kudasheva.noteskeeper.data.models.User;
import ru.kudasheva.noteskeeper.friends.FriendInfoCard;

public class CreateNoteViewModel extends ViewModel {
    private final List<String> selectedFriends = new ArrayList<>();
    private final String username = DBManager.getInstance().getFullUsername();

    public String title;
    public String noteBody;

    public final String[]  contactList  = getContacts();
    public final boolean[] checkedItems = new boolean[contactList.length];

    public MutableLiveData<CreateNoteViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void onAddFriendButtonClicked() {
        activityCommand.setValue(Commands.OPEN_SELECTED_FRIEND_DIALOG);
    }

    public void onSaveNoteButtonClicked() {
        selectedFriends.add(username); // чтобы мы тоже отображалдись в пошаренных юзерах
        Note note = new Note(username, title, noteBody,
                getCurrentDate(), selectedFriends);
        DBManager.getInstance().addNote(note);

        activityCommand.setValue(Commands.CLOSE_ACTIVITY);
    }

    private String[] getContacts() {
        // TODO поменять, пока там загдушка на пользователей

        /*List<User> friends = DBManager.getInstance().getFriends();

        String[] friendsFullName = new String[friends.size()];

        for (int i = 0; i < friends.size(); i++) {
            User user = friends.get(i);
            friendsFullName[i] = user.getFullUsername();
        }
        return friendsFullName;*/

        List<FriendInfoCard> friendsInfo = new ArrayList<>();
        List<String> rawFriends = null;

        if (rawFriends != null) {
            for (String friendInfo : rawFriends) {
                FriendInfoCard friendInfoCard = new FriendInfoCard(friendInfo);
                friendsInfo.add(friendInfoCard);
            }
        }

        String[] friendsNames = new String[friendsInfo.size()];

        for (int i = 0; i < friendsInfo.size(); i++) {
            friendsNames[i] = friendsInfo.get(i).getName();
        }
        return friendsNames;
    }

    public void okClicked() {
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i] && !selectedFriends.contains(contactList[i])) {
                selectedFriends.add(contactList[i]);
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
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return df.format(currentDate);
    }

    enum Commands {
        OPEN_SELECTED_FRIEND_DIALOG,
        CLOSE_ACTIVITY
    }
}
