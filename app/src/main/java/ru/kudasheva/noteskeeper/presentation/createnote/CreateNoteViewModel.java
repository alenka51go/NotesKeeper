package ru.kudasheva.noteskeeper.presentation.createnote;

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
import ru.kudasheva.noteskeeper.data.models.NoteDocument;
import ru.kudasheva.noteskeeper.domain.Util;
import ru.kudasheva.noteskeeper.domain.models.User;

public class CreateNoteViewModel extends ViewModel {
    private final User user = DBManager.getInstance().getUser();

    private final List<String> selectedFriends = new ArrayList<>();
    public String[]  contactList;
    public List<String> usernameContactList;
    public boolean[] checkedItems;

    public MutableLiveData<String> noteBody = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();
    public MutableLiveData<CreateNoteViewModel.Commands> activityCommand = new MutableLiveData<>();

    public void initData() {
        DBManager.getInstance().getFriends((friends) -> {
            contactList = new String[friends.size()];
            usernameContactList = new ArrayList<>();

            for (int i = 0; i < friends.size(); i++) {
                User user = friends.get(i);
                usernameContactList.add(user.getUsername());
                contactList[i] = user.getFullName();
            }

            checkedItems = new boolean[contactList.length];
        });
    }

    public void onAddFriendButtonClicked() {
        activityCommand.setValue(Commands.OPEN_SELECTED_FRIEND_DIALOG);
    }

    public void onSaveNoteButtonClicked() {
        selectedFriends.add(user.getUsername());
        NoteDocument noteDocument = new NoteDocument(user.getUsername(), title.getValue(), noteBody.getValue(),
                Util.getCurrentDate(), selectedFriends);
        DBManager.getInstance().addNote(noteDocument);

        activityCommand.setValue(Commands.CLOSE_ACTIVITY);
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

    enum Commands {
        OPEN_SELECTED_FRIEND_DIALOG,
        CLOSE_ACTIVITY
    }
}
