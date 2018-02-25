package com.example.mailapp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

class Mail {

    private String mFrom;
    private String mSubject;
    private Date mDate;
    private int mId;
    private Message mMessage;

    Mail(Message message) {
        mMessage = message;
        initMessage();
    }

    private void initMessage() {
        InternetAddress fromAddress;
        try {
            fromAddress = (InternetAddress) mMessage.getFrom()[0];
            mFrom = fromAddress.getPersonal() + " - " + fromAddress.getAddress();

            mSubject = mMessage.getSubject();

            if(mMessage.getReceivedDate() != null) {
                mDate = mMessage.getReceivedDate();
            } else if (mMessage.getReceivedDate() !=null ) {
                mDate = mMessage.getSentDate();
            } else {
                mDate = new Date();
            }

            mId = mMessage.getMessageNumber();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    String getFromAddress() { return mFrom; }
    String getSubject() { return mSubject; }
    //Date getDate() { return mDate; }
    int getId() { return mId; }

    String getMailTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ENGLISH);

        if(mDate == null) {
            mDate = new Date();
        }
        return dateFormat.format(mDate);
    }
    String getBody() {
        String b = "";
        try {
            b = getTextFromMessage(mMessage);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return b;
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
