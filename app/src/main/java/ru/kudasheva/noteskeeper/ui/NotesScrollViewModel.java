package ru.kudasheva.noteskeeper.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.kudasheva.noteskeeper.MyApplication;
import ru.kudasheva.noteskeeper.data.DataRepository;

public class NotesScrollViewModel extends ViewModel {
    private final DataRepository dataRepo = MyApplication.getDataRepo();

    public MutableLiveData<NotesScrollViewModel.Commands> activityCommand = new MutableLiveData<>();
    public String username;

    public void startPosition() {
        activityCommand.setValue(Commands.MAKE_INITIALIZATION);
        username = dataRepo.getUserName();
    }


    public enum Commands {
        MAKE_INITIALIZATION
    }
}
