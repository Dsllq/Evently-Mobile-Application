package com.evently.eventlyapp.Activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.evently.eventlyapp.Model.EventModel;
import com.evently.eventlyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class AddEventActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser user ;
    String userId;

    private TextInputEditText etEventTitle, etEventDetails, etEventDate, etEventLocation;
    private Button btnSave;
    String lastKey;

    private LatLng mSelectedLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivEventImage;
    private String imageBase64 = null;

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 67);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 76);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        requestLocationPermission();//location perm request
        ivEventImage = findViewById(R.id.ivEventImage);
        lastKey=getIntent().getExtras().getString("LAST_ITEM_KEY");
        EditText etAnnouncementDate = findViewById(R.id.etAnnouncementDate);
        //firebase processes
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database =  FirebaseDatabase.getInstance();
        user =  auth.getCurrentUser();
        userId = user.getUid();

        mRef=
                database.getReference().child("EVENTLYDB/"+"/events/"+String.valueOf(Integer.parseInt(lastKey)+1));
        //initialize
        etEventTitle = findViewById(R.id.etAnnouncementTitle);
        etEventDetails = findViewById(R.id.etAnnouncementDetails);
        etEventDate = findViewById(R.id.etAnnouncementDate);
        etEventLocation = findViewById(R.id.etEventLocation);
        btnSave = findViewById(R.id.btnClose);
        etAnnouncementDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialogTheme,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etAnnouncementDate.setText(selectedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
        findViewById(R.id.btnUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventTitle = etEventTitle.getText().toString().trim();
                String eventDetails = etEventDetails.getText().toString().trim();
                String eventDate = etEventDate.getText().toString().trim();
                String eventLocation = etEventLocation.getText().toString().trim();

                if (eventTitle.isEmpty()) {
                    etEventTitle.setError("Event Title is required");
                    etEventTitle.requestFocus();
                    return;
                }

                if (eventDetails.isEmpty()) {
                    etEventDetails.setError("Event Details is required");
                    etEventDetails.requestFocus();
                    return;
                }

                if (eventDate.isEmpty()) {
                    etEventDate.setError("Event Date is required");
                    etEventDate.requestFocus();
                    return;
                }

                if (eventLocation.isEmpty()) {
                    etEventLocation.setError("Event Location is required");
                    etEventLocation.requestFocus();
                    return;
                }

                if (imageBase64 == null) {
                    Toast.makeText(AddEventActivity.this, "Please Choose a photo first", Toast.LENGTH_SHORT).show();
                    return;
                }


                ProgressDialog progressDialog = new ProgressDialog(AddEventActivity.this);
                progressDialog.setMessage("Adding Event...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mRef.setValue(new EventModel(imageBase64, eventTitle, eventDetails, eventDate, userId, 0,
                        eventLocation)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(AddEventActivity.this, "Event added!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEventActivity.this, "Failed to add event. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //map initializng
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivEventImage.setImageBitmap(bitmap);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
//        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            mMap.addMarker(markerOptions);
            mSelectedLocation = latLng;
            Toast.makeText(AddEventActivity.this, "Selected: " + latLng.toString(),
                    Toast.LENGTH_SHORT).show();
            etEventLocation.setText(latLng.latitude+","+latLng.longitude);
        });

        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Set API key
        try {
            if (!TextUtils.isEmpty(getString(R.string.google_maps_api_key))) {
                String apiKey = getString(R.string.google_maps_api_key);
                MapsInitializer.initialize(this.getApplicationContext());

                // Show user's current location
                mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mMap.animateCamera(cameraUpdate);
                        etEventLocation.setText(latLng.latitude+","+latLng.longitude);
                    }
                });
            } else {
                Toast.makeText(this, "Google maps not available", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show user's current location
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {

            }
        } catch (SecurityException e) {
            e.printStackTrace();
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
}