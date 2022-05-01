package ru.kudasheva.noteskeeper.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collection;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityFriendsBinding;
import ru.kudasheva.noteskeeper.databinding.DialogBoxBinding;
import ru.kudasheva.noteskeeper.login.LoginActivity;

public class FriendsActivity extends AppCompatActivity {
    private static final String TAG = FriendsActivity.class.getSimpleName();

    private FriendsViewModel friendsViewModel;
    private ActivityFriendsBinding binding;

    private FriendsRecyclerAdapter friendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friends);
        binding.setViewModel(friendsViewModel);
        setRecyclerView();

        observeLiveData();
    }

    private void observeLiveData() {
        friendsViewModel.command.observe(this, activityCommand -> {
            if (activityCommand == FriendsViewModel.Commands.OPEN_DIALOG) {
                openFindFriendsDialog();
            }
        });

        friendsViewModel.friends.observe(this, friendList -> friendsAdapter.setItems(friendList));

        friendsViewModel.snackBarMessage.observe(this, this::showErrorMessage);
    }

    private void openFindFriendsDialog() {
        DialogBoxBinding dialogBoxBinding = DialogBoxBinding.inflate(getLayoutInflater());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FriendsActivity.this);

        AlertDialog dialog = alertDialogBuilder
                .setView(dialogBoxBinding.getRoot())
                .setCancelable(false)
                .setPositiveButton(R.string.add_friends, null)
                .setNeutralButton(android.R.string.cancel, (dialog_, which) -> {})
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
                String text = dialogBoxBinding.textBox.getText().toString();
                if (friendsViewModel.checkIfUserExist(text)) {
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewListOfFriends.setLayoutManager(layoutManager);

        friendsAdapter = new FriendsRecyclerAdapter();
        binding.recyclerViewListOfFriends.setAdapter(friendsAdapter);
    }

    private void showErrorMessage(String errorMessage) {
        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
        toast.show();
    }
}