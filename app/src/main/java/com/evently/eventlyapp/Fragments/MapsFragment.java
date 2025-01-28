package com.evently.eventlyapp.Fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.evently.eventlyapp.Model.EventModel;
import com.evently.eventlyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

  private FirebaseAuth auth;
  private FirebaseUser firebaseUser;
  private FirebaseAuth.AuthStateListener authListener;
  FirebaseDatabase database;
  DatabaseReference mRef;
  FirebaseUser user ;
  String userId;
  ArrayList<EventModel> eventModelArrayList=new ArrayList<>();
  private LatLng mSelectedLocation;
  private FusedLocationProviderClient mFusedLocationClient;
  private GoogleMap mMap;

  private void requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(),
              new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 67);
    }
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(),
              new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 76);
    }
  }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment_maps, container, false);

    requestLocationPermission();
    auth = FirebaseAuth.getInstance();
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    database =  FirebaseDatabase.getInstance();
    user =  auth.getCurrentUser();
    userId = user.getUid();

    getEventData();
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    return view;
    }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
      return;
    }
    mMap.setMyLocationEnabled(true);

    try {
      boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
      if (!success) {
        Log.e(TAG, "Style parsing failed.");
      }
    } catch (Resources.NotFoundException e) {
      Log.e(TAG, "Can't find style. Error: ", e);
    }

    try {
      if (!TextUtils.isEmpty(getString(R.string.google_maps_api_key))) {
        String apiKey = getString(R.string.google_maps_api_key);
        MapsInitializer.initialize(getActivity().getApplicationContext());

        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
          if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
          }
        });
      } else {
        Toast.makeText(getActivity(), "Google maps not available", Toast.LENGTH_LONG).show();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      LocationManager locationManager =
              (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        // Marker tıklandığında yapılacak işlemler
        // Örneğin, marker'ın konumunu ve başlığını alabilirsiniz:
        LatLng position = marker.getPosition();
        String title = marker.getTitle();

        customAlertDialog(Integer.parseInt(marker.getId().replace("m","")));
        // İlgili işlemleri gerçekleştirin

        return true;
      }
    });


  }

  public void customAlertDialog(int i) {
    TextInputEditText etEventTitle, etEventDetails, etEventDate, etEventLocation;
    AlertDialog.Builder rewardDialog = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.custom_dialog, null);
    AlertDialog alertDialog = rewardDialog.create();

    etEventTitle=dialogView.findViewById(R.id.etAnnouncementTitle);
    etEventDetails=dialogView.findViewById(R.id.etAnnouncementDetails);
    etEventDate=dialogView.findViewById(R.id.etAnnouncementDate);
    etEventLocation=dialogView.findViewById(R.id.etEventLocation);

    etEventTitle.setText(eventModelArrayList.get(i).getEventTitle());
    etEventDetails.setText(eventModelArrayList.get(i).getEventDetails());
    etEventDate.setText(eventModelArrayList.get(i).getEventDate());
    etEventLocation.setText(eventModelArrayList.get(i).getEventLocation());

    float cornerRadius = 10.0f;
    ShapeDrawable roundedCornerDrawable = new ShapeDrawable();
    roundedCornerDrawable.setShape(new RoundRectShape(new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius}, null, null));
    roundedCornerDrawable.getPaint().setColor(Color.TRANSPARENT);
    alertDialog.getWindow().setBackgroundDrawable(roundedCornerDrawable);
    MapView mapView = dialogView.findViewById(R.id.map);
    mapView.onCreate(null);
    mapView.onResume();


    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        String[] parts = eventModelArrayList.get(i).getEventLocation().split(",");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);
        // İstenilen konumu haritada gösterin
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
      }
    });

    dialogView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        alertDialog.dismiss();
      }
    });
    alertDialog.setView(dialogView);
    alertDialog.show();
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
//            database.getReference().child("IAUNetwork/"+"events");
            database.getReference().child("EVENTLYDB").child("events");

    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
          System.out.println("eventSnapshot: " + eventSnapshot);
          EventModel eventModel = eventSnapshot.getValue(EventModel.class);
          eventModel.setEventKey(eventSnapshot.getKey());
          eventModelArrayList.add(eventModel);
          if (eventModel != null) {
            Log.i("MARDUK", eventModel.getEventTitle());
          }

        }

        progressDialog.dismiss();
        for (EventModel eventModels:eventModelArrayList){

          String coordinateString = eventModels.getEventLocation();
          System.out.println("coordinateString: " + coordinateString);
          String[] coordinateArray = coordinateString.split(",");
          double latitude = Double.parseDouble(coordinateArray[0]);
          double longitude = Double.parseDouble(coordinateArray[1]);
          LatLng latLng = new LatLng(latitude, longitude);
          Log.i("MAPDEDNEME",latLng.toString());
          mMap.addMarker(new MarkerOptions().position(latLng).title(eventModels.getEventTitle()));
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}