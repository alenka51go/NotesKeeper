package ru.kudasheva.noteskeeper.createnote;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityNoteCreateBinding;


public class CreateNoteActivity extends AppCompatActivity {
    private static final String TAG = CreateNoteActivity.class.getSimpleName();

    private CreateNoteViewModel createNoteViewModel;
    private ActivityNoteCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNoteViewModel = ViewModelProviders.of(this).get(CreateNoteViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_create);
        binding.setViewModel(createNoteViewModel);

        observeLiveData();
    }

    private void observeLiveData() {
        createNoteViewModel.activityCommand.observe(this, activityCommand -> {
            if (activityCommand == CreateNoteViewModel.Commands.CLOSE_ACTIVITY) {
                finish();
            } else if (activityCommand == CreateNoteViewModel.Commands.OPEN_SELECTED_FRIEND_DIALOG) {
                setDialogActivity();
            }
        });
    }

    private void setDialogActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);

        AlertDialog dialog = builder
                .setCancelable(false)
                .setTitle(R.string.select_friends)
                .setMultiChoiceItems(createNoteViewModel.contactList, createNoteViewModel.checkedItems,
                        (dialog_, which, isChecked) -> createNoteViewModel.setCheckedItem(which, isChecked))
                .setPositiveButton(android.R.string.ok, (dialog_, which) -> createNoteViewModel.okClicked())
                .setNegativeButton(android.R.string.cancel, (dialog_, which) -> {
                })
                .setNeutralButton(android.R.string.selectAll, (dialog_, which) -> createNoteViewModel.selectedAllClicked())
                .create();

        dialog.show();
    }
}