package com.example.easyjobs.Objects;

import java.util.HashMap;
import java.util.Map;

public class Rating
{
    private String userId_from;
    private Map<String, Float> To_ID;

    public Rating()
    {
        Map<String, Float> To_ID = new HashMap<>();
    }

    public String getUserId_from()
    {
        return userId_from;
    }

    public void setUserId_from(String userId_from)
    {
        this.userId_from = userId_from;
    }

    public Map<String, Float> getTo_ID()
    {
        return To_ID;
    }

    public void setTo_ID(Map<String, Float> to_ID)
    {
        To_ID = to_ID;
    }
}
