package ru.kudasheva.noteskeeper;

import androidx.appcompat.app.AppCompatActivity;
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

public class FriendsActivity extends AppCompatActivity {
    String name;
    private static final String TAG = FriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends);
        setRecyclerView();
        setFindFriendButton();
    }

    private void setFindFriendButton() {
        Button findFriend = findViewById(R.id.button_find_friends);
        findFriend.setOnClickListener(v -> {
            setSignUpDialog();
        });
    }

    private void setSignUpDialog() {
        LayoutInflater li = LayoutInflater.from(FriendsActivity.this);
        View prompt = li.inflate(R.layout.dialog_box, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FriendsActivity.this);
        alertDialogBuilder.setView(prompt);
        final EditText pass = (EditText) prompt.findViewById(R.id.text_box);

        final AlertDialog dialog = alertDialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.add_friends, null)
                .setNeutralButton("cancel", (dialog_, which) -> {})
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(view -> {
                // TODO тут пока костомная проверка на существование пользователя
                String text = pass.getText().toString();
                if (text.equals("Volodia")) {
                    findUserErrorMessage(text);
                } else {
                    name = text;
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_list_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FriendsRecyclerAdapter friendsAdapter = new FriendsRecyclerAdapter();
        recyclerView.setAdapter(friendsAdapter);

        Collection<FriendInfoCard> friends = loadFriends();
        friendsAdapter.setItems(friends);
    }

    private Collection<FriendInfoCard> loadFriends() {
        // TODO заглука зашрузить друзей
        return Arrays.asList(
                new FriendInfoCard("Harry1"),
                new FriendInfoCard("Harry2"),
                new FriendInfoCard("Harry3"),
                new FriendInfoCard("Harry4"),
                new FriendInfoCard("Harry5"),
                new FriendInfoCard("Harry6"),
                new FriendInfoCard("Harry7"),
                new FriendInfoCard("Harry8"),
                new FriendInfoCard("Harry9"),
                new FriendInfoCard("Harry1"),
                new FriendInfoCard("Harry2"),
                new FriendInfoCard("Harry3"),
                new FriendInfoCard("Harry4"),
                new FriendInfoCard("Harry5"),
                new FriendInfoCard("Harry6"),
                new FriendInfoCard("Harry7"),
                new FriendInfoCard("Harry8"),
                new FriendInfoCard("Harry9")
        );
    }

    private void findUserErrorMessage(String inputName) {
        String errorMessage = "Can't find user with name " + inputName;
        Toast toast = Toast.makeText(this, errorMessage,Toast.LENGTH_LONG);
        toast.show();
    }
}