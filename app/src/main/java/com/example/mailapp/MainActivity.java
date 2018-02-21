package com.example.mailapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MainActivity extends AppCompatActivity {

    MessageAdapter mAdapter;
    private String mUser;
    private String mPassword;
    private String mHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mailListView = findViewById(R.id.list);

        mAdapter = new MessageAdapter(this, new ArrayList<Mail>());

        mailListView.setAdapter(mAdapter);

        mailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO on click listener
                Mail currentMail = mAdapter.getItem(i);
                Intent intent = new Intent(MainActivity.this, MailBodyActivity.class);

                intent.putExtra("From", currentMail.getFrom());
                intent.putExtra("Subject", currentMail.getSubject());
                intent.putExtra("Time", currentMail.getDate());
                intent.putExtra("Message", currentMail.getBody());

                startActivity(intent);
            }
        });

        mUser = "testmailforapp@wp.pl";
        mPassword = "testmail123";
        mHost = "pop3.wp.pl";

        MailFetchTask mailFetchTask = new MailFetchTask();
        mailFetchTask.execute(mUser, mPassword, mHost);
    }

    private class MailFetchTask extends AsyncTask<String, Void, List<Mail>> {

        @Override
        protected List<Mail> doInBackground(String... accountStrings) {

            List<Mail> mailList = new ArrayList<>();

            // Account name/pass/host
            String user = accountStrings[0];
            String password = accountStrings[1];
            String host = accountStrings[2];

            try {
                Properties properties = new Properties();
                properties.put("mail.pop3.host", host);
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");

                Session emailSession = Session.getDefaultInstance(properties);
                Store store;

                store = emailSession.getStore("pop3s");

                store.connect(host, user, password);

                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                Message[] messages = emailFolder.getMessages();

                for (Message message :
                        messages) {

                    InternetAddress fromAddress = (InternetAddress) message.getFrom()[0];
                    String fromAddressText = fromAddress.getPersonal() + " - " + fromAddress.getAddress();

                    String msgBody = getTextFromMessage(message);

                    mailList.add(new Mail(fromAddressText, message.getSubject(), msgBody, message.getSentDate()));
                }

                emailFolder.close(false);
                store.close();

            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }

            return mailList;
        }

        @Override
        protected void onPostExecute(List<Mail> mail) {
            mAdapter.clear();
            if (mail != null && !mail.isEmpty())
                mAdapter.addAll(mail);
        }

        private String getTextFromMessage(Message message) throws MessagingException, IOException {
            String result = "";
            if (message.isMimeType("text/plain")) {
                result = message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = getTextFromMimeMultipart(mimeMultipart);
            }
            return result;
        }

        private String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
            StringBuilder result = new StringBuilder();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append("\n").append(bodyPart.getContent());
                    break;
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result.append("\n").append(org.jsoup.Jsoup.parse(html).text());
                } else if (bodyPart.getContent() instanceof MimeMultipart){
                    result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
                }
            }
            return result.toString();
        }
    }
}
