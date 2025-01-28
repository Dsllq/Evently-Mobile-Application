package com.evently.eventlyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.evently.eventlyapp.Fragments.AdminHomeFragment;
import com.evently.eventlyapp.Fragments.EditorHomeFragment;
import com.evently.eventlyapp.Fragments.HomeFragment;
import com.evently.eventlyapp.Fragments.MapsFragment;
import com.evently.eventlyapp.Fragments.VisitorHomeFragment;
import com.evently.eventlyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView imgHome,imgClubs,imgAdmin,imgLogout,imgListView,imgMapView;
    TextView tvHome,tvClubs,tvAdmin,tvLogout,tvMapView,tvListView;
    ConstraintLayout btnAdmin;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String fragment1 = getIntent().getStringExtra("fragment");
        if (fragment1!= null && fragment1.equals("events")) {
            FrameLayout container = findViewById(R.id.fragmentContainer);
            bottomLastColor();
            bottomSelector(container,imgHome,tvHome,true);
            HomeFragment fragment=new HomeFragment();
            FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer,fragment,"HOME");
            fragmentTransaction.commit();
            findViewById(R.id.linearLayoutTop).setVisibility(View.VISIBLE);
        }
        btnAdmin=findViewById(R.id.btnAdmin);
        imgHome=findViewById(R.id.imgHome);
        tvMapView=findViewById(R.id.tvMapView);
        imgListView=findViewById(R.id.imgList);
        imgMapView=findViewById(R.id.imgMap);
        tvListView=findViewById(R.id.tvList);
        imgAdmin=findViewById(R.id.imgAdmin);
        imgLogout=findViewById(R.id.imgLogout);




        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference=
                firebaseDatabase.getReference("EVENTLYDB/Users/"+user.getUid()+"/UID/role");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 role=snapshot.getValue(String.class).toLowerCase(Locale.ROOT);
                navigateToHome(role);
                Log.d("RoleValue", "The value of role is: " + role);

                if (role.equals("Visitor")){

                } else if (role.equals("editor")){
                    DatabaseReference databaseReference=
                            firebaseDatabase.getReference("EVENTLYDB/Users/"+user.getUid()+"/UID" +
                                    "/enableEditor");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue(Boolean.class)!=null&&snapshot.getValue(Boolean.class)){
                                btnAdmin.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else if (role.equals("admin")){
                    btnAdmin.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    ImageView lastImageviewBottomMenu;

    TextView lastTextviewBottomMenu;

    public void bottomSelector(FrameLayout container,ImageView imageView, TextView textView, boolean isActive) {
        if (isActive) {
            lastImageviewBottomMenu = imageView;
            lastTextviewBottomMenu = textView;
            imageView.setImageTintList(ContextCompat.getColorStateList(this, R.color.menuEnable));
            textView.setTextColor(ContextCompat.getColor(this, R.color.menuEnable));

            container.setBackgroundResource(R.drawable.active_background);

        } else {
            imageView.setImageTintList(ContextCompat.getColorStateList(this, R.color.menuDisable));
            textView.setTextColor(ContextCompat.getColor(this, R.color.menuDisable));
            container.setBackgroundResource(0);

        }
    }
    private void loadFragment(HomeFragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
    public void topLastColor() {
        try {
            lastImageviewTopmMenu.setImageTintList(ContextCompat.getColorStateList(this,R.color.menuDisable));
            lastTextviewTopMenu.setTextColor(ContextCompat.getColor(this,R.color.menuDisable));

        } catch (Exception e) {
        }

    }
    public void topSelector(ImageView imageView, TextView textView, boolean isActive) {
        if (isActive) {
            lastImageviewTopmMenu = imageView;
            lastTextviewTopMenu = textView;
            imageView.setImageTintList(ContextCompat.getColorStateList(this, R.color.menuEnable));
            textView.setTextColor(ContextCompat.getColor(this, R.color.menuEnable));

        } else {
            imageView.setImageTintList(ContextCompat.getColorStateList(this, R.color.menuDisable));
            textView.setTextColor(ContextCompat.getColor(this, R.color.menuDisable));

        }
    }

    public void bottomLastColor() {
        try {
            lastImageviewBottomMenu.setImageTintList(ContextCompat.getColorStateList(this,R.color.menuDisable));
            lastTextviewBottomMenu.setTextColor(ContextCompat.getColor(this,R.color.menuDisable));

        } catch (Exception e) {
        }

    }

    ImageView lastImageviewTopmMenu;
    TextView lastTextviewTopMenu;
    private void navigateToHome(String role) {
        FrameLayout container = findViewById(R.id.fragmentContainer);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (role.equals("Visitor")) {
            VisitorHomeFragment visitorHomeFragment = new VisitorHomeFragment();
            fragmentTransaction.replace(R.id.fragmentContainer, visitorHomeFragment, "HOME");
        } else if (role.equals("admin")) {
            AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
            fragmentTransaction.replace(R.id.fragmentContainer, adminHomeFragment, "HOME");
        } else if (role.equals("editor")) {
            EditorHomeFragment editorHomeFragment = new EditorHomeFragment();
            fragmentTransaction.replace(R.id.fragmentContainer, editorHomeFragment, "HOME");
        } else {

            EditorHomeFragment editorHomeFragment = new EditorHomeFragment();
            fragmentTransaction.replace(R.id.fragmentContainer, editorHomeFragment, "HOME");
        }

        fragmentTransaction.commit();

        bottomLastColor();
        bottomSelector(container, imgClubs, tvClubs, true);
    }

    public void ClickMenu(View view){
        FrameLayout container = findViewById(R.id.fragmentContainer);
        switch (view.getId()){
            case R.id.btnHome:
                topLastColor();
                topSelector(imgListView,tvListView,true);
                bottomLastColor();
                bottomSelector(container,imgHome,tvHome,true);
                HomeFragment fragment=new HomeFragment();
                FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer,fragment,"HOME");
                fragmentTransaction.commit();
                findViewById(R.id.linearLayoutTop).setVisibility(View.VISIBLE);
                break;
            case R.id.btnClubs:
                if (role.equals("Visitor")){
                    VisitorHomeFragment visitorHomeFragment=new VisitorHomeFragment();
                    FragmentTransaction fragmentTransaction1=
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.fragmentContainer,visitorHomeFragment,"visitor Home ");
                    fragmentTransaction1.commit();
                    bottomLastColor();
                    bottomSelector(container,imgClubs,tvClubs,true);
                    findViewById(R.id.linearLayoutTop).setVisibility(View.GONE);
                }
                else if (role.equals("admin")){
                    AdminHomeFragment adminHomeFragment=new AdminHomeFragment();
                    FragmentTransaction fragmentTransaction1=
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.fragmentContainer,adminHomeFragment,"Admin Home ");
                    fragmentTransaction1.commit();
                    bottomLastColor();
                    bottomSelector(container,imgClubs,tvClubs,true);
                    findViewById(R.id.linearLayoutTop).setVisibility(View.GONE);

                }
                else if (role.equals("editor")){
                    EditorHomeFragment editorHomeFragment=new EditorHomeFragment();
                    FragmentTransaction fragmentTransaction1=
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.fragmentContainer,editorHomeFragment,"Editor Home ");
                    fragmentTransaction1.commit();
                    bottomLastColor();
                    bottomSelector(container,imgClubs,tvClubs,true);
                    findViewById(R.id.linearLayoutTop).setVisibility(View.GONE);


                }

                break;
            case R.id.btnAdmin:
                if (role.equals("admin")){
                    Intent editorIntent=new Intent(MainActivity.this, AdminActivity.class);
                    startActivity(editorIntent);
                }else {
                    Intent editorIntent=new Intent(MainActivity.this, EditorActivity.class);
                    startActivity(editorIntent);
                }

                bottomLastColor();
                bottomSelector(container,imgAdmin,tvAdmin,true);
                break;
            case R.id.btnLogout:
                bottomLastColor();
                bottomSelector(container,imgLogout,tvLogout,true);
                FirebaseAuth auth=FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
                break;

            case R.id.btnMapView:
                topLastColor();
                topSelector(imgMapView,tvMapView,true);
                MapsFragment mapsFragment=new MapsFragment();
                FragmentTransaction fragmentTransaction2=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.fragmentContainer,mapsFragment,"MAPS");
                fragmentTransaction2.commit();
                break;
            case R.id.btnList:
                topLastColor();
                topSelector(imgListView,tvListView,true);
                HomeFragment fragments=new HomeFragment();
                FragmentTransaction fragmentTransactions=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransactions.replace(R.id.fragmentContainer,fragments,"HOME");
                fragmentTransactions.commit();
                break;
        }

    }
}