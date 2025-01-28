package com.evently.eventlyapp.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.evently.eventlyapp.Fragments.AdminEditorListFragment;
import com.evently.eventlyapp.Fragments.EventListFragment;
import com.evently.eventlyapp.R;

public class AdminActivity extends AppCompatActivity {
    // Java code for the button click handling
    private ConstraintLayout btnEditorList;
    private ConstraintLayout btnEventList;
    private ConstraintLayout btnLogout;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnEditorList = findViewById(R.id.btnEditorList);

        btnEventList = findViewById(R.id.btnEventList);
        btnLogout = findViewById(R.id.btnLogout);

        EventListFragment eventListFragment=new EventListFragment();
        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,eventListFragment,"Event");
        fragmentTransaction.commit();

        btnEditorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform your actions here when btnEditorList is clicked

                AdminEditorListFragment adminEditorListFragment=new AdminEditorListFragment();
                FragmentTransaction fragmentTransaction=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer,adminEditorListFragment,
                        "EDITORLIST");
                fragmentTransaction.commit();
            }
        });


        btnEventList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventListFragment fragment=new EventListFragment();
                FragmentTransaction fragmentTransaction=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer,fragment,"EVENTLİST");
                fragmentTransaction.commit();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform your actions here when btnLogout is clicked
                finish();
            }
        });
    }
}