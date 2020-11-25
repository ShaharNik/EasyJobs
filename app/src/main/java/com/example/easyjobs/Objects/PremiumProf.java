package com.example.easyjobs.Objects;

public class PremiumProf extends Prof implements Comparable<PremiumProf> {
    private boolean isPremium;

    public PremiumProf() {
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    @Override
    public int compareTo(PremiumProf o) {
        return (o.isPremium == isPremium ? 0 : (isPremium ? -1 : 1));
    }
}
