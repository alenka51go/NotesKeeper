package ru.kudasheva.noteskeeper.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.kudasheva.noteskeeper.R;

public class CreateNoteActivity extends AppCompatActivity {
    private static final String TAG = CreateNoteActivity.class.getSimpleName();

    List<String> selectedFriend = new ArrayList<>();
    final String[]  contactList  = getContacts();
    final boolean[] checkedItems = new boolean[contactList.length];

    String header;
    String noteText;
    String dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_create);

        setAddFriendButton();
        setOnSaveButton();
    }

    private void setOnSaveButton() {
        Button button = findViewById(R.id.save_note_button);
        button.setOnClickListener((View v) -> {
            EditText title = findViewById(R.id.enter_title_box);
            header = title.getText().toString();

            EditText body = findViewById(R.id.enter_note_box);
            noteText = body.getText().toString();

            Log.d(TAG, header);
            Log.d(TAG, noteText);
            for (String friend : selectedFriend) {
                Log.d(TAG, friend);
            }

            Date currentDate = new Date();

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            dateText = dateFormat.format(currentDate);
            Log.d(TAG, dateText);

            // TODO вернуться на главную страницу и схранить новую заметку в базу

            finish();
        });
    }

    private void setAddFriendButton() {
        Button button = findViewById(R.id.add_friend_button);
        button.setOnClickListener((View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);

            builder.setTitle("Select friends for sharing");

            builder.setMultiChoiceItems(contactList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    checkedItems[which] = isChecked;
                }
            });

            builder.setCancelable(false);

            builder.setPositiveButton("Done", (dialog, which) -> {
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i] && !selectedFriend.contains(contactList[i])) {
                        selectedFriend.add(contactList[i]);
                    }
                }
            });

            builder.setNegativeButton("CANCEL", (dialog, which) -> {
            });

            builder.setNeutralButton("CLEAR ALL", (dialog, which) ->
                    Arrays.fill(checkedItems, false));

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private String[]  getContacts() {
        // TODO заглушка
        return new String[]{
            "Harry",
            "Ron",
            "Hermione",
            "Draco",
            "Fred",
            "Volodia"
        };
    }
}