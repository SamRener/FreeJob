package com.freejob.freejob.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.freejob.freejob.Adapters.Schedule_List_Adapter;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.Request;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    CalendarView calendar;
    ListView requests_list;
    Schedule_List_Adapter adapter;

    DatabaseReference reference;
    FirebaseAuth auth;
    User user;
    List<Request> requests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initVariables();
    }

    private void initVariables() {
        reference = new CommonMethods().getRef(ScheduleActivity.this);
        auth = new CommonMethods().getAuth(ScheduleActivity.this);
        calendar = findViewById(R.id.SC_calendar);
        requests_list = findViewById(R.id.SC_requests);
        getUser();
        LoadCurrentServices();
        getRequestsDates();
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String mon = "Jan.";
                switch(month){
                    case 0: mon = "Jan."; break;
                    case 1: mon = "Fev."; break;
                    case 2: mon = "Mar."; break;
                    case 3: mon = "Abr."; break;
                    case 4: mon = "Mai."; break;
                    case 5: mon = "Jun."; break;
                    case 6: mon = "Jul."; break;
                    case 7: mon = "Ago."; break;
                    case 8: mon = "Set."; break;
                    case 9: mon = "Out."; break;
                    case 10: mon = "Nov."; break;
                    case 11: mon = "Dez."; break;
                }
                mon = (dayOfMonth+" de "+mon+" de "+year);
                setRequestList(mon);
            }
        });
    }



    private void setRequestList(String date) {
        int cont = 1;
        List<Request> requestsPerDate = new ArrayList<>();
        while (requests.size() >= cont) {
            if (requests.get(cont - 1).getData().equals(date)) {
                requestsPerDate.add(requests.get(cont - 1));
            }
            cont++;
        }
        adapter = new Schedule_List_Adapter(ScheduleActivity.this, requestsPerDate);
        requests_list.setAdapter(adapter);

    }
    public void LoadCurrentServices(){
        reference.child("Requests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                if(request.isAccepted()){
                    if(request.getWorker().getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && request.isPayed()){
                       requests.add(request);
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
    public void getUser() {
        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ScheduleActivity.this.user = user;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getRequestsDates() {
        int cont = 1;
        while (requests.size() >= cont) {

            cont++;
        }
    }

}
