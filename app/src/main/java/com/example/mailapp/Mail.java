package com.example.mailapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Mail {

    private String mFrom;
    private String mSubject;
    private String mBody;
    private Date mDate;

    Mail(String from, String subject, String text, Date date)
    {
        mFrom = from;
        mSubject = subject;
        mBody = text;
        mDate = date;
    }

    String getFromAddress() { return mFrom; }
    String getSubject() { return mSubject; }
    String getBody() { return mBody; }
    //Date getDate() { return mDate; }
    String getMailTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ENGLISH);
        return dateFormat.format(mDate);
    }
}
