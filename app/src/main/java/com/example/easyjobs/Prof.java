package com.example.easyjobs;

public class Prof {
    int job_ID;
    int User_ID;
    String desc;
    int[] category;
    String location;

    public Prof(int job_ID, int user_ID, String desc, int[] category, String location) {
        this.job_ID = job_ID;
        User_ID = user_ID;
        this.desc = desc;
        this.category = category;
        this.location = location;
    }

    public Prof(int user_ID, String desc, int[] category, String location) {
        User_ID = user_ID;
        this.desc = desc;
        this.category = category;
        this.location = location;
    }

    public int getJob_ID() {
        return job_ID;
    }

    public void setJob_ID(int job_ID) {
        this.job_ID = job_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int[] getCategory() {
        return category;
    }

    public void setCategory(int[] category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
