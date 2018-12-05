package com.example.prfc.Classes;

public class ExpenseList {
    private String item;
    private String price;
    private String time;

    public ExpenseList() {
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() { return price; }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() { return time; }

    public void setTime(String time) {
        this.time = time;
    }

    public ExpenseList(String item, String price, String time) {
        this.item = item;
        this.price = price;
        this.time = time;
    }
}
