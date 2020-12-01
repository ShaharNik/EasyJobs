package com.example.easyjobs.Objects;

import java.io.Serializable;
import java.util.List;

public class Prof implements Serializable {
    String prof_ID;
    String User_ID;
    String desc;
    List<String> category;
    String location;

    public Prof(){}

    public Prof(String prof_ID, String user_ID, String desc, List<String> category, String location) {
        this.prof_ID = prof_ID;
        User_ID = user_ID;
        this.desc = desc;
        this.category = category;
        this.location = location;
    }

    public Prof(String user_ID, String desc, List<String> category, String location) {// Don't know if required
        User_ID = user_ID;
        this.desc = desc;
        this.category = category;
        this.location = location;
    }

    public String getProf_ID() { return prof_ID; }

    public void setProf_ID(String prof_ID) { this.prof_ID = prof_ID; }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getDesc() { return desc; }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
