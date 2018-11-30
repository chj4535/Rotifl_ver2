package com.example.prfc.Classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.github.bassaer.chatmessageview.model.IChatUser;
import com.github.bassaer.chatmessageview.model.Message;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageList {

    private List<SaveMessage> mMessages;
    //private int groupid;

    public MessageList() {
        mMessages = new ArrayList<>();
        //this.groupid = groupid;
    }

    public ArrayList<Message> getMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        for (SaveMessage saveMessage : mMessages) {
            messages.add(convertMessage(saveMessage));
        }
        return messages;
    }

    public void setMessages(List<Message> messages) {
        for (Message message : messages) {
            mMessages.add(convertMessage(message));
        }
    }

    public void add(Message message) {
        mMessages.add(convertMessage(message));
    }

    public Message get(int index) {
        return convertMessage(mMessages.get(index));
    }

    public List<Message> get() {
        List<Message> list = new ArrayList<>();
        for (SaveMessage message : mMessages) {
            list.add(convertMessage(message));
        }
        return list;
    }

    public int size() {
        return mMessages.size();
    }

    private SaveMessage convertMessage(Message message) {
        SaveMessage saveMessage = new SaveMessage(
                message.getUser().getId(),
                message.getUser().getName(),
                message.getText(),
                message.getSendTime(),
                message.isRight());

        saveMessage.setType(message.getType());

        if (message.getType() == Message.Type.PICTURE
                && message.getPicture() != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.getPicture().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            saveMessage.setPictureString(Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
        }

        return saveMessage;
    }

    private Message convertMessage(SaveMessage saveMessage) {
        IChatUser user = new User(saveMessage.getId(), saveMessage.getUsername(), null);

        Message message = new Message.Builder()
                .setUser(user)
                .setText(saveMessage.getContent())
                .setRight(saveMessage.isRightMessage())
                .setSendTime(saveMessage.getCreatedAt())
                .setType(saveMessage.getType())
                .build();

        if (saveMessage.getPictureString() != null) {
            byte[] bytes = Base64.decode(saveMessage.getPictureString().getBytes(), Base64.DEFAULT);
            message.setPicture(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
        return message;
    }

    private class SaveMessage {
        private String mId;
        private String mUsername;
        private String mContent;
        private Calendar mCreatedAt;
        private boolean mRightMessage;
        private String mPictureString;
        private Message.Type mType;

        public SaveMessage(String id, String username, String content, Calendar createdAt, boolean isRightMessage) {
            mId = id;
            mUsername = username;
            mContent = content;
            mCreatedAt = createdAt;
            mRightMessage = isRightMessage;
        }

        public String getId() {
            return mId;
        }

        public String getUsername() {
            return mUsername;
        }

        public String getContent() {
            return mContent;
        }

        public Calendar getCreatedAt() {
            return mCreatedAt;
        }

        public boolean isRightMessage() {
            return mRightMessage;
        }

        public String getPictureString() {
            return mPictureString;
        }

        public void setPictureString(String pictureString) {
            mPictureString = pictureString;
        }

        public Message.Type getType() {
            return mType;
        }

        public void setType(Message.Type type) {
            mType = type;
        }
    }
}