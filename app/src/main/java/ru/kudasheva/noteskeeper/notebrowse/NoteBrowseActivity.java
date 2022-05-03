package ru.kudasheva.noteskeeper.notebrowse;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityNoteBrowseBinding;

public class NoteBrowseActivity extends AppCompatActivity {
    private static final String TAG = NoteBrowseActivity.class.getSimpleName();

    private NoteBrowseViewModel noteBrowseViewModel;
    private ActivityNoteBrowseBinding binding;
    private MultipleTypesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteBrowseViewModel = ViewModelProviders.of(this).get(NoteBrowseViewModel.class);
        noteBrowseViewModel.preloadData(getIntent().getExtras().getString("Id"));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_browse);
        binding.setViewModel(noteBrowseViewModel);
        setRecyclerView();

        observeLiveData();
    }

    private void observeLiveData() {
        noteBrowseViewModel.dataContainer.observe(this, listOfNoteAndComments -> {
            adapter.setItems(listOfNoteAndComments);
        });

        noteBrowseViewModel.activityCommand.observe(this, activityCommand -> {
            if (activityCommand == NoteBrowseViewModel.Commands.SHOW_MENU) {
                showMenu();
            } else if (activityCommand == NoteBrowseViewModel.Commands.CLOSE_ACTIVITY) {
                finish();
            } else if (activityCommand == NoteBrowseViewModel.Commands.REMOVE_ACTION_BUTTON) {
                binding.buttonAction.setVisibility(View.GONE);
            }
        });

        noteBrowseViewModel.snackBarMessage.observe(this, this::showErrorMessage);
    }

    private void showMenu() {
        PopupMenu menu = new PopupMenu(NoteBrowseActivity.this, binding.buttonAction);
        menu.getMenuInflater().inflate(R.menu.popup_note_action_menu, menu.getMenu());

        menu.setOnMenuItemClickListener((MenuItem menuItem) -> {
            int action = menuItem.getItemId();
            if (action == R.id.deleteNoteItem) {
                noteBrowseViewModel.deleteNote();
            }
            return true;
        });
        menu.show();
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewNoteAndComments.setLayoutManager(layoutManager);

        adapter = new MultipleTypesAdapter();
        binding.recyclerViewNoteAndComments.setAdapter(adapter);
    }

    private void showErrorMessage(String errorMessage) {
        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
        toast.show();
    }

}