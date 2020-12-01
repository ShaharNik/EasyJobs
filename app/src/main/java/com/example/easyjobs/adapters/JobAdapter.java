package com.example.easyjobs.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.PremiumJob;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private List<PremiumJob> JobsFeed=new ArrayList();
    private Context context;
    private static int Regular = 1;
    private static int Premium = 2;
    public JobAdapter(Context context)
   {
        this.context = context;
   }

    public void setJobsFeed(List<PremiumJob> jobsFeed){
        this.JobsFeed=jobsFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        int layout = R.layout.jobs_feed_layout;
//        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
//        return new ViewHolder(v);
        View view;
        if (viewType == Regular) { // for call layout
            view = LayoutInflater.from(context).inflate(R.layout.jobs_feed_layout, parent, false);
        } else { // for email layout
            view = LayoutInflater.from(context).inflate(R.layout.jobs_feed_layout, parent, false);
            view.setBackgroundColor(Color.YELLOW);

        }
        return new ViewHolder(view);
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
            Date startDate = job.getStartDate();
            Date endDate = job.getEndDate();
            descTextView.setText(desc);
            locTextView.setText(loc);
            DateFormat df = new SimpleDateFormat("dd/MM/yy");
            dateTextView.setText(df.format(startDate.getTime())+" - " + df.format(endDate.getTime()));
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (JobsFeed.get(position).isPremium()) {
            return Premium;

        } else {
            return Regular;
        }
    }

}

