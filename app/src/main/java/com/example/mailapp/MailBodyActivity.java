package com.example.mailapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class MailBodyActivity extends AppCompatActivity {

    Mail mail;
    String mBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_body);

        Toolbar toolbar = findViewById(R.id.tool_bar_body);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("ID");

        List<Mail> mailList = MainActivity.emailList;
        mail = mailList.get(id-1);

        GetMailBodyTask getMailBodyTask = new GetMailBodyTask();
        getMailBodyTask.execute();
    }

    private class GetMailBodyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return mail.getBody();
        }

        @Override
        protected void onPostExecute(String body) {

            View loadingIndicator = findViewById(R.id.loading_indicator_mailBody);
            loadingIndicator.setVisibility(View.GONE);

            mBody = body;

            TextView fromTextView = findViewById(R.id.body_from);
            fromTextView.setText(mail.getFromAddress());

            TextView subjectTextView = findViewById(R.id.body_subject);
            subjectTextView.setText(mail.getSubject());

            TextView timeTextView = findViewById(R.id.body_time);
            timeTextView.setText(mail.getMailTime());

            TextView messageTextView = findViewById(R.id.body_body);
            messageTextView.setText(mBody);
            messageTextView.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}
