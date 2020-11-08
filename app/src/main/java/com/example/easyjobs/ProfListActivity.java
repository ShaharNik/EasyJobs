package com.example.easyjobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Prof> ProfList;
    private profAdapter ProfAdapter;
   // private Button profProfile;
    private Button postProf;
    private ImageView backBLA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_list);

        backBLA = findViewById(R.id.back_prof_list);
        backBLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfListActivity.super.onBackPressed();
            }
        });

        postProf = findViewById(R.id.profList_to_PostProf);
        postProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPostProf();
            }
        });


        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_prof_list);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        //profProfile = findViewById(R.id.profList_to_profProfile);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // System.err.println(position);
                        moveToProfProfile(position);
                    }
                }));
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(16));
        init();
    }

    private void init()
    {
        ProfList = new ArrayList<>();
       // System.err.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
        ProfAdapter=new profAdapter(ProfListActivity.this);
        recyclerView.setAdapter(ProfAdapter);

        FirebaseDBProfs dbdbj = new FirebaseDBProfs();
        DatabaseReference dr = dbdbj.getAllProfs();

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s :
                        snapshot.getChildren()) {
                    ProfList.add(s.getValue(Prof.class));
                   // System.err.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
                }
                ProfAdapter.setProfsFeed(ProfList);
                ProfAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void moveToProfProfile(int id){
        Intent i = new Intent(ProfListActivity.this, ProfProfileActivity.class);
        i.putExtra("prof_id",ProfList.get(id).prof_ID);
        startActivity(i);
    }

    public void moveToPostProf(){
        Intent i = new Intent(ProfListActivity.this, PostProfActivity.class);
        startActivity(i);
    }

    public class CommonItemSpaceDecoration extends RecyclerView.ItemDecoration {

        private int mSpace = 0;
        private boolean mVerticalOrientation = true;

        public CommonItemSpaceDecoration(int space) {
            this.mSpace = space;
        }

        public CommonItemSpaceDecoration(int space, boolean verticalOrientation) {
            this.mSpace = space;
            this.mVerticalOrientation = verticalOrientation;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = SizeUtils.dp2px(view.getContext(), mSpace);
            if (mVerticalOrientation) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.set(0, SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace));
                } else {
                    outRect.set(0, 0, 0, SizeUtils.dp2px(view.getContext(), mSpace));
                }
            } else {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.set(SizeUtils.dp2px(view.getContext(), mSpace), 0, 0, 0);
                } else {
                    outRect.set(SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace), 0);
                }
            }
        }
    }
}