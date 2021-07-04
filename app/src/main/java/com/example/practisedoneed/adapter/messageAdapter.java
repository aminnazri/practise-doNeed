package com.example.practisedoneed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practisedoneed.Model.Chat;
import com.example.practisedoneed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder> {

    Context context;
    List<Chat> chatslist;

    public static final int MESSAGE_RIGHT = 0; // FOR ME (
    public static final int MESSAGE_LEFT = 1; // FOR FRIEND

    public messageAdapter(Context context, List<Chat> chatslist) {
        this.context = context;
        this.chatslist = chatslist;
    }

    @NonNull
    @NotNull
    @Override
    public messageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_right_item, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_left_item, parent, false);
        }
        return new messageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull messageAdapter.ViewHolder holder, int position) {
        Chat chats  = chatslist.get(position);

        holder.messageTV.setText(chats.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatslist.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (chatslist.get(position).getSender().equals(user.getUid())) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTV = itemView.findViewById(R.id.message_text);

        }
    }
}
