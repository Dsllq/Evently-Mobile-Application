package com.evently.eventlyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.evently.eventlyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        if (user!=null){
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_welcome);
    }
    public void clickButton(View view) {
        switch (view.getId()){
            case R.id.btnAdmin:
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.btnEditor:
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            finish();
                break;
            case R.id.btnStudent:
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
               break;
            case R.id.btnRegister:
                startActivity(new Intent(WelcomeActivity.this,RegisterActivity.class));
                finish();
                break;
        }
    }
}