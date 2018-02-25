package com.example.mailapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MailBodyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_body);

        Toolbar toolbar = findViewById(R.id.tool_bar_body);
        setSupportActionBar(toolbar);

        // Create new Mail object based on passed arguments from MainActivity
        Bundle extras = getIntent().getExtras();
        String from = extras.getString("From");
        String subject = extras.getString("Subject");
        String message = extras.getString("Message");
        String time = extras.getString("Time");

        DateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Mail mail = new Mail(from, subject, message, date);

        TextView fromTextView = findViewById(R.id.body_from);
        fromTextView.setText(mail.getFromAddress());

        TextView subjectTextView = findViewById(R.id.body_subject);
        subjectTextView.setText(mail.getSubject());

        TextView timeTextView = findViewById(R.id.body_time);
        timeTextView.setText(mail.getMailTime());

        TextView messageTextView = findViewById(R.id.body_body);
        messageTextView.setText(mail.getBody());
        messageTextView.setMovementMethod(new ScrollingMovementMethod());
    }
}
