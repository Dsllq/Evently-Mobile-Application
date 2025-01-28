package com.evently.eventlyapp.Model;

import java.util.ArrayList;

public class MainUserModel {
    private String mail;
    private String name;
    private String role;
    private String userUID;
    private boolean enableEditor;
    private ArrayList<String> myEvents;

    public MainUserModel() {
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEnableEditor(boolean enableEditor) {
        this.enableEditor = enableEditor;
    }

    public void setMyEvents(ArrayList<String> myEvents) {
        this.myEvents = myEvents;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean isEnableEditor() {
        return enableEditor;
    }

    public ArrayList<String> getMyEvents() {
        return myEvents;
    }

}
