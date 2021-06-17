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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practisedoneed.MainActivity;
import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.ProfileSetting;
import com.example.practisedoneed.R;

import com.example.practisedoneed.adapter.myFotosAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
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
    private Button logout;
    private TabLayout tabLayout;
    private int[] tabIcon = {R.drawable.ic_grid, R.drawable.ic_save};

    String profileid;

    private myFotosAdapter fotosAdapter;
    private List<donatePost> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        intent = new Intent(getActivity(), ProfileSetting.class);
        final Button button = (Button) rootView.findViewById(R.id.setting_button);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");


///////////////////////////////////////////////////////////////////////////////////////

        ImageView logout2 = (ImageView) rootView.findViewById(R.id.logout2);

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
            }
        });

        tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        setupTabIcons();

        RecyclerView recyclerView1 = rootView.findViewById(R.id.post_recycler);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView1.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        fotosAdapter = new myFotosAdapter(getContext(),postList);
        recyclerView1.setAdapter(fotosAdapter);

        myFotos();

        return rootView;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcon[0]);
        tabLayout.getTabAt(1).setIcon(tabIcon[1]);
    }

    private void myFotos(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    donatePost post = data.getValue(donatePost.class);
//                    if(post.getDonator().equals(profileid)){
//                        postList.add(post);
//                    }
                    postList.add(post);

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
}