package com.example.prfc.AccountActivity;

import android.widget.RelativeLayout;
import android.widget.Button;

import java.util.Date;

public class list_item {
    private Date write_date;
    private String _id ;
    private String boardid;
    private String title ;
    private String user ;
    private String content ;
    private String comment ;
    private RelativeLayout relativeLayout;

    public int getProfile_image() {
        return profile_image;
    }

    public Date getWrite_date() {
        return write_date;
    }

    public String get_id() {
        return _id;
    }

    public String getBoardid() {
        return boardid;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getComment() {
        return comment;
    }

    //추가한 변수
    private int profile_image;

    public void setProfile_image(int profile_image) {
        this.profile_image = profile_image;
    }

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setBoardid(String boardid) {
        this.boardid = boardid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }


    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }



    public list_item(int profile_image, Date write_date,
                     String _id, String bordid,
                     String title, String user,
                     String content, String comment) {
        this.profile_image = profile_image;
        this.write_date = write_date;
        this._id = _id;
        this.boardid = bordid;
        this.title = title;
        this.user=user;
        this.comment =comment;
        this.content = content;
    }

}
