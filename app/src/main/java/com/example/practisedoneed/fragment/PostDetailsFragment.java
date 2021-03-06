package com.example.practisedoneed.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.User;
import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PostDetailsFragment extends Fragment implements View.OnClickListener {


    private donatePost post;
    private String postId;
    public FirebaseUser firebaseUser;
    public DatabaseReference mPostReference;
    private String userID;
    public ImageView postImage, saveIcon, imageProfile, optionIcon;
    public TextView title, username, description, quantity, category, location, date;
    public Button chatBtn;
    private FragmentManager fragmentManager;
    SharedPreferences.Editor editor;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId = preferences.getString("postId", "none");
        fragmentManager = getActivity().getSupportFragmentManager();

        toolbar = (Toolbar) view.findViewById(R.id.tool_bar);
        // showing the back button in action bar
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        assert appCompatActivity != null;
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);

        editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("editPostID","none");
        editor.putString("imageUrl","none");
        editor.putString("date", "none");
        editor.apply();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        postImage = view.findViewById(R.id.post_image);
        saveIcon = view.findViewById(R.id.save);
        optionIcon = view.findViewById(R.id.optionIcon);
        imageProfile = view.findViewById(R.id.image_profile);
        title = view.findViewById(R.id.post_title);
        username = view.findViewById(R.id.usernamepost);
        description = view.findViewById(R.id.description);
        quantity = view.findViewById(R.id.quantity);
        category = view.findViewById(R.id.category);
        location = view.findViewById(R.id.location);
        chatBtn = view.findViewById(R.id.chat_button);
        date = view.findViewById(R.id.date);

        optionIcon.setOnClickListener(this);
        saveIcon.setOnClickListener(this);
        chatBtn.setOnClickListener(this);
        imageProfile.setOnClickListener(this);


        readPostDetails();


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void publisherInfo(final ImageView image_profile, final TextView donator, String userid) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(getActivity() != null){
                    User user = dataSnapshot.getValue(User.class);

                    Glide.with(getContext()).load(user.getImageUrl()).into(image_profile);

                    donator.setText(user.getUsername());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    public void isSaved(String postId, ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //Delete Post Function
    public void showDialog(String title, CharSequence message, String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (title != null) {
            builder.setTitle(title);
        }

        builder.setMessage(message);
//        builder.show();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //del post
                mPostReference = FirebaseDatabase.getInstance().getReference()
                        .child("Posts").child(postId);
                mPostReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        fragmentManager.popBackStack();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void readPostDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (getActivity() == null) {
                    return;
                }

                if (snapshot.exists()) {
                    post = snapshot.getValue(donatePost.class);
                    isSaved(post.getId(), saveIcon);
                    publisherInfo(imageProfile, username, post.getDonator());
                    Glide.with(getActivity()).load(post.getImage()).into(postImage);
                    title.setText(post.getTitle());
                    description.setText(post.getDescription());
                    quantity.setText(post.getQuantity());
                    category.setText(post.getCategory());
                    location.setText(post.getLocation());
                    date.setText(post.getDate());

                    if (post.getDonator().equals(userID)) {
                        chatBtn.setVisibility(View.INVISIBLE);
                        saveIcon.setVisibility(View.GONE);
                        optionIcon.setVisibility(View.VISIBLE);
                    } else {
                        chatBtn.setVisibility(View.VISIBLE);
                        saveIcon.setVisibility(View.VISIBLE);
                        optionIcon.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.optionIcon) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.post_option);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.edit_post) {
                        editor.putString("editPostID", post.getId());
                        editor.putString("imageUrl", post.getImage());
                        editor.putString("defaultDate", post.getDate());
                        editor.apply();

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new donateFragment())
                                .addToBackStack("donate")
                                .commit();

                    } else if (item.getItemId() == R.id.del_post) {
                        //delete posts confirmation
                        showDialog("Confrimation", "Confirm Delete?", post.getId());
                    }
                    return false;
                }
            });
            popupMenu.show();
        } else if (v.getId() == R.id.save) {
            if (saveIcon.getTag().equals("save")) {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getId()).setValue("true");
            } else {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getId()).removeValue();
            }

        } else if (v.getId() == R.id.image_profile) {
//            SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("profileId", post.getDonator());
            editor.apply();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new profileFragment())
                    .addToBackStack("profile")
                    .commit();
        } else if (v.getId() == R.id.chat_button) {
//            SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("chatWith", post.getDonator());
            editor.apply();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new chattingFragment())
                    .addToBackStack("chat")
                    .commit();
        }
    }
}