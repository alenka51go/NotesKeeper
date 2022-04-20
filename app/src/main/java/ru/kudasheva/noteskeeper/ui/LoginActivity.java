package ru.kudasheva.noteskeeper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityLoginBinding;
import ru.kudasheva.noteskeeper.databinding.DialogBoxBinding;
import ru.kudasheva.noteskeeper.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(loginViewModel);

        observeLiveData();
    }

    private void observeLiveData() {
        loginViewModel.openActivityCommand.observe(this, (activityCode) -> {
            if (activityCode == LoginViewModel.Commands.openNoteScrollActivity) {
                Intent intent = new Intent(LoginActivity.this, NotesScrollActivity.class);
                startActivity(intent);

                finish();
            } else if (activityCode == LoginViewModel.Commands.openDialog) {
                setDialogActivity();
            }
        });

        loginViewModel.snackBarMessage.observe(this, this::showErrorMessage);
    }

    void setDialogActivity() {
        DialogBoxBinding dialogBoxBinding = DialogBoxBinding.inflate(getLayoutInflater());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        AlertDialog dialog = alertDialogBuilder
                .setView(dialogBoxBinding.getRoot())
                .setCancelable(false)
                .setPositiveButton(R.string.register_now, null)
                .setNeutralButton(android.R.string.cancel, (dialog_, which) -> {
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener((view) -> {
                String text = dialogBoxBinding.textBox.getText().toString();
                if (loginViewModel.checkPossibilityOfAdditionNewUser(text)) {
                    dialog.dismiss();
                }
            });
        });


        dialog.show();
    }

    private void showErrorMessage(String errorMessage) {
        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
        toast.show();
    }
}