package com.evently.eventlyapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evently.eventlyapp.Activities.EditorEventEditActivity;
import com.evently.eventlyapp.Model.EventModel;
import com.evently.eventlyapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class EditorEventAdapter extends RecyclerView.Adapter<EditorEventAdapter.RecyclerviewHolder> {

    LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<EventModel> dataModelArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public EditorEventAdapter(Activity context, ArrayList<EventModel> dataModelArrayList, FirebaseDatabase firebaseDatabase) {
        layoutInflater = LayoutInflater.from(context);
        this.activity = context;
        this.firebaseDatabase = firebaseDatabase;
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = layoutInflater.inflate(R.layout.layout_editor_event, viewGroup, false);
        RecyclerviewHolder recyclerviewHolder = new RecyclerviewHolder(view, i);

        return recyclerviewHolder;
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
    public void onBindViewHolder(@NonNull RecyclerviewHolder recyclerviewHolder, int position) {
        int adapterPosition = recyclerviewHolder.getAdapterPosition();

        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }

        EventModel currentItem = dataModelArrayList.get(adapterPosition);

        recyclerviewHolder.tvTitle.setText(currentItem.getEventTitle());
        recyclerviewHolder.tvDetails.setText(currentItem.getEventDetails());
        recyclerviewHolder.tvDate.setText(currentItem.getEventDate());
        recyclerviewHolder.tvTotalNumber.setText(currentItem.getEventActor() + " people attend");

        if (currentItem.getEventImageBitmap() != null) {
            recyclerviewHolder.imgEvent.setImageBitmap(currentItem.getEventImageBitmap());
        } else {
            recyclerviewHolder.imgEvent.setImageResource(R.drawable.ic_placeholder);
        }

        recyclerviewHolder.imgDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyCustomAlertDialogTheme);
            builder.setTitle("Delete Item");
            builder.setMessage("Are you sure you want to delete this item?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                Toast.makeText(activity, "Event Deleted!", Toast.LENGTH_SHORT).show();

                DatabaseReference databaseReference = firebaseDatabase
                        .getReference()
                        .child("EVENTLYDB/events/" + currentItem.getEventKey());

                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataModelArrayList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    } else {
                        Toast.makeText(activity, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


        recyclerviewHolder.imgEdit.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EditorEventEditActivity.class);
            intent.putExtra("getEventKey", currentItem.getEventKey());
            intent.putExtra("getEventActor", String.valueOf(currentItem.getEventActor()));
            intent.putExtra("getEventDate", currentItem.getEventDate());
            intent.putExtra("getEventDetails", currentItem.getEventDetails());
            intent.putExtra("getEventTitle", currentItem.getEventTitle());
            intent.putExtra("getEventorUID", currentItem.getEventorUID());
            intent.putExtra("getEventLocation", currentItem.getEventLocation());
            intent.putExtra("getEventimageBase64",currentItem.getImageUrl());

            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class RecyclerviewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDetails,tvDate,tvTotalNumber;
        ImageView imgEdit,imgDelete,imgEvent;
        public RecyclerviewHolder(@NonNull final View itemView, final int i) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalNumber = itemView.findViewById(R.id.tvTotalNumber);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgEvent = itemView.findViewById(R.id.imgEvent);
        }
    }
}
