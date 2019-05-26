package com.freejob.freejob.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.freejob.freejob.Adapters.Message_List_Adapter;

import com.freejob.freejob.Globals.Config;
import com.freejob.freejob.Items.Chat;
import com.freejob.freejob.Items.Message;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    User sender, receiver;
    Message_List_Adapter adapter;
    Message message;
    static Chat chat;
    int worker_profile;

    ImageView worker_image;
    TextView Name;
    EditText message_to_send;
    Button send;
    List<Message> messages;
    private ListView list_messages;


    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initFirebase();
        setUsers();
        getChat();
        initVariables();
        getMessages();

    }

    private void initVariables() {
        Name = findViewById(R.id.ACH_name);


        message_to_send = findViewById(R.id.ACH_Message_to_Send);
        send = findViewById(R.id.ACH_Send_Message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
    }

    private void SendMessage() {

       message = new Message(message_to_send.getText().toString(), true, sender, chat.getMessages().size());
       chat.getMessages().add(message);
       reference.child("Chats").child("Chat"+chat.getUid()).child("messages").setValue(chat.getMessages()).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               initFirebase();
               setUsers();
               getChat();
               initVariables();
               getMessages();
               message_to_send.setText("");

           }
       });

    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        reference =  FirebaseDatabase.getInstance().getReference();

    }
    private void setUsers(){
         reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 sender = dataSnapshot.getValue(User.class);

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
         String uid = getIntent().getStringExtra("uid");
        reference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                receiver = dataSnapshot.getValue(User.class);
                Name.setText(receiver.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChat() {
        reference.child("Chats").child("Chat"+getIntent().getStringExtra("chat_uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatActivity.chat = dataSnapshot.getValue(Chat.class);
                setList(chat.getMessages());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getMessages(){
        reference.child("Chats").child("Chat"+getIntent().getStringExtra("chat_uid")).child("messages").orderByChild("messageid").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messages = new ArrayList<>();
                Message message = dataSnapshot.getValue(Message.class);
                messages.add(message);
                HandleNotifications(message);
               getChat();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                messages = new ArrayList<>();
                Message message = dataSnapshot.getValue(Message.class);
                messages.add(message);
                HandleNotifications(message);
                getChat();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messages = new ArrayList<>();
                Message message = dataSnapshot.getValue(Message.class);
                messages.add(message);
                setList(messages);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                messages = new ArrayList<>();
                Message message = dataSnapshot.getValue(Message.class);
                messages.add(message);
                setList(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void HandleNotifications(Message message) {
      if(!message.getSender().getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
          Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
          PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent.putExtra("uid",getIntent().getStringExtra("uid")).putExtra("chat_uid", getIntent().getStringExtra("chat_uid")), PendingIntent.FLAG_UPDATE_CURRENT);
          NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

          b.setAutoCancel(true)
                  .setDefaults(Notification.DEFAULT_ALL)
                  .setWhen(System.currentTimeMillis())
                  .setSmallIcon(R.drawable.freejob_icon)
                  .setContentTitle(message.getSender().getName())
                  .setContentText(message.getMessage())
                  .setContentIntent(contentIntent);

          NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
          notificationManager.notify(1, b.build());
      }
    }



    private void setList(List<Message> messages) {
        adapter = null;
        list_messages = findViewById(R.id.ACH_Messages);
        list_messages.setAdapter(adapter);


        adapter = new Message_List_Adapter(messages, ChatActivity.this, sender);

        list_messages.setAdapter(adapter);


    }

    @Override
    protected void onPause() {
        getMessages();
        super.onPause();
    }

    @Override
    protected void onResume() {
        readMessages(messages);
        super.onResume();
    }

    private void readMessages(List<Message> messages){
        int cont = 1;
        if(messages != null)
    while(messages.size() >= cont){
        if(!messages.get(cont-1).getSender().getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
        reference.child("Chats").child("Chat"+chat.getUid()).child("messages").child("isNew").setValue(false);
        }
        cont++;
    }
    }
}
