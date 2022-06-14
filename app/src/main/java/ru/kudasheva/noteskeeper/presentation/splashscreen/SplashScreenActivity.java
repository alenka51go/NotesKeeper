package ru.kudasheva.noteskeeper.presentation.splashscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import ru.kudasheva.noteskeeper.presentation.login.LoginActivity;
import ru.kudasheva.noteskeeper.presentation.notescroll.NotesScrollActivity;

@SuppressLint("CustomSplashScreen")
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
                    startActivity(new Intent(SplashScreenActivity.this,
                            LoginActivity.class));
                    finish();
                    break;
                }
                case OPEN_NOTESCROLL_ACTIVITY: {
                    startActivity(new Intent(SplashScreenActivity.this,
                            NotesScrollActivity.class));
                    finish();
                    break;
                }
            }
        });
    }
}