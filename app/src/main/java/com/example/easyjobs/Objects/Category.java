package com.example.easyjobs.Objects;

public class Category implements Comparable
{
    String category_id;
    String cat_name;

    public Category() {}

    public Category(String category_id, String cat_name)
    {
        this.category_id = category_id;
        this.cat_name = cat_name;
    }

    public String getCategory_id()
    {
        return category_id;
    }

    public void setCategory_id(String category_id)
    {
        this.category_id = category_id;
    }

    public String getCat_name()
    {
        return cat_name;
    }

    public void setCat_name(String cat_name)
    {
        this.cat_name = cat_name;
    }

    @Override
    public String toString()
    {
        return cat_name;
    }

    @Override
    public int compareTo(Object o)
    {
        if (this.cat_name.compareTo("כללי") == 0)
        {
            return 0;
        }
        return 1;
    }
}
