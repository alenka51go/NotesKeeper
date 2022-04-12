package ru.kudasheva.noteskeeper;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Collection;

public class NotesScrollActivity extends AppCompatActivity {
    boolean isMenuOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notes_scroll);

        setUserName();
        setRecyclerView();
        setOnMenuListener();
    }

    private void setUserName() {
        // TODO загушка
        String userName = "Alena Kudasheva";
        TextView name = findViewById(R.id.user_name);
        name.setText(userName);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CustomRecyclerAdapter noteAdapter = new CustomRecyclerAdapter();
        recyclerView.setAdapter(noteAdapter);

        Collection<NoteShortCard> notes = loadNotes();
        noteAdapter.setItems(notes);
    }

    private Collection<NoteShortCard> loadNotes() {
        // TODO заглушка
        return Arrays.asList(
                new NoteShortCard("First note", "10.01.22"),
                new NoteShortCard("Second note", "10.01.22"),
                new NoteShortCard("Third note", "10.01.22"),
                new NoteShortCard("Fourth note", "10.01.22"),
                new NoteShortCard("Fifth note", "10.01.22"),
                new NoteShortCard("Sixth note", "10.01.22"),
                new NoteShortCard("Seventh note", "10.01.22"),
                new NoteShortCard("Eighth note", "10.01.22"),
                new NoteShortCard("Ninth note", "10.01.22"),
                new NoteShortCard("Tenth note", "10.01.22"),
                new NoteShortCard("Eleventh note", "10.01.22"),
                new NoteShortCard("Twelfth note", "10.01.22")
        );
    }

    private void setOnActionButtonsListener() {
        FloatingActionButton fabCreate = findViewById(R.id.create_note_button);
        FloatingActionButton fabAddContact = findViewById(R.id.add_contact_button);
        FloatingActionButton fabChangeUse = findViewById(R.id.change_user_button);

        fabCreate.setOnClickListener((View v) -> {
            Intent intent = new Intent(NotesScrollActivity.this, CreateNoteActivity.class);
            startActivity(intent);
        });

        fabAddContact.setOnClickListener((View v) -> {
            // TODO добавить действие
        });

        fabChangeUse.setOnClickListener((View v) -> {
            // TODO добавить действие
        });
    }

    private void setOnMenuListener() {
        FloatingActionButton fabMenu = findViewById(R.id.fab_menu_action);
        LinearLayout fabCreate = findViewById(R.id.fab_add_note_action);
        LinearLayout fabAddContact = findViewById(R.id.fab_add_contact_action);
        LinearLayout fabChangeUse = findViewById(R.id.fab_change_user_action);

        setOnActionButtonsListener();

        fabMenu.setOnClickListener((View v) -> {
            if (!isMenuOpen) {
                isMenuOpen=true;
                fabMenu.animate().rotationBy(-45f);
                fabCreate.setVisibility(View.VISIBLE);
                fabAddContact.setVisibility(View.VISIBLE);
                fabChangeUse.setVisibility(View.VISIBLE);
                fabCreate.animate().translationY(-getResources().getDimension(R.dimen.standard_200));
                fabAddContact.animate().translationY(-getResources().getDimension(R.dimen.standard_135));
                fabChangeUse.animate().translationY(-getResources().getDimension(R.dimen.standard_70));
            } else {
                isMenuOpen = false;
                fabMenu.animate().rotationBy(45f);
                fabCreate.animate().translationY(0);
                fabAddContact.animate().translationY(0);
                fabChangeUse.animate().translationY(0);
                fabChangeUse.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!isMenuOpen) {
                            fabCreate.setVisibility(View.GONE);
                            fabAddContact.setVisibility(View.GONE);
                            fabChangeUse.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        });
    }
}
