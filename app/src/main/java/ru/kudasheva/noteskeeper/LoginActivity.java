package ru.kudasheva.noteskeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setLoginListener();
    }

    private void setLoginListener() {
        Button loginButton = findViewById(R.id.log_in_button);
        loginButton.setOnClickListener((View v) -> {
            EditText name_box = findViewById(R.id.name_enter_box);
            String user_name = name_box.getText().toString();

            Log.d(TAG, user_name);
            // TODO передача имени + проверка, что имя подходящее

            Intent intent = new Intent(LoginActivity.this, NotesScrollActivity.class);
            startActivity(intent);

            finish();
        });
    }
}