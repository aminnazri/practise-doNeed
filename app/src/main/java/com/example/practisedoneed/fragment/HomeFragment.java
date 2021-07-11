package com.example.practisedoneed.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;

//import com.example.practisedoneed.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.practisedoneed.adapter.postAdapter;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//FEED CLASS
public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private com.example.practisedoneed.adapter.postAdapter postAdapter;
    private List<donatePost> postLists;
    private List<donatePost> filteredList;
    private ProgressBar progressBar;

    RecyclerView.LayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private TextView doNeedTitle;
    SearchView searchView;


    private TextView textState;
    private boolean[] selectedState;
    String[] stateArray = {"All","Johor","Kedah","Kelantan","Melaka","Negeri Sembilan","Pahang","Penang","Perak","Perlis"
                        ,"Sabah","Sarawak","Selangor","Terengganu","Kuala Lumpur","Labuan","Putrajaya"};
    final List<String> stateList = Arrays.asList(stateArray);

    private TextView textCategory;
    private boolean[] selectedCategory;
    String[] categoryArray = {"All","Books","Home Equipment","Food","Furniture","Computer","Smartphone","Technology","Cloth","Sport"};
    final List<String> categoryList = Arrays.asList(categoryArray);

    private List<String> filteredCategory;
    private List<String> filteredState;
    Boolean wantToClose;

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
        postAdapter = new postAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setHasFixedSize(true);

        filteredCategory = new ArrayList<>();
        filteredState = new ArrayList<>();
        filteredState.add("All");
        filteredCategory.add("All");
        filteredList = new ArrayList<>();

        readPost();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_toolbar, menu);
        //SEARCH FUNCTION
        searchView = (SearchView) menu.findItem(R.id.search_tool).getActionView();
        searchView.setQueryHint("Search Item");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query!=null){
                    searchItem(query);
                }
                else{
                    readPost();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0){
                    readPost();
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        //if user click chat button on toolbar
        if(item.getItemId()==R.id.chat_tool){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new mainChatFragment())
                    .addToBackStack("chat")
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        //STATE FILTER FUNCTION
        if(v.getId()==R.id.state_filter){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select State")
                    .setCancelable(false);
            builder.setMultiChoiceItems(stateArray, selectedState, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    selectedState[which] = isChecked;
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
                    filteredCategory.clear();
                    filteredState.clear();
                    filteredList.clear();
                    for (int i = 0; i<selectedCategory.length; i++){
                        boolean checked = selectedCategory[i];

                        if (checked) {
                            filteredCategory.add(categoryList.get(i));
                        }
                    }
                    for (int i = 0; i<selectedState.length; i++){
                        boolean checked = selectedState[i];

                        if (checked) {
                            filteredState.add(stateList.get(i));
                        }
                    }
                    wantToClose=true;
                    setFiltered();
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
                    builder.show();

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        //CATEGORY FILTER FUNCTION
        else if(v.getId()==R.id.category_filter){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Category")
                    .setCancelable(false);
            builder.setMultiChoiceItems(categoryArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    selectedCategory[which] = isChecked;
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
                    filteredCategory.clear();
                    filteredState.clear();
                    filteredList.clear();
                    for (int i = 0; i<selectedCategory.length; i++){
                        boolean checked = selectedCategory[i];

                        if (checked) {
                            filteredCategory.add(categoryList.get(i));
                        }
                    }
                    for (int i = 0; i<selectedState.length; i++){
                        boolean checked = selectedState[i];

                        if (checked) {
                            filteredState.add(stateList.get(i));
                        }
                    }
                    setFiltered();
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
                    builder.show();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //SHOW POSTS FUNCTION
    //Retrieve Posts from database
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

    //SEARCH POSTS FUNCTION
    //Search by post's title
    private void searchItem(String data){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("title").startAt(data).endAt(data + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
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


    //SHOW FILTERED POSTS FUNCTION
    //Show only the filtered posts
    private void setFiltered(){

        if(filteredState.contains("All") && filteredCategory.contains("All")){
//            filteredList = postLists;
            filteredList.addAll(postLists);
        }
        else if(!filteredState.contains("All") && !filteredCategory.contains("All")){
            for(donatePost post: postLists){
                for(int i = 0; i< filteredState.size(); i++){
                    if(post.getLocation().equals(filteredState.get(i))){
                        for(int x=0; x<filteredCategory.size(); x++){
                            if(post.getCategory().equals(filteredCategory.get(x))){
                                filteredList.add(post);
                            }
                        }
                    }
                }
            }
        }
        else if(!filteredState.contains("All")){
            for(donatePost post: postLists){
                for(int i = 0; i< filteredState.size(); i++){
                    if(post.getLocation().equals(filteredState.get(i))){
                        filteredList.add(post);
                    }
                }
            }
        }
        else if(!filteredCategory.contains("All")){
            for(donatePost post: postLists){
                for(int i = 0; i< filteredCategory.size(); i++){
                    if(post.getCategory().equals(filteredCategory.get(i))){
                        filteredList.add(post);
                    }
                }
            }
        }

        postAdapter = new postAdapter(getContext(),filteredList);
        recyclerView.setAdapter(postAdapter);
    }




}