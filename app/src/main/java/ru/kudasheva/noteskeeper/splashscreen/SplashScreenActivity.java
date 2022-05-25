package ru.kudasheva.noteskeeper.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import ru.kudasheva.noteskeeper.login.LoginActivity;
import ru.kudasheva.noteskeeper.notescroll.NotesScrollActivity;

public class SplashScreenActivity extends AppCompatActivity {

    SplashScreenViewModel splashScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splashScreenViewModel = ViewModelProviders.of(this).get(SplashScreenViewModel.class);
        splashScreenViewModel.init();

        observeLiveData();
    }

    private void observeLiveData() {
        splashScreenViewModel.activityCommand.observe(this, activityCommand -> {
            if (activityCommand == SplashScreenViewModel.Commands.OPEN_LOGIN_ACTIVITY) {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            } else if (activityCommand == SplashScreenViewModel.Commands.OPEN_NOTESCROLL_ACTIVITY) {
                startActivity(new Intent(SplashScreenActivity.this, NotesScrollActivity.class));
                finish();
            }
        });
    }
}