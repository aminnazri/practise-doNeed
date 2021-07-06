package com.example.practisedoneed.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.Chat;
import com.example.practisedoneed.Model.ChatID;
import com.example.practisedoneed.Model.User;
import com.example.practisedoneed.R;
import com.example.practisedoneed.adapter.messageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Chat class
//This class will enable user to chat with another user
public class chattingFragment extends Fragment implements View.OnClickListener {

    private String chatWith, message, myID, chatID;
    FirebaseUser firebaseUser;
    EditText et_message;
    ImageView send, profile_picture;
    TextView username;

    DatabaseReference reference;

    List<Chat> chatsList;
    messageAdapter messageAdapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        chatWith = preferences.getString("chatWith", "none");

        recyclerView = view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        send = view.findViewById(R.id.send_btn);
        et_message = view.findViewById(R.id.edit_text_message);
        profile_picture = view.findViewById(R.id.profile_picture);
        username = view.findViewById(R.id.userID);

        send.setOnClickListener(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myID = firebaseUser.getUid();


        readMessages();


        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_btn) {
            //if user click send button
            sendMessage(myID, chatWith, et_message.getText().toString());
            et_message.setText("");
        }
    }

    //SHOW ALL MESSAGE IN THE CHAT FUNCTION
    //will read/retrieve all message from database
    private void readMessages() {

        chatsList = new ArrayList<>();

        DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("Users").child(chatWith);
        userdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageUrl()).into(profile_picture);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatID");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        ChatID chat = data.getValue(ChatID.class);
                        if ((chat.getMember1().equals(myID) || chat.getMember1().equals(chatWith)) &&
                                (chat.getMember2().equals(myID) || chat.getMember2().equals(chatWith))) {
                            chatID = chat.getId();

                        }
                    }
                    if (chatID == null) {
                        String uploadId = databaseReference.push().getKey();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", uploadId);
                        hashMap.put("member1", myID);
                        hashMap.put("member2", chatWith);

                        databaseReference.child(uploadId).setValue(hashMap);
                        chatID = uploadId;
                    }
                } else {
                    String uploadId = databaseReference.push().getKey();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", uploadId);
                    hashMap.put("member1", myID);
                    hashMap.put("member2", chatWith);

                    databaseReference.child(uploadId).setValue(hashMap);
                    chatID = uploadId;

                }

                if (chatID != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages").child(chatID);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            chatsList.clear();

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Chat chats = ds.getValue(Chat.class);
                                chatsList.add(chats);
                                messageAdapter = new messageAdapter(getContext(), chatsList);
                                recyclerView.setAdapter(messageAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    //SEND MESSAGE FUNCTION
    private void sendMessage(final String myID, final String chatWithID, final String message) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String messageID = reference.child("Messages").child(chatID).push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myID);
        hashMap.put("receiver", chatWithID);
        hashMap.put("message", message);

        reference.child("Messages").child(chatID).child(messageID).setValue(hashMap);
    }
}
