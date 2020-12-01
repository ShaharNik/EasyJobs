package com.example.easyjobs.Objects;

import java.io.Serializable;
import java.util.Date;

public class Job implements Serializable {

    String job_ID;
    String User_ID;
    String desc;
    int price;
    String location;
    Date startDate;
    Date endDate;


    /// PICTURE
    int category_ID;

    public Job(){}

    public Job(String job_ID, String user_ID, String desc, int price, String location, Date startDate,Date endDate, int category_ID) {
        this.job_ID = job_ID;
        User_ID = user_ID;
        this.desc = desc;
        this.price = price;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category_ID = category_ID;
    }

    public Job(String user_ID, String desc, int price, String location, Date date, int category_ID) {// Don't know if required
        User_ID = user_ID;
        this.desc = desc;
        this.price = price;
        this.location = location;
        this.startDate = date;
        this.category_ID = category_ID;
    }

    public String getJob_ID() {
        return job_ID;
    }

    public void setJob_ID(String job_ID) {
        this.job_ID = job_ID;
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

    public int getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(int category_ID) {
        this.category_ID = category_ID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    }