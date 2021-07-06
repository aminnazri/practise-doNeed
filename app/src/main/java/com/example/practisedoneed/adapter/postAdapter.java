package com.example.practisedoneed.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.User;
import com.example.practisedoneed.Model.donatePost;
import com.example.practisedoneed.R;
import com.example.practisedoneed.fragment.PostDetailsFragment;
import com.example.practisedoneed.fragment.profileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//ADAPTER FOR FEED FRAGMENT
public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder> {

    public Context mContext;
    public List<donatePost> mPost;
    private FirebaseUser firebaseUser;

    public postAdapter(Context mContext, List<donatePost> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_item,parent,false);

        return new postAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final donatePost post = mPost.get(position);
        //Set donator profile picture
        Glide.with(mContext).load(post.getImage()).into(holder.post_image);
        //set post title
        holder.title.setText(post.getTitle());
        if(post.getDonator().equals(firebaseUser.getUid())){
            holder.save.setVisibility(View.GONE);
        }else {
            holder.save.setVisibility(View.VISIBLE);
        }
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Profile Fragment
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",post.getDonator());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profileFragment())
                        .addToBackStack("profile")
                        .commit();
            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Post Detail Fragment
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",post.getId());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailsFragment())
                        .addToBackStack("details")
                        .commit();

            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SAVE POSTS FUNCTION
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getId()).setValue("true");
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getId()).removeValue();
                }
            }
        });

        isSaved(post.getId(), holder.save);
        donatorInfo(holder.image_profile, holder.username, post.getDonator());
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image, save;
        public TextView username, title, quantity, category, location, description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.usernamepost);
            title = itemView.findViewById(R.id.title);
            quantity = itemView.findViewById(R.id.quantity);
            category = itemView.findViewById(R.id.category);
            location = itemView.findViewById(R.id.location);
            description = itemView.findViewById(R.id.description);

        }
    }

    //Check if the activity is destroyed or not
    //Prevent the apps from crash because of destroyed activity
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    //GET DONATOR INFO FUNCTION
    private  void donatorInfo(final ImageView image_profile, final TextView donator , String userid ){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                if(isValidContextForGlide(mContext)){
                    Glide.with(mContext).load(user.getImageUrl()).into(image_profile);
                    donator.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    //CHECK SAVED POST FUNCTION
    //If the post is saved, the save icon will turn to black
    public void isSaved(String postId, ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}