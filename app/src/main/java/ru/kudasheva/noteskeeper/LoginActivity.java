package ru.kudasheva.noteskeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setLoginListener();
        setRegisterListener();
    }

    private void setRegisterListener() {
        Button signUpButton = findViewById(R.id.registration_button);
        signUpButton.setOnClickListener(v -> {
            setSignUpDialog();
        });
    }

    private void setSignUpDialog() {
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View prompt = li.inflate(R.layout.dialog_box, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setView(prompt);
        final EditText pass = (EditText) prompt.findViewById(R.id.text_box);

        final AlertDialog dialog = alertDialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.register_now, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO тут пока костомная проверка на существование пользователя
                        String text = pass.getText().toString();
                        if (text.equals("Volodia")) {
                            nameAlreadyExistErrorMessage(text);
                        } else {
                            name = text;
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void setLoginListener() {
        Button loginButton = findViewById(R.id.log_in_button);
        loginButton.setOnClickListener((View v) -> {
            EditText name_box = findViewById(R.id.name_enter_box);
            String user_name = name_box.getText().toString();

            // TODO заглушка для дебага глазами, тут будет проверка существования пользователя
            if (user_name.equals("Vovan")) {
                authorizationErrorMessage(user_name);
            }

            Log.d(TAG, user_name);
            // TODO передача имени

            Intent intent = new Intent(LoginActivity.this, NotesScrollActivity.class);
            startActivity(intent);

            finish();
        });
    }

    private void nameAlreadyExistErrorMessage(String inputName) {
        String errorMessage = inputName + " already exists!";
        Toast toast = Toast.makeText(this, errorMessage,Toast.LENGTH_LONG);
        toast.show();
    }

    private void authorizationErrorMessage(String inputName) {
        String errorMessage = inputName + " doesn't exist!";
        Toast toast = Toast.makeText(this, errorMessage,Toast.LENGTH_LONG);
        toast.show();
    }
}