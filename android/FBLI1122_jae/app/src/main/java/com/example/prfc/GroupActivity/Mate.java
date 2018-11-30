package com.example.prfc.GroupActivity;

import android.os.Parcel;
import android.os.Parcelable;

public class Mate implements Parcelable {

    String name;
    String email;

    public Mate(String name, String email){
        this.name = name;
        this.email = email;
    }

    protected Mate(Parcel in) {
        name = in.readString();
        email = in.readString();
    }

    public static final Creator<Mate> CREATOR = new Creator<Mate>() {
        @Override
        public Mate createFromParcel(Parcel in) {
            return new Mate(in);
        }

        @Override
        public Mate[] newArray(int size) {
            return new Mate[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getEmail(){
        return email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
    }
}
