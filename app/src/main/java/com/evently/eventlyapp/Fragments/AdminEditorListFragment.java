package com.evently.eventlyapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.evently.eventlyapp.Adapters.EditorListAdapter;
import com.evently.eventlyapp.Model.MainUserModel;
import com.evently.eventlyapp.R;

import java.util.ArrayList;
import java.util.Locale;


public class AdminEditorListFragment extends Fragment {
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        auth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();

        getEditprListData();
        return view;
    }

    public void getEditprListData(){

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();
        mRef = database.getReference().child("EVENTLYDB").child("Users");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<MainUserModel> mainUserModelList = new ArrayList<>();

                System.out.println("user editor list");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child("UID").child("role").getValue(String.class).toLowerCase(Locale.ROOT).equals(
                            "editor")){

                        System.out.println("user editor list: " + userSnapshot);

                        String uid = userSnapshot.getKey(); // UID'yi al
                        Log.i("USER LIST",uid);
                        MainUserModel mainUserModel = userSnapshot.child("UID").getValue(MainUserModel.class);
                        mainUserModel.setUserUID(uid);
                        mainUserModelList.add(mainUserModel);
                    }
                    EditorListAdapter editorListAdapter=new EditorListAdapter(getActivity(),
                            mainUserModelList);
                    recyclerView.setAdapter(editorListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}