package com.freejob.freejob.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.freejob.freejob.Adapters.Chat_List_Adapter;
import com.freejob.freejob.Items.Chat;
import com.freejob.freejob.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Chat_List_Activity extends AppCompatActivity {

    List<Chat> chatsList = new ArrayList<>();
    Chat_List_Adapter adapter;

    ListView chats;

    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatlist_activity);
        initFirebase();
        initList();
        initVariables();


    }



    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        reference = FirebaseDatabase.getInstance().getReference();
    }

    private void initList() {
        reference.child("Chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Chat chat = dataSnapshot.getValue(Chat.class);
                if(chat.getClient().getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || chat.getWorker().getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    chatsList.add(chat);
                    initVariables();
                    chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(Chat_List_Activity.this, ChatActivity.class).putExtra("uid",chat.getWorker().getUuid()).putExtra("chat_uid",chat.getUid()));
                            Toast.makeText(Chat_List_Activity.this, chat.getWorker().getUuid(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initVariables() {
        chats = findViewById(R.id.CLA_listChats);
        adapter = null;
        chats.setAdapter(adapter);
        adapter = new Chat_List_Adapter(chatsList, this);
        chats.setAdapter(adapter);



    }
}
