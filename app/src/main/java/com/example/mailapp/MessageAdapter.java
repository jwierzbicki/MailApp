package com.example.mailapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class MessageAdapter extends ArrayAdapter<Mail> {

    MessageAdapter(Context context, List<Mail> mails)
    {
        super(context,0,mails);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.mail_item, parent, false);
        }

        Mail currentMail = getItem(position);

        if (currentMail != null) {

            TextView subjectTextView = listItemView.findViewById(R.id.mail_subject_view);
            subjectTextView.setText(currentMail.getSubject());

            TextView fromTextView = listItemView.findViewById(R.id.mail_from_view);
            fromTextView.setText(currentMail.getFromAddress());

            TextView dateTextView =  listItemView.findViewById(R.id.mail_date_view);
            dateTextView.setText(currentMail.getMailTime());

        }

        return listItemView;
    }

}
