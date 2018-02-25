package com.example.mailapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_REQUEST_CODE = 1;

    MessageAdapter mAdapter;
    private String mUser;
    private String mPassword;
    private String mHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mUser = "testmailforapp@wp.pl";
//        mPassword = "testmail123";
//        mHost = "pop3.wp.pl";

        Toolbar tool_bar = findViewById(R.id.tool_bar_main);
        setSupportActionBar(tool_bar);

        ListView mailListView = findViewById(R.id.list);

        mAdapter = new MessageAdapter(this, new ArrayList<Mail>());

        mailListView.setAdapter(mAdapter);

        mailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Mail currentMail = mAdapter.getItem(i);
                Intent intent = new Intent(MainActivity.this, MailBodyActivity.class);

                intent.putExtra("From", currentMail.getFromAddress());
                intent.putExtra("Subject", currentMail.getSubject());
                intent.putExtra("Time", currentMail.getMailTime());
                intent.putExtra("Message", currentMail.getBody());

                startActivity(intent);
            }
        });

        Intent intent = new Intent(this, LogScreenActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                mUser = data.getStringExtra("email");
                mPassword = data.getStringExtra("password");
                mHost = data.getStringExtra("host");

                MailFetchTask mailFetchTask = new MailFetchTask();
                mailFetchTask.execute(mUser, mPassword, mHost);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_send) {
            Intent sendActivityIntent = new Intent(this, SendEmailActivity.class);
            sendActivityIntent.putExtra("user", mUser);
            sendActivityIntent.putExtra("password", mPassword);
            startActivity(sendActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class MailFetchTask extends AsyncTask<String, Void, List<Mail>> {

        @Override
        protected List<Mail> doInBackground(String... accountStrings) {

            List<Mail> mailList = new ArrayList<>();

            // Account name/pass/host
            final String user = accountStrings[0];
            final String password = accountStrings[1];
            String host = accountStrings[2];

            try {
                Properties properties = new Properties();
                properties.put("mail.store.protocol", "imaps");
                properties.put("mail.imap.host", host);
                properties.put("mail.imap.port", "993");
//                properties.put("mail.imap.starttls.enable", "true");
//                properties.put("mail.pop3.user", user);
//                properties.put("mail.pop3.socketFactory", 995);
//                properties.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                Session session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
                Store store = session.getStore("imaps");

                store.connect(host, user, password);

                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                Message[] messages = emailFolder.getMessages();

                for (Message message : messages) {

                    InternetAddress fromAddress = (InternetAddress) message.getFrom()[0];
                    String fromAddressText = fromAddress.getPersonal() + " - " + fromAddress.getAddress();

                    String msgBody = getTextFromMessage(message);

                    mailList.add(new Mail(fromAddressText, message.getSubject(), msgBody, message.getReceivedDate()));
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

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

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
