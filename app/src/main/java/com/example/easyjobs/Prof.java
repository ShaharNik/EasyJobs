package com.example.easyjobs;

import java.util.List;

public class Prof {
    int prof_ID;
    String User_ID;
    String desc;
    List<Integer> category;
    String location;

    public Prof(){}

    public Prof(int prof_ID, String user_ID, String desc, List<Integer> category, String location) {
        this.prof_ID = prof_ID;
        User_ID = user_ID;
        this.desc = desc;
        this.category = category;
        this.location = location;
    }

    public Prof(String user_ID, String desc, List<Integer> category, String location) {
        User_ID = user_ID;
        this.desc = desc;
        this.category = category;
        this.location = location;
    }

    public int getJob_ID() {
        return prof_ID;
    }

    public void setJob_ID(int prof_ID) {
        this.prof_ID = prof_ID;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Integer> getCategory() {
        return category;
    }

    public void setCategory(List<Integer> category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
