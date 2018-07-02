package com.collegeapp.collegeapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.collegeapp.collegeapp.R;
import com.collegeapp.collegeapp.adapters.DisplayAdaptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

    public class scrollActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

        private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
        private boolean mIsAvatarShown = true;

        private int mMaxScrollSize;
        ProgressDialog progressDialog;
        DatabaseReference myref;
        public String key;
        public  DisplayAdaptor displayAdaptor;
        public ViewPager mViewpager;
        TabLayout mtablayout;

        public CollapsingToolbarLayout collapsingToolbarLayout;
        @BindView(com.collegeapp.collegeapp.R.id.toolbar)
        Toolbar toolbar;
        @BindView(com.collegeapp.collegeapp.R.id.profileimage)
        ImageView profileimage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(com.collegeapp.collegeapp.R.layout.activity_scrolling);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
            mViewpager =(ViewPager)findViewById(com.collegeapp.collegeapp.R.id.Vpager);
            mtablayout =(TabLayout)findViewById(com.collegeapp.collegeapp.R.id.tablayout_);

            appbarLayout.addOnOffsetChangedListener(this);
            mMaxScrollSize = appbarLayout.getTotalScrollRange();

            Intent i = getIntent();

            key = i.getStringExtra("key");
            myref = FirebaseDatabase.getInstance().getReference().child("root").child("contact list").child("chairpersons");

            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String a = dataSnapshot.child(key).child("image").getValue().toString();
                    Glide.with(getApplicationContext()).load(a).into(profileimage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            displayAdaptor = new DisplayAdaptor(getSupportFragmentManager(), key);
            mViewpager.setAdapter(displayAdaptor);
            mtablayout.setupWithViewPager(mViewpager);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onBackPressed();
                }
            });

        }

        public static void start(Context c) {
            c.startActivity(new Intent(c, scrollActivity.class));
        }
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (mMaxScrollSize == 0)
                mMaxScrollSize = appBarLayout.getTotalScrollRange();

            int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

            if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
                mIsAvatarShown = false;

                profileimage.animate()
                        .scaleY(0).scaleX(0)
                        .setDuration(200)
                        .start();
            }

            if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
                mIsAvatarShown = true;

                profileimage.animate()
                        .scaleY(1).scaleX(1)
                        .start();
            }
        }

    }

