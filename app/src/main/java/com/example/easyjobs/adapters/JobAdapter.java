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

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.PremiumJob;
import com.example.easyjobs.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder>
{
    private static final int Regular = 1;
    private static final int Premium = 2;
    private final Context context;
    private List<PremiumJob> JobsFeed = new ArrayList();

    public JobAdapter(Context context)
    {
        this.context = context;
    }

    public void setJobsFeed(List<PremiumJob> jobsFeed)
    {
        this.JobsFeed = jobsFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if (viewType == Regular)
        {
            view = LayoutInflater.from(context).inflate(R.layout.jobs_feed_layout, parent, false);
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.jobs_feed_layout, parent, false);
            LinearLayout LL = view.findViewById(R.id.JobLayout);
            LL.setBackgroundColor(Color.rgb(233, 237, 180));
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Job jobs = JobsFeed.get(position);
        holder.showCallDetails(jobs);
    }

    @Override
    public int getItemCount()
    {
        return JobsFeed.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (JobsFeed.get(position).isPremium())
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
        private final TextView dateTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            // Initiate view
            descTextView = (TextView) itemView.findViewById(R.id.desc);
            locTextView = (TextView) itemView.findViewById(R.id.loc);
            dateTextView = (TextView) itemView.findViewById(R.id.date);
        }

        public void showCallDetails(Job job)
        {
            //Attach values for each item
            String desc = job.getDesc();
            String loc = job.getLocation();
            Date startDate = job.getStartDate();
            Date endDate = job.getEndDate();
            descTextView.setText(desc);
            locTextView.setText(loc);
            DateFormat df = new SimpleDateFormat("dd/MM/yy");
            dateTextView.setText(df.format(startDate.getTime()) + " - " + df.format(endDate.getTime()));
        }
    }
}

