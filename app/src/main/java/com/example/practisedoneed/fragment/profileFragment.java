package com.example.practisedoneed.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.MainActivity;
import com.example.practisedoneed.Model.User;
import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.ProfileSetting;
import com.example.practisedoneed.R;

import com.example.practisedoneed.adapter.myFotosAdapter;
import com.example.practisedoneed.test;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class profileFragment extends Fragment {

    Intent intent;
    private ImageView logout2, profilePicture;
    private TextView username, bio;
    private TabLayout tabLayout;
    private int[] tabIcon = {R.drawable.ic_grid, R.drawable.ic_save};
    private FirebaseUser firebaseUser;
    String userID;
    String profileid;

    private myFotosAdapter fotosAdapter;
    private List<donatePost> postList;
    RecyclerView recyclerView1;

    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    private myFotosAdapter saves_adapter;
    private List<donatePost> savesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        intent = new Intent(getActivity(), ProfileSetting.class);
        final Button button = (Button) rootView.findViewById(R.id.setting_button);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileId", "none");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

///////////////////////////////////////////////////////////////////////////////////////

        profilePicture = rootView.findViewById(R.id.image_profile);
        username = rootView.findViewById(R.id.username);
        bio = rootView.findViewById(R.id.user_bio);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        logout2 = (ImageView) rootView.findViewById(R.id.logout2);

        logout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
            }
        });

        tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        setupTabIcons();

        recyclerView1 = rootView.findViewById(R.id.post_recycler);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView1.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        fotosAdapter = new myFotosAdapter(getContext(),postList);
        recyclerView1.setAdapter(fotosAdapter);

        mySaves = new ArrayList<>();
        recyclerView_saves = rootView.findViewById(R.id.saved_recycler);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(),3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        savesList = new ArrayList<>();
        saves_adapter = new myFotosAdapter(getContext(),savesList);
        recyclerView_saves.setAdapter(saves_adapter);

        recyclerView1.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        checkTab();
        userProfile();
        myPhotos();
        mySaves();



        return rootView;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcon[0]);
        tabLayout.getTabAt(1).setIcon(tabIcon[1]);
    }

    private void checkTab(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    recyclerView1.setVisibility(View.VISIBLE);
                    recyclerView_saves.setVisibility(View.GONE);
                }
                else if(tab.getPosition()==1){
                    recyclerView1.setVisibility(View.GONE);
                    recyclerView_saves.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        if(tabLayout.getSelectedTabPosition()==0){
//            recyclerView1.setVisibility(View.VISIBLE);
//            recyclerView_saves.setVisibility(View.GONE);
//        }else if(tabLayout.getSelectedTabPosition()==1){
//            recyclerView1.setVisibility(View.GONE);
//            recyclerView_saves.setVisibility(View.VISIBLE);
//        }
    }

    private void myPhotos(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    donatePost post = data.getValue(donatePost.class);
                    if(post.getDonator().equals(userID)){
                        postList.add(post);
                    }
//                    postList.add(post);

                }
                Collections.reverse(postList);
                fotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.i(TAG,error.getMessage());
            }
        });
    }

    public void userProfile(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageUrl()).into(profilePicture);
                username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void mySaves(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    mySaves.add(data.getKey());
                }
                displaySaves();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void displaySaves(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Posts");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                savesList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    donatePost post = data.getValue(donatePost.class);
                    if(!mySaves.isEmpty()){
                        for(String id : mySaves){
                            if(post.getId().equals(id)){
                                savesList.add(post);
                            }
                        }
                    }
                }
                saves_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}