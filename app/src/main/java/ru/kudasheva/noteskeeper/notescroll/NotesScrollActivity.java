package ru.kudasheva.noteskeeper.notescroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityNotesScrollBinding;
import ru.kudasheva.noteskeeper.login.LoginActivity;
import ru.kudasheva.noteskeeper.ui.CreateNoteActivity;
import ru.kudasheva.noteskeeper.ui.FriendsActivity;
import ru.kudasheva.noteskeeper.notebrowse.NoteBrowseActivity;

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

    @Override
    protected void onResume() {
        super.onResume();
        notesScrollViewModel.onResume();
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
                // скорее всего сюда стоит предавать какой-нибудь id заметки, чтобы потом NoteBrowseVM смогла ее загрузить
                Intent intent = new Intent(NotesScrollActivity.this, NoteBrowseActivity.class);
                intent.putExtra("Title", notesScrollViewModel.noteToShow.header);
                startActivity(intent);

            } else if (activityCode == NotesScrollViewModel.Commands.OPEN_MENU) {
                binding.fabAddNoteAction.setVisibility(View.VISIBLE);
                binding.fabAddContactAction.setVisibility(View.VISIBLE);
                binding.fabChangeUserAction.setVisibility(View.VISIBLE);
                binding.fabMenuButton.animate().rotationBy(-45f);
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
