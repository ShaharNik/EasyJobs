package com.example.easyjobs;

import java.util.Date;

public class Job {
    int job_ID;
    int User_ID;
    String desc;
    int price;
    String location;
    Date date;
    /// PICTURE
    int category_ID;

    public Job(int job_ID, int user_ID, String desc, int price, String location, Date date, int category_ID) {
        this.job_ID = job_ID;
        User_ID = user_ID;
        this.desc = desc;
        this.price = price;
        this.location = location;
        this.date = date;
        this.category_ID = category_ID;
    }

    public Job(int user_ID, String desc, int price, String location, Date date, int category_ID) {
        User_ID = user_ID;
        this.desc = desc;
        this.price = price;
        this.location = location;
        this.date = date;
        this.category_ID = category_ID;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(int category_ID) {
        this.category_ID = category_ID;
    }
}
