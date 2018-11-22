package com.example.prfc.AccountActivity;

import java.util.ArrayList;

public class UserInfo {

    String user_id;
    //img?
    ArrayList group_list;


    public UserInfo(String user_id, ArrayList group_list)
    {
        this.user_id = user_id;
        this.group_list = group_list;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_id(){
        return user_id;
    }

    public void setGroup_list(ArrayList group_list) {
        this.group_list = group_list;
    }

    public ArrayList getGroup_list() {
        return group_list;
    }
}
