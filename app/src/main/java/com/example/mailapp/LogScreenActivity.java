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
    private String host = "";

    private EditText emailView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_screen);

        emailView = findViewById(R.id.email_edit_text);
        passwordView = findViewById(R.id.password_edit_text);
        Button loginButton = findViewById(R.id.login_button);

        final Spinner spinner = findViewById(R.id.host_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.host_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemX = spinner.getSelectedItem().toString();
                Toast.makeText(LogScreenActivity.this, "SelectedItem: " + selectedItemX, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        String selectedItem = spinner.getSelectedItem().toString();
        if(selectedItem.equals("WP.PL")) {
            host = "imap.wp.pl";
        } else if (selectedItem.equals("GMAIL.COM")) {
            host = "imap.gmail.com";
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailView.getText().toString();
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
