package com.example.easyjobs.Objects;

public class PremiumJob extends Job implements Comparable<PremiumJob>
{
    private boolean isPremium;

    public PremiumJob() { }

    public boolean isPremium()
    {
        return isPremium;
    }

    public void setPremium(boolean premium)
    {
        isPremium = premium;
    }

    @Override
    public int compareTo(PremiumJob o)
    {
        if (o.isPremium == isPremium)
        {
            return getStartDate().compareTo(o.getStartDate());
        }
        return isPremium ? -1 : 1;
    }
}
