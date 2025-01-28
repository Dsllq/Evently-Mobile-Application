package com.evently.eventlyapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evently.eventlyapp.Adapters.EditorEventAdapter;
import com.evently.eventlyapp.Model.EventModel;
import com.evently.eventlyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authListener;
    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;

    ArrayList<EventModel> eventModelArrayList=new ArrayList<>();
    ArrayList<String> eventKeySize=new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
       try {
           eventModelArrayList.clear();
           getEventData();
       }catch (Exception e){

       }
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase processes
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) { // when auth state change
                    finish();
                }
            }
        };

        getEventData();

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EditorActivity.this,AddEventActivity.class);
                intent.putExtra("TYPE","NEW");
                if (eventKeySize.size()>0){
                    intent.putExtra("LAST_ITEM_KEY",
                            eventKeySize.get(eventKeySize.size()-1));
                }else {
                    intent.putExtra("LAST_ITEM_KEY","0");
                }

                startActivity(intent);
            }
        });
    }

    public void getEventData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        mRef = database.getReference().child("EVENTLYDB/events");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("MARDUK", dataSnapshot.getKey());
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    eventKeySize.add(eventSnapshot.getKey());
                    if (eventSnapshot.child("eventorUID").getValue(String.class).equals(userId)) {
                        EventModel eventModel = eventSnapshot.getValue(EventModel.class);
                        eventModel.setEventKey(eventSnapshot.getKey());

                        String imageBase64 = eventSnapshot.child("imageUrl").getValue(String.class);
                        if (imageBase64 != null && !imageBase64.isEmpty()) {
                            try {
                                byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                eventModel.setEventImageBitmap(decodedBitmap); // افترضنا وجود خاصية لتمثيل الصورة Bitmap
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }

                        boolean isDuplicate = false;
                        for (EventModel existingEvent : eventModelArrayList) {
                            if (existingEvent.getEventTitle().equals(eventModel.getEventTitle())) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            eventModelArrayList.add(eventModel);
                            Log.i("MARDUK", eventModel.getEventTitle() + "****" + eventModel.getEventorUID());
                        }
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                EditorEventAdapter editorEventAdapter =
                        new EditorEventAdapter(EditorActivity.this, eventModelArrayList, database);
                recyclerView.setAdapter(editorEventAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(EditorActivity.this));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}