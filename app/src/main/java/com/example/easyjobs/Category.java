package com.example.easyjobs;

public class Category {
    String category_id;
    String cat_name;

    public Category(String category_id, String cat_name) {
        this.category_id = category_id;
        this.cat_name = cat_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }
}
