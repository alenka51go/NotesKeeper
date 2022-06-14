package ru.kudasheva.noteskeeper.presentation.notebrowse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityNoteBrowseBinding;

public class NoteBrowseActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener  {
    private static final String TAG = NoteBrowseActivity.class.getSimpleName();

    private NoteBrowseViewModel noteBrowseViewModel;
    private ActivityNoteBrowseBinding binding;
    private MultipleTypesAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteBrowseViewModel = ViewModelProviders.of(this).get(NoteBrowseViewModel.class);
        noteBrowseViewModel.initData(getIntent().getExtras().getString("Id"));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_browse);
        binding.setViewModel(noteBrowseViewModel);
        binding.setLifecycleOwner(this);

        setProperties();
        observeLiveData();
    }

    private void observeLiveData() {
        noteBrowseViewModel.progressIsVisible.observe(this, (res) -> {
            if (res) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this, R.style.MyTheme);
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.show();
                }
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });

        noteBrowseViewModel.dataContainer.observe(this, listOfNoteAndComments -> {
            adapter.setItems(listOfNoteAndComments);
        });

        noteBrowseViewModel.activityCommand.observe(this, activityCommand -> {
            switch (activityCommand) {
                case SHOW_MENU: {
                    showMenu();
                    break;
                }
                case CLOSE_ACTIVITY: {
                    finish();
                    break;
                }
                case REMOVE_ACTION_BUTTON: {
                    binding.buttonAction.setVisibility(View.GONE);
                    break;
                }
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

    private void setProperties() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewNoteAndComments.setLayoutManager(layoutManager);

        adapter = new MultipleTypesAdapter(this);
        binding.recyclerViewNoteAndComments.setAdapter(adapter);

        binding.titleBox.setSelected(true);

        mSwipeRefreshLayout = binding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void showErrorMessage(String errorMessage) {
        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "Refresh recycled container");
        new Handler().postDelayed(() -> {
            noteBrowseViewModel.update();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }
}