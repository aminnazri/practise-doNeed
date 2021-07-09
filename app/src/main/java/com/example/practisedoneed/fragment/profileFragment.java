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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
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

import com.example.practisedoneed.adapter.myPostAdapter;
import com.example.practisedoneed.homePage;
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
import java.util.Objects;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

//Profile Class
public class profileFragment extends Fragment {

    Intent intent;
    private ImageView logout2, profilePicture,moon,sun;
    private TextView username, bio;
    private Button settingButton, chatButton;
    private TabLayout tabLayout;
    private int[] tabIcon = {R.drawable.ic_grid, R.drawable.ic_save};
    private FirebaseUser firebaseUser;
    String userID;
    String profileid;

    private myPostAdapter fotosAdapter;
    private List<donatePost> postList;
    RecyclerView recyclerView1;

    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    private myPostAdapter saves_adapter;
    private List<donatePost> savesList;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        intent = new Intent(getContext(), ProfileSetting.class);
        settingButton = (Button) rootView.findViewById(R.id.setting_button);
        chatButton = (Button) rootView.findViewById(R.id.chat_button);

        prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileId", "none");
        editor = prefs.edit();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

///////////////////////////////////////////////////////////////////////////////////////
        moon = rootView.findViewById(R.id.moon);
        sun = rootView.findViewById(R.id.sun);
        profilePicture = rootView.findViewById(R.id.image_profile);
        username = rootView.findViewById(R.id.username);
        bio = rootView.findViewById(R.id.user_bio);

        //Check whether user choose light or night mode
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            moon.setVisibility(View.GONE);
            sun.setVisibility(View.VISIBLE);
        }else{
            sun.setVisibility(View.GONE);
            moon.setVisibility(View.VISIBLE);
        }

        moon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt("NightModeInt",AppCompatDelegate.getDefaultNightMode());
                editor.apply();
                startActivity(new Intent(requireActivity().getApplicationContext(),MainActivity.class));
                requireActivity().finish();
            }
        });
        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt("NightModeInt",AppCompatDelegate.getDefaultNightMode());
                editor.apply();
                startActivity(new Intent(requireActivity().getApplicationContext(),MainActivity.class));
                requireActivity().finish();
            }
        });


        settingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Open profile Setting Page
                startActivity(intent);
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Chat Page
                editor.putString("chatWith", profileid);
                editor.apply();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new chattingFragment())
                        .addToBackStack("chat")
                        .commit();
            }
        });

        logout2 = (ImageView) rootView.findViewById(R.id.logout2);

        logout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LOGOUT FUNCTION
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
        fotosAdapter = new myPostAdapter(getContext(),postList);
        recyclerView1.setAdapter(fotosAdapter);

        mySaves = new ArrayList<>();
        recyclerView_saves = rootView.findViewById(R.id.saved_recycler);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(),3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        savesList = new ArrayList<>();
        saves_adapter = new myPostAdapter(getContext(),savesList);
        recyclerView_saves.setAdapter(saves_adapter);

        recyclerView1.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        checkTab();
        userProfile();
        myPosts();
        if(profileid.equals(userID)){
            mySaves();
            settingButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.GONE);
            recyclerView_saves.setVisibility(View.VISIBLE);
            ((LinearLayout) Objects.requireNonNull(tabLayout.getTabAt(1)).view).setVisibility(View.VISIBLE);
        }
        else {
            settingButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
            recyclerView_saves.setVisibility(View.GONE);
            ((LinearLayout) Objects.requireNonNull(tabLayout.getTabAt(1)).view).setVisibility(View.GONE);

        }




        return rootView;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcon[0]);
        tabLayout.getTabAt(1).setIcon(tabIcon[1]);
    }

    //Check whether user on myPost tab or savePost tab
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
    }

    //GET USER POSTS FUNCTION
    private void myPosts(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    donatePost post = data.getValue(donatePost.class);
                    if(post.getDonator().equals(profileid)){
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

    //GET USER PROFILE FUNCTION
    public void userProfile(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (getActivity() != null) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(getContext()).load(user.getImageUrl()).into(profilePicture);
                    username.setText(user.getUsername());
                    bio.setText(user.getBio());
                }


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //GET USER SAVE POSTS FUNCTION
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

    //SHOW USER SAVE POSTS FUNCTION
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