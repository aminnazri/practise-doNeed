package com.example.practisedoneed.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.Chat;
import com.example.practisedoneed.Model.ChatID;
import com.example.practisedoneed.Model.User;
import com.example.practisedoneed.R;
import com.example.practisedoneed.fragment.testChatFrag;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.ViewHolder> {

    private Context context;
    private List<ChatID> chatslist;
    private String chatWith;
    private String profileID;

    public chatListAdapter(Context context, List<ChatID> chatslist) {
        this.context = context;
        this.chatslist = chatslist;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_chat_item, parent, false);
        return new chatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myID = user.getUid();
        ChatID chat = chatslist.get(position);
        if(chat.getMember1().equals(myID)){
            chatWith = chat.getMember2();
            holder.userChat.setText(chatWith);
        }else{
            chatWith = chat.getMember1();
            holder.userChat.setText(chatWith);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(chatWith);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getImageUrl()).into(holder.profile_picture);
                holder.username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("chatWith", holder.userChat.getText().toString());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new testChatFrag())
                        .addToBackStack("chat")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatslist.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile_picture;
        TextView username;
        TextView userChat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_picture = itemView.findViewById(R.id.profile_picture);
            username = itemView.findViewById(R.id.userID);
            userChat = itemView.findViewById(R.id.user_chat);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("chatWith", userChat.getText().toString());
                    editor.apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new testChatFrag())
                            .addToBackStack("chat")
                            .commit();
                }
            });
        }
    }
}
