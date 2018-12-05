package com.example.prfc.Classes;

public class UserList {
    private String name;
    private String amount;
    private String email;

    public UserList() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() { return amount; }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.amount = email;
    }

    public UserList(String name, String amount, String email) {
        this.name = name;
        this.amount = amount;
        this.email = email;
    }
}
