package com.evently.eventlyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.evently.eventlyapp.R;

public class EditorHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_home);
        findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VisitorHomeActivity", "Button clicked!");
                Intent editorIntent=new Intent(EditorHomeActivity.this, AdminActivity.class);
                startActivity(editorIntent);
            }
        });

    }
}
