package com.example.prfc.Classes;

public class ExpenseList {
    private String item;
    private String price;

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

    public ExpenseList(String item, String price) {
        this.item = item;
        this.price = price;
    }

    @Override
    public String toString() {
        return "ExpenseList{" +
                "item='" + item + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
