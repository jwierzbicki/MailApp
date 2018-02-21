package com.example.mailapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.Date;

public class MailBodyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_body);


        // Create new Mail object based on passed arguments from MainActivity
        Bundle extras = getIntent().getExtras();
        String from = extras.getString("From");
        String subject = extras.getString("Subject");
        String message = extras.getString("Message");
        Date time = (Date) extras.get("Time");
        Mail mail = new Mail(from, subject, message, time);

        TextView fromTextView = findViewById(R.id.body_from);
        fromTextView.setText(mail.getFrom());

        TextView subjectTextView = findViewById(R.id.body_subject);
        subjectTextView.setText(mail.getSubject());

        TextView timeTextView = findViewById(R.id.body_time);
        timeTextView.setText(mail.getTime());

        TextView messageTextView = findViewById(R.id.body_body);
        messageTextView.setText(mail.getBody());
        messageTextView.setMovementMethod(new ScrollingMovementMethod());


    }
}
