package ru.kudasheva.noteskeeper.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Collection;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityNotesScrollBinding;
import ru.kudasheva.noteskeeper.login.LoginActivity;

public class NotesScrollActivity extends AppCompatActivity {
    private static final String TAG = NotesScrollActivity.class.getSimpleName();

    private NotesScrollViewModel notesScrollViewModel;
    private ActivityNotesScrollBinding binding;

    private CustomRecyclerAdapter noteAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notesScrollViewModel = ViewModelProviders.of(this).get(NotesScrollViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notes_scroll);
        binding.setViewModel(notesScrollViewModel);
        setRecyclerView();

        observeLiveData();
    }

    private void observeLiveData() {
        notesScrollViewModel.activityCommand.observe(this, (activityCode) -> {
            if (activityCode == NotesScrollViewModel.Commands.OPEN_CREATE_NOTE_ACTIVITY) {
                Intent intent = new Intent(NotesScrollActivity.this, CreateNoteActivity.class);
                startActivity(intent);

            } else if (activityCode == NotesScrollViewModel.Commands.OPEN_FRIENDS_LIST_ACTIVITY) {
                Intent intent = new Intent(NotesScrollActivity.this, FriendsActivity.class);
                startActivity(intent);

            } else if (activityCode == NotesScrollViewModel.Commands.OPEN_LOGIN_ACTIVITY) {
                Intent intent = new Intent(NotesScrollActivity.this, LoginActivity.class);
                startActivity(intent);

            } else if (activityCode == NotesScrollViewModel.Commands.OPEN_BROWSE_NOTE_ACTIVITY) {
                Intent intent = new Intent(NotesScrollActivity.this, NoteBrowseActivity.class);
                startActivity(intent);

            } else if (activityCode == NotesScrollViewModel.Commands.OPEN_MENU) {
                binding.fabMenuButton.animate().rotationBy(-45f);
                binding.fabAddNoteAction.setVisibility(View.VISIBLE);
                binding.fabAddContactAction.setVisibility(View.VISIBLE);
                binding.fabChangeUserAction.setVisibility(View.VISIBLE);
                binding.fabAddNoteAction.animate().translationY(-getResources().getDimension(R.dimen.standard_200));
                binding.fabAddContactAction.animate().translationY(-getResources().getDimension(R.dimen.standard_135));
                binding.fabChangeUserAction.animate().translationY(-getResources().getDimension(R.dimen.standard_70));

            } else if (activityCode == NotesScrollViewModel.Commands.CLOSE_MENU) {
                binding.fabMenuButton.animate().rotationBy(45f);
                binding.fabAddNoteAction.animate().translationY(0);
                binding.fabAddContactAction.animate().translationY(0);
                binding.fabChangeUserAction.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        notesScrollViewModel.animationEnd();
                    }
                });

            } else if (activityCode == NotesScrollViewModel.Commands.HIDE_EXTRA_MENU_INFO) {
                binding.fabAddNoteAction.setVisibility(View.GONE);
                binding.fabAddContactAction.setVisibility(View.GONE);
                binding.fabChangeUserAction.setVisibility(View.GONE);
            }
        });

        notesScrollViewModel.notes.observe(this, notes -> noteAdapter.setItems(notes));
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewNotesShortCard.setLayoutManager(layoutManager);

        CustomRecyclerAdapter.OnNoteClickListener onUserClickListener = note ->
                notesScrollViewModel.clickedOnNote(note);

        noteAdapter = new CustomRecyclerAdapter(onUserClickListener);
        binding.recyclerViewNotesShortCard.setAdapter(noteAdapter);
    }
}
