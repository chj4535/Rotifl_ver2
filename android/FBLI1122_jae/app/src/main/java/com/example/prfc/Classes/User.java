package com.example.prfc.Classes;

import android.graphics.Bitmap;

import com.github.bassaer.chatmessageview.model.IChatUser;

public class User implements IChatUser {
    String id;//email
    String name;
    Bitmap icon;

    public User(String id, String name, Bitmap icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Bitmap getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}