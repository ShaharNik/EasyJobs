package com.example.easyjobs.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyjobs.Objects.PremiumProf;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.R;

import java.util.ArrayList;
import java.util.List;

public class profAdapter extends RecyclerView.Adapter<profAdapter.ViewHolder> {

    private List<PremiumProf> ProfsFeed=new ArrayList();
    private Context context;
    private static int Regular = 1;
    private static int Premium = 2;

    public profAdapter(Context context)
    {
        this.context = context;
    }

    public void setProfsFeed(List<PremiumProf> ProfsFeed){
        this.ProfsFeed=ProfsFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        int layout = R.layout.profs_feed_layout;
//        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
//        return new ViewHolder(v);

        View view;
        if (viewType == Regular) { // for call layout
            view = LayoutInflater.from(context).inflate(R.layout.profs_feed_layout, parent, false);
        } else { // for email layout
            view = LayoutInflater.from(context).inflate(R.layout.profs_feed_layout, parent, false);
            view.setBackgroundColor(Color.YELLOW);
        }
        return new profAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prof Profs = ProfsFeed.get(position);
        holder.showCallDetails(Profs);
    }

    @Override
    public int getItemCount() {
        return ProfsFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView descTextView,locTextView,dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            //Initiate view
            descTextView=(TextView)itemView.findViewById(R.id.Prof_desc);
            locTextView=(TextView)itemView.findViewById(R.id.Prof_loc);
        }

        public void showCallDetails(Prof Prof){
            //Attach values for each item
            String desc = Prof.getDesc();
            String loc = Prof.getLocation();
            descTextView.setText(desc);
            locTextView.setText(loc);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (ProfsFeed.get(position).isPremium()) {
            return Premium;
        } else {
            return Regular;
        }
    }
}
