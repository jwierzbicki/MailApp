package com.example.mailapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LogScreenActivity extends AppCompatActivity {

    private String email = "";
    private String password = "";
    private String host = "imap.wp.pl";

    private EditText emailView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_screen);

        emailView = findViewById(R.id.email_edit_text);
        passwordView = findViewById(R.id.password_edit_text);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailView.getText().toString();
                email = email + "@wp.pl";
                password = passwordView.getText().toString();

                Intent returnIntent = getIntent();

                if (email.length() < 7 || password.length() < 5) {
                    Toast.makeText(LogScreenActivity.this, "Email or password too short", Toast.LENGTH_LONG).show();
                } else {

                    returnIntent.putExtra("email", email);
                    returnIntent.putExtra("password", password);
                    returnIntent.putExtra("host", host);

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }
}
