package com.evently.eventlyapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.evently.eventlyapp.Model.EventModel;
import com.evently.eventlyapp.R;

import java.util.ArrayList;


public class StudentEventAdapter extends RecyclerView.Adapter<StudentEventAdapter.StudentViewHolder> {

    LayoutInflater layoutInflater;

    Activity activity;
    ArrayList<EventModel> dataModelArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReference2;
    String userID;
    public StudentEventAdapter(Activity context, ArrayList<EventModel> dataModelArrayList,
                               FirebaseDatabase firebaseDatabase,String userID) {
        layoutInflater = LayoutInflater.from(context);
        this.activity = context;
        this.firebaseDatabase = firebaseDatabase;
        this.dataModelArrayList = dataModelArrayList;
        this.userID = userID;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = layoutInflater.inflate(R.layout.layout_student_event, viewGroup, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(view, i);

        return studentViewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder recyclerviewHolder, final int i) {
       recyclerviewHolder.tvTitle.setText(dataModelArrayList.get(i).getEventTitle());
       recyclerviewHolder.tvDetails.setText(dataModelArrayList.get(i).getEventDetails());
       recyclerviewHolder.tvDate.setText(dataModelArrayList.get(i).getEventDate());
       recyclerviewHolder.tvTotalNumber.setText(dataModelArrayList.get(i).getEventActor()+" " +
               "people attend");
        // Set image
        if (dataModelArrayList.get(i).getEventImageBitmap() != null) {
            recyclerviewHolder.imgEvent.setImageBitmap(dataModelArrayList.get(i).getEventImageBitmap());
        } else {
            recyclerviewHolder.imgEvent.setImageResource(R.drawable.ic_placeholder);
        }

        recyclerviewHolder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference=
                        firebaseDatabase.getReference().child("EVENTLYDB/events/"+dataModelArrayList.get(i).getEventKey()+"/eventActor");
                databaseReference2=
                        firebaseDatabase.getReference().child("EVENTLYDB/Users/"+userID+
                                "/myEvents/"+dataModelArrayList.get(i).getEventKey());


                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.i("MARDUK",
                                dataModelArrayList.get(i).getEventTitle()+"**"+snapshot.getValue(String.class));
                        if (dataModelArrayList.get(i).getEventTitle().equals(snapshot.getValue(String.class))){
                            Toast.makeText(activity, "You are already participating in this event!", Toast.LENGTH_SHORT).show();

                        }else {
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int value=snapshot.getValue(Integer.class)+1;
                                    databaseReference.setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            databaseReference2.setValue(dataModelArrayList.get(i).getEventTitle());
                                            dataModelArrayList.get(i).setEventActor(value);
                                            notifyDataSetChanged();
                                            Toast.makeText(activity, "Your registration for the event has been received!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }


    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDetails,tvDate,tvTotalNumber;
        Button btnJoin;
        ImageView imgEvent;
        public StudentViewHolder(@NonNull final View itemView, final int i) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalNumber = itemView.findViewById(R.id.tvTotalNumber);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            imgEvent = itemView.findViewById(R.id.imgEvent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customAlertDialog(i);
                }
            });
        }
    }
    public void customAlertDialog(int i) {
        TextInputEditText etEventTitle, etEventDetails, etEventDate, etEventLocation;
        AlertDialog.Builder rewardDialog = new AlertDialog.Builder(activity);
        ImageView etimgEvent;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        AlertDialog alertDialog = rewardDialog.create();

        etEventTitle=dialogView.findViewById(R.id.etAnnouncementTitle);
        etEventDetails=dialogView.findViewById(R.id.etAnnouncementDetails);
        etEventDate=dialogView.findViewById(R.id.etAnnouncementDate);
        etEventLocation=dialogView.findViewById(R.id.etEventLocation);
        etimgEvent=dialogView.findViewById(R.id.imgEvent);

        etEventTitle.setText(dataModelArrayList.get(i).getEventTitle());
        etEventDetails.setText(dataModelArrayList.get(i).getEventDetails());
        etEventDate.setText(dataModelArrayList.get(i).getEventDate());
        etEventLocation.setText(dataModelArrayList.get(i).getEventLocation());
        if (dataModelArrayList.get(i).getEventImageBitmap() != null) {etimgEvent.setImageBitmap(dataModelArrayList.get(i).getEventImageBitmap());
        } else {
            etimgEvent.setImageResource(R.drawable.ic_placeholder);
        }

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
                String[] parts = dataModelArrayList.get(i).getEventLocation().split(",");
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
}
