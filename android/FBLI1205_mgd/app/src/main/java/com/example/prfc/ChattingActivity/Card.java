package com.example.prfc.ChattingActivity;

public class Card {

    String name;
    int year;
    int month;
    int day;
    String startTime;
    String endTime;
    String color;
    String comment;

    public Card(String name, int year, int month, int day, String startTime, String endTime, String color, String comment){
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.comment = comment;
    }
    //color가 빠진 생성자, color을 random하게 넣어주자
    public Card(String name, int year, int month, int day, String startTime, String endTime, String comment){

        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comment = comment;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
