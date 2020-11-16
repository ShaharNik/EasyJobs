package com.example.easyjobs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private List<Job> JobsFeed=new ArrayList();
    private Context context;

    public JobAdapter(Context context)
   {
        this.context = context;
   }

    public void setJobsFeed(List<Job> jobsFeed){
        this.JobsFeed=jobsFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.jobs_feed_layout;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job jobs = JobsFeed.get(position);
        holder.showCallDetails(jobs);
    }

    @Override
    public int getItemCount() {
        return JobsFeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView descTextView,locTextView,dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            descTextView=(TextView)itemView.findViewById(R.id.desc);
            locTextView=(TextView)itemView.findViewById(R.id.loc);
            dateTextView=(TextView)itemView.findViewById(R.id.date);
        }

        public void showCallDetails(Job job){
            //Attach values for each item
            String desc = job.getDesc();
            String loc = job.getLocation();
            Date date = job.getDate();
            descTextView.setText(desc);
            locTextView.setText(loc);
            dateTextView.setText(date.getTime()+"");
        }
    }
}
