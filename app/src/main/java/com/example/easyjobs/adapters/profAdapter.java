package com.example.easyjobs.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyjobs.Objects.PremiumProf;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.R;

import java.util.ArrayList;
import java.util.List;

public class profAdapter extends RecyclerView.Adapter<profAdapter.ViewHolder>
{
    private static final int Regular = 1;
    private static final int Premium = 2;
    private final Context context;
    private List<PremiumProf> ProfsFeed = new ArrayList();

    public profAdapter(Context context)
    {
        this.context = context;
    }

    public void setProfsFeed(List<PremiumProf> ProfsFeed)
    {
        this.ProfsFeed = ProfsFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if (viewType == Regular)
        {
            view = LayoutInflater.from(context).inflate(R.layout.profs_feed_layout, parent, false);
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.profs_feed_layout, parent, false);
            LinearLayout LL = view.findViewById(R.id.ProfLayout);
            LL.setBackgroundColor(Color.rgb(233, 237, 180));
        }
        return new profAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Prof Profs = ProfsFeed.get(position);
        holder.showCallDetails(Profs);
    }

    @Override
    public int getItemCount()
    {
        return ProfsFeed.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (ProfsFeed.get(position).isPremium())
        {
            return Premium;
        }
        else
        {
            return Regular;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView descTextView;
        private final TextView locTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            //Initiate view
            descTextView = (TextView) itemView.findViewById(R.id.Prof_desc);
            locTextView = (TextView) itemView.findViewById(R.id.Prof_loc);
        }

        public void showCallDetails(Prof Prof)
        {
            //Attach values for each item
            String desc = Prof.getDesc();
            String loc = Prof.getLocation();
            descTextView.setText(desc);
            locTextView.setText(loc);
        }
    }
}
