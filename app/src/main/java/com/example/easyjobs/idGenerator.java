package com.example.easyjobs;

public class idGenerator {
    public static String tokenGenerator()
    {
        String token = "";
        char chars;
        int nums;
        int numOrChar;
        for (int i = 0; i < 32; i++)
        {
            numOrChar = (int)(Math.random() * 2); //Generate number: 0 or 1
            if (numOrChar == 0)
            {
                chars = (char)((Math.random()*(122-97+1)) + 97); // 97 - 122 Generate char 'a'-'z'
                token += chars;
            }
            else
            {
                nums = (int)(Math.random() * 10); // Generate Number: 0-9
                token += nums;
            }
        }

        return token;
    }
}
