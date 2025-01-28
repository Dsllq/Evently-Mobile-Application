package com.evently.eventlyapp.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.evently.eventlyapp.R;

public class ProfileActivity extends Activity {
 
    private TextView tvExit;
    TextInputEditText tvNameSurname,tvMail,tvRole;
    private Button btnChangePassword;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authListener;
    private String str;

    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;
    public void GetUserData(){
        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();
        mRef =
                database.getReference().child("EVENTLYDB/Users/"+userId+"/UID");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNameSurname.setText(dataSnapshot.child("name").getValue(String.class));
                tvMail.setText(dataSnapshot.child("mail").getValue(String.class));
                tvRole.setText(dataSnapshot.child("role").getValue(String.class));
                Log.i("MARDUK",dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
 
        auth = FirebaseAuth.getInstance();
        GetUserData();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user
 
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) { // when auth state change
                    finish();
                }
            }
        };

        tvExit =findViewById(R.id.tvExit);
        tvNameSurname =findViewById(R.id.etRegName);
        tvMail =findViewById(R.id.etRegEmail);
        tvRole =findViewById(R.id.etRole);
        btnChangePassword =findViewById(R.id.btnChangePassword);

 
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = "Please enter the new password.";
                changeEmailOrPasswordFunc(str,false);
            }
        });
 
    }
 
    private void signOutFunc() {
 
        auth.signOut();
        finishAffinity();
    }
 
    private void changeEmailOrPasswordFunc(String title, final boolean option) {
 
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ProfileActivity.this);
        final EditText edit = new EditText(ProfileActivity.this);
        builder.setPositiveButton(getString(R.string.change_txt), null);
        builder.setNegativeButton(getString(R.string.close_txt), null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        edit.setLayoutParams(lp);
        if(!option){  // password type
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        builder.setTitle(title);
        builder.setView(edit);
 
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
 
            @Override
            public void onShow(DialogInterface dialog) {
 
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
 
                    @Override
                    public void onClick(View view) {
 
                        if(edit.getText().toString().isEmpty()){
 
                            edit.setError("Please fill in the relevant field!");
 
                        }else{
 
                            {
                                changePassword();
                            }
                        }
 
                    }
                });
            }
 
            private void changePassword() {
 
                firebaseUser.updatePassword(edit.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "The password was changed.", Toast.LENGTH_LONG).show();
                                    signOutFunc();
                                } else {
                                    edit.setText("");
                                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        mAlertDialog.show();
    }
}