package com.example.prfc.Classes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class Board {

    private String id;
    private String title;
    private String content;
    private String name;
    private ArrayList invitedUsers;

    public Board() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInvitedUsers(ArrayList invitedUsers){ this.invitedUsers = invitedUsers;}

    public ArrayList getInvitedUsers(){return invitedUsers;}

    public Board(String id, String title, String content, String name, ArrayList invitedUsers) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.name = name;
        this.invitedUsers = invitedUsers;
        System.out.println("******************Board :" + this.invitedUsers);
    }

    @Override
    public String toString() {
        return "Board{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}