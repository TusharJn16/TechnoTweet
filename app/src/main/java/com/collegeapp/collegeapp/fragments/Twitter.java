package com.collegeapp.collegeapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.collegeapp.collegeapp.R;
import com.collegeapp.collegeapp.activities.NewPostActivity;
import com.collegeapp.collegeapp.adapters.TwitterAdapter;
import com.collegeapp.collegeapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class Twitter extends Fragment {


    DatabaseReference mref;
    RecyclerView recyclerView;
    TwitterAdapter adapter;
    DialogFragment fragment_my_post;
    List<User> userList = new ArrayList<>();
    View v;
    @BindView(R.id.bar)
    BottomAppBar bar;
    Unbinder unbinder;
    NavigationView navigationView;
    int i = R.id.app_bar_fav;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.twitter_recycler)
    RecyclerView twitterRecycler;

    public Twitter() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v = view;
        loadData();
    }

    private void loadData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        twitterRecycler.setHasFixedSize(true);
        twitterRecycler.setLayoutManager(layoutManager);
        mref = FirebaseDatabase.getInstance().getReference().child("root").child("twitter").child("posts");

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                    Toast.makeText(getContext(), snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                }
                adapter = new TwitterAdapter(getContext(), userList);
                twitterRecycler.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.bar)
    public void onViewClicked() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_nav, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        dialog.setContentView(view);
        dialog.show();
        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(i);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                navigationView.setCheckedItem(menuItem.getItemId());
                Fragment newFragment;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                switch (menuItem.getItemId()) {

                    case R.id.app_bar_fav:
                        i = menuItem.getItemId();
                        newFragment = new Twitter();
                        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                        transaction.replace(R.id.twitter_recycler, newFragment);
                        transaction.commit();
                        dialog.dismiss();
                        break;

                    case R.id.app_bar_search:
                        i = menuItem.getItemId();

                        //int i =getActivity().findViewById(R.id.tablayout);
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        DialogFragment fragment_my_post = new fragment_my_post();
//                        fragment_my_post.show(ft, "dialog");
//                        View view = getActivity().findViewById(R.id.tablayout);

                        newFragment = new fragment_my_post();
                        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                        transaction.replace(R.id.tablayout, newFragment);
                        transaction.commit();
                        dialog.dismiss();
                        Toast.makeText(getContext(), "search", Toast.LENGTH_LONG).show();
                        break;
                }

                return false;
            }
        });

    }

    @OnClick(R.id.fab)
    public void onFabClicked() {

        Toast.makeText(getContext(), "fab", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), NewPostActivity.class);
        getActivity().startActivity(intent);
    }

}