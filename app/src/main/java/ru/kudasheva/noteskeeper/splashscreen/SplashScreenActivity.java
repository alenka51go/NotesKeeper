package ru.kudasheva.noteskeeper.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import ru.kudasheva.noteskeeper.login.LoginActivity;
import ru.kudasheva.noteskeeper.notescroll.NotesScrollActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private SplashScreenViewModel splashScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splashScreenViewModel = ViewModelProviders.of(this).get(SplashScreenViewModel.class);
        splashScreenViewModel.initData();

        observeLiveData();
    }

    private void observeLiveData() {
        splashScreenViewModel.activityCommand.observe(this, activityCommand -> {
            switch (activityCommand) {
                case OPEN_LOGIN_ACTIVITY: {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                    break;
                }
                case OPEN_NOTESCROLL_ACTIVITY: {
                    startActivity(new Intent(SplashScreenActivity.this, NotesScrollActivity.class));
                    finish();
                    break;
                }
            }
        });
    }
}