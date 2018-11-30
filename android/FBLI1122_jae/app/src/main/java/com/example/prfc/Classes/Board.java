package com.example.prfc.Classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.prfc.GroupActivity.Mate;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Parcelable {

    private String id;
    private String title;
    private String content;
    private String name;//팀원들 이름을 보여주기 위해 사용
    private ArrayList<Mate> invitedUsers;//Mate 객체 여러개.

    public Board() {
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    protected Board(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        name = in.readString();
        invitedUsers = in.readArrayList(Mate.class.getClassLoader());//클래스 객체의 경우... 클래스 로더를 설정해주어야 한다.

    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(name);
        parcel.writeList(invitedUsers);
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

    public Board(String id, String title, String content, String name, ArrayList<Mate> invitedUsers) {
        String temp= "";

        this.id = id;
        this.title = title;
        this.content = content;
        this.name = name;
        this.invitedUsers = invitedUsers;

        for(int i = 0; i< invitedUsers.size();i++){
            if(!invitedUsers.get(i).getName().equals(this.name))
                temp = temp + invitedUsers.get(i).getName() + " ";
        }

        this.name = this.name + " " + temp;

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