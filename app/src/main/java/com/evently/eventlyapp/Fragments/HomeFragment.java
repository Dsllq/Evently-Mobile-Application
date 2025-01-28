package com.evently.eventlyapp.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evently.eventlyapp.Adapters.StudentEventAdapter;
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

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authListener;
    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;
    RecyclerView recyclerView;
    ArrayList<EventModel> eventModelArrayList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();

        getEventData();
        return view;
    }

    public void getEventData(){

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();
        mRef =
                database.getReference().child("EVENTLYDB/"+"events");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    EventModel eventModel = eventSnapshot.getValue(EventModel.class);
                    eventModel.setEventKey(eventSnapshot.getKey());
                    // فك تشفير الصورة إذا كانت موجودة
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
                    eventModelArrayList.add(eventModel);
                    if (eventModel != null) {
                        Log.i("MARDUK", eventModel.getEventTitle());
                    }

                }

                FragmentActivity activity = getActivity();
                System.out.println(activity);
                StudentEventAdapter studentEventAdapter = new StudentEventAdapter(activity,eventModelArrayList,database,userId);
                recyclerView.setAdapter(studentEventAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}