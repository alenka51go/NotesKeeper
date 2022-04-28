package ru.kudasheva.noteskeeper.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.kudasheva.noteskeeper.R;

public class NoteBrowseActivity extends AppCompatActivity {
    private static final String TAG = NoteBrowseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_browse);

        setRecyclerView();
        setActionButton();
        setSendButton();
        loadDates();
    }

    private void setSendButton() {
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(v ->{
            EditText leaveCommentBox = findViewById(R.id.comment_box);
            String newComment = leaveCommentBox.getText().toString();
            Log.d(TAG, newComment);
            // TODO собрать данные о пользователе и опубликовать их с комментом
            leaveCommentBox.setText("");
        });
    }

    private void loadDates() {

        TextView header = findViewById(R.id.title_box);
        header.setText("Title");
    }

    private void setActionButton() {
        Button button = findViewById(R.id.button_action);
        button.setOnClickListener((View v) -> {
            PopupMenu menu = new PopupMenu(NoteBrowseActivity.this, button);
            menu.getMenuInflater().inflate(R.menu.popup_note_action_menu, menu.getMenu());

            menu.setOnMenuItemClickListener((MenuItem menuItem) -> {
                int action = menuItem.getItemId();
                if (action == R.id.deleteNoteItem) {
                    // TODO вернуть информацию об удалении заметки
                    finish();
                }
                return true;
            });
            menu.show();
        });
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_note_and_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MultipleTypesAdapter noteAdapter = new MultipleTypesAdapter();
        recyclerView.setAdapter(noteAdapter);

        List<InfoCard> note = Collections.singletonList(loadNote());
        noteAdapter.setItems(note);

        List<InfoCard> comments = loadComments();
        noteAdapter.setItems(comments);
    }

    private List<InfoCard> loadComments() {
        // TODO загулшка, сделать выгрузку комментариев
        return Arrays.asList(
                new CommentInfoCard("Bob", "10.01.11", "Wow!"),
                new CommentInfoCard("Alice", "10.01.11", "Cool!"),
                new CommentInfoCard("Ron", "11.01.11", "Aunt Petunia often said that Dudley looked like a baby angel - Harry often said that Dudley looked like a pig in a wig."),
                new CommentInfoCard("Harry", "12.01.11", "booble!"),
                new CommentInfoCard("Hermione", "13.01.11", "levIosa not leviosA!"),
                new CommentInfoCard("Draco", "10.03.11", "Mudblood"),
                new CommentInfoCard("Dobby", "11.01.11", "The owner gave Dobby a sock!"),
                new CommentInfoCard("Volodia", "14.01.11", "Avada!")
        );
    }

    private InfoCard loadNote() {
        // TODO загулшка, сделать выгрузку внутрянки комментария
        return new NoteFullCard("Harry Potter and the Philosopher's Stone is the first novel in the Harry Potter series written by J. K. Rowling. The book was first published on 26 June 1997[1] by Bloomsbury in London and was later made into a film of the same name.\n" +
                "\n" +
                "The book was released in the United States under the name Harry Potter and the Sorcerer's Stone because the publishers were concerned that most American readers would not be familiar enough with the term \"Philosopher's Stone\". However, this decision led to criticism by the British public who felt it shouldn't be changed due to the fact it was an English book.", "Alena Kudasheva", "12.12.12");
    }
}