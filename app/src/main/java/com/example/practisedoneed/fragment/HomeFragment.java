package com.example.practisedoneed.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;

//import com.example.practisedoneed.databinding.FragmentHomeBinding;
import com.example.practisedoneed.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.practisedoneed.adapter.donateAdapter;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private donateAdapter postAdapter;
    private List<donatePost> postLists;
    private ProgressBar progressBar;

    RecyclerView.LayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private TextView doNeedTitle;

    private TextView textState;
    private boolean[] selectedState;
    String[] stateArray = {"All","Johor","Kedah","Kelantan","Melaka","Negeri Sembilan","Pahang","Penang","Perak","Perlis"
                        ,"Sabah","Sarawak","Selangor","Terengganu","Kuala Lumpur","Labuan","Putrajaya"};
    final List<String> stateList = Arrays.asList(stateArray);

    private TextView textCategory;
    private boolean[] selectedCategory;
    String[] categoryArray = {"All","Home Equipment","Furniture","Computer","Smartphone","Technology","Cloth","Sport"};
    final List<String> categoryList = Arrays.asList(categoryArray);

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.app_toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        doNeedTitle = toolbar.findViewById(R.id.text_doNeed);
        doNeedTitle.setVisibility(View.VISIBLE);

        textState = view.findViewById(R.id.state_filter);
        selectedState = new boolean[stateArray.length];
        textState.setOnClickListener(this);
        selectedState[0] = true;

        textCategory = view.findViewById(R.id.category_filter);
        selectedCategory = new boolean[categoryArray.length];
        textCategory.setOnClickListener(this);
        selectedCategory[0] = true;

        recyclerView = view.findViewById(R.id.recycler_viewhome);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(linearLayoutManager);

        postLists = new ArrayList<>();
        postAdapter = new donateAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        readPost();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_tool).getActionView();
        searchView.setQueryHint("Search Item");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.state_filter){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select State")
                    .setCancelable(false);
            builder.setMultiChoiceItems(stateArray, selectedState, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {


                    selectedState[which] = isChecked;
                    String currentItem = stateList.get(which);
                    Toast.makeText(getActivity(), currentItem+ " "+ isChecked, Toast.LENGTH_SHORT).show();

                    for(int i=1; i<selectedState.length; i++){
                        if(selectedState[i]){
                            selectedState[0] = false;
                            break;
                        }
                    }
                }
            });

            builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //get true item n search
                    String SB = "";
                    for (int i = 0; i<selectedState.length; i++){
                        boolean checked = selectedState[i];
                        if (checked) {
                            SB = SB + stateList.get(i) + " ";
                        }
                    }
                    Toast.makeText(getActivity(), SB, Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    for(int i=0; i<selectedState.length; i++){
//                        selectedState[i]=false;
//                        stateList.clear();
//                    }
                    Arrays.fill(selectedState, false);
                    selectedState[0] = true;
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        else if(v.getId()==R.id.category_filter){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Category")
                    .setCancelable(false);
            builder.setMultiChoiceItems(categoryArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    selectedCategory[which] = isChecked;
                    String currentItem = categoryList.get(which);
                    Toast.makeText(getActivity(), currentItem+ " "+ isChecked, Toast.LENGTH_SHORT).show();

                    for(int i=1; i<selectedCategory.length; i++){
                        if(selectedCategory[i]){
                            selectedCategory[0] = false;
                            break;
                        }
                    }
                }
            });

            builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //get true item n search
                    String SB = "";
                    for (int i = 0; i<selectedCategory.length; i++){
                        boolean checked = selectedCategory[i];
                        if (checked) {
                            SB = SB + categoryList.get(i) + " ";
                        }
                    }
                    Toast.makeText(getActivity(), SB, Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Arrays.fill(selectedCategory, false);
                    selectedCategory[0] = true;
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private  void  readPost(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                postLists.clear();
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){

                    donatePost post  = Snapshot.getValue(donatePost.class);
                    postLists.add(post);
                }
                Collections.reverse(postLists);
                postAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}