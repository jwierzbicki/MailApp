package com.example.mailapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailActivity extends AppCompatActivity {

    private EditText toEditText;
    private EditText subjectEditText;
    private EditText bodyEditText;

    private String user, password;
    String to, subject, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        toEditText = findViewById(R.id.send_to_editText);
        subjectEditText = findViewById(R.id.send_subject_editText);
        bodyEditText = findViewById(R.id.send_body_editText);
        Button sendButton = findViewById(R.id.send_button);

        Toolbar toolbar = findViewById(R.id.tool_bar_send);
        setSupportActionBar(toolbar);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                to = toEditText.getText().toString();
                subject = subjectEditText.getText().toString();
                body = bodyEditText.getText().toString();

//                if(to.length() < 7 || subject.length() < 3) {
//                    Toast.makeText(SendEmailActivity.this, "Recipient address or subject too short", Toast.LENGTH_SHORT).show();
//                } else {
                    Log.v("SendEmailActivity", "Calling sendEmailTask");
                    SendMail sendMailTask = new SendMail();
                    sendMailTask.execute(to, subject, body);
//                }
            }
        });

        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
    }

    private class SendMail extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String toInTask = strings[0];
            String subjectInTask = strings[1];
            String bodyInTask = strings[2];

            Log.v("SendEmailActivity", "Started doInBackground, took variables");

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.wp.pl");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            session.setDebug(true);

            Log.v("SendEmailActivity", "Started session");

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user));
                //message.setRecipient(Message.RecipientType.TO, new InternetAddress(toInTask));
                message.setSubject(subjectInTask);
                message.setText(bodyInTask);

                Log.v("SendEmailActivity", "Set message parameters");

                Transport transport = session.getTransport("smtp");
                transport.connect("smtp.wp.pl", user, password);

                Log.v("SendEmailActivity", "Transport connected");

                message.saveChanges();

                Address addressArray[] = new Address[] { (Address) new InternetAddress(toInTask)};
                transport.sendMessage(message, addressArray);

                Log.v("SendEmailActivity", "Email sent");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(SendEmailActivity.this, "Email sent", Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
    }
}
