package com.freejob.freejob.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.freejob.freejob.Adapters.Request_List_Adapter;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.Request;
import com.freejob.freejob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    DatabaseReference reference;
    List<Request> requests = new ArrayList<>();
    ListView listView_requests;
    Request_List_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        initVariables();
    }

    private void initVariables() {
        reference = new CommonMethods().getRef(RequestsActivity.this);
        listView_requests = findViewById(R.id.ARS_requests);
        reference.child("Requests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                if (request.getClient().getUuid() != null) {
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(request.getClient().getUuid())) {
                        requests.add(request);
                        adapter = null;
                        listView_requests.setAdapter(null);
                        adapter = new Request_List_Adapter(RequestsActivity.this, requests);
                        listView_requests.setAdapter(adapter);

                    }

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
}
