package com.example.practisedoneed.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practisedoneed.Model.ChatID;
import com.example.practisedoneed.R;
import com.example.practisedoneed.adapter.chatListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//Main Chat class
//This class will show a list of chat
public class mainChatFragment extends Fragment implements View.OnClickListener{

    private chatListAdapter chatListAdapter;
    private RecyclerView recyclerView;
    private List<ChatID> chatIDList;
    private FirebaseUser firebaseUser;
    private String myID;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_chat, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myID = firebaseUser.getUid();
        recyclerView = view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        chatIDList = new ArrayList<>();
        chatListAdapter = new chatListAdapter(getContext(),chatIDList);
        recyclerView.setAdapter(chatListAdapter);

        getChat();

        return view;
    }

    @Override
    public void onClick(View v) {

    }

    //GET USER CHAT LIST FUNCTION
    public void getChat(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatID");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                chatIDList.clear();

                for(DataSnapshot data:snapshot.getChildren()){
                    ChatID chat = data.getValue(ChatID.class);
                    if(chat.getMember1().equals(myID) || chat.getMember2().equals(myID)){
                        chatIDList.add(chat);
                    }
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}
