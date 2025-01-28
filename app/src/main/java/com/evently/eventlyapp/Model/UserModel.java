package com.evently.eventlyapp.Model;

public class UserModel {
    String name;
    String mail;
    String role;

    public UserModel(String name, String mail, String role) {
        this.name = name;
        this.mail = mail;
        this.role = role;
    }
    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }
}
