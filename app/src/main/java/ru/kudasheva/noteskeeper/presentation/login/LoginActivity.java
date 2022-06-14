package ru.kudasheva.noteskeeper.presentation.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.ActivityLoginBinding;
import ru.kudasheva.noteskeeper.presentation.notescroll.NotesScrollActivity;

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
        loginViewModel.activityCommand.observe(this, (activityCode) -> {
            if (activityCode == LoginViewModel.Commands.OPEN_NOTE_SCROLL_ACTIVITY) {
                Intent intent = new Intent(LoginActivity.this, NotesScrollActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loginViewModel.snackBarMessage.observe(this, this::showErrorMessage);
    }

    private void showErrorMessage(String errorMessage) {
        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
        toast.show();
    }
}