package com.example.mailapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_REQUEST_CODE = 1;

    long startTime = 0, endTime = 0;

    Session session;
    Store store;
    Folder emailFolder;

    public static List<Mail> emailList;

    MessageAdapter mAdapter;
    private String mUser;
    private String mPassword;
    private String mHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                intent.putExtra("ID", currentMail.getId());

                startActivity(intent);
            }
        });

        Intent intent = new Intent(this, LogScreenActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    emailFolder.close(false);
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
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

            startTime = System.currentTimeMillis();

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
                properties.put("mail.debug", "true");

                session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
                store = session.getStore("imaps");

                store.connect(host, user, password);

                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                Message[] messages = emailFolder.getMessages();

                FetchProfile profile = new FetchProfile();
                profile.add(FetchProfile.Item.ENVELOPE);
                profile.add(FetchProfile.Item.CONTENT_INFO);
                profile.add("X-Mailer");

                emailFolder.fetch(messages, profile);

                for (Message message : messages) {
                    mailList.add(new Mail(message));
                }

            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return mailList;
        }

        @Override
        protected void onPostExecute(List<Mail> mailList) {

            endTime = System.currentTimeMillis();

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            emailList = mailList;

            mAdapter.clear();

            if (mailList != null && !mailList.isEmpty()) {
                mAdapter.addAll(mailList);
                Toast.makeText(MainActivity.this, "Execution time: " + ((endTime - startTime)/1000) + " seconds", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "Execution time: " + ((endTime - startTime)/1000) + " seconds");
            }
        }
    }
}
