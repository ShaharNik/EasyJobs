package com.example.easyjobs.Objects;

import java.io.File;
import java.io.Serializable;

public class Picture implements Serializable
{
    private File f;
    private String name;

    public Picture() {}

    public Picture(File f, String name)
    {
        this.f = f;
        this.name = name;
    }

    public File getF()
    {
        return f;
    }

    public void setF(File f)
    {
        this.f = f;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

