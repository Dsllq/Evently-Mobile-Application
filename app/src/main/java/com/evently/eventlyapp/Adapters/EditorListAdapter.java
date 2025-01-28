package com.evently.eventlyapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.evently.eventlyapp.Model.MainUserModel;
import com.evently.eventlyapp.R;

import java.util.ArrayList;

public class EditorListAdapter extends RecyclerView.Adapter<EditorListAdapter.EditorListHolder> {

    LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<MainUserModel> dataModelArrayList;

    public EditorListAdapter(Activity context,
                             ArrayList<MainUserModel> dataModelArrayList){
        layoutInflater = LayoutInflater.from(context);
        this.activity = context;
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public EditorListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = layoutInflater.inflate(R.layout.layout_editor_list, viewGroup, false);
        EditorListHolder editorListHolder = new EditorListHolder(view, i);

        return editorListHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditorListHolder holder, int position) {
        MainUserModel data = dataModelArrayList.get(position);

        holder.tvName.setText(data.getName());
        holder.tvMail.setText(data.getMail());

        if (data.isEnableEditor()){
            holder.lllayout.setVisibility(View.GONE);
        }

        holder.btnConfirm.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyCustomAlertDialogTheme);
            builder.setTitle("Confirm Editor");
            builder.setMessage("Are you sure you want to confirm this editor?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Toast.makeText(activity, "Confirmed!", Toast.LENGTH_SHORT).show();
                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                DatabaseReference  databaseReference =
                        firebaseDatabase.getReference().child("EVENTLYDB/"+"Users" +
                                "/"+ data.getUserUID()+"/UID" +
                                "/enableEditor");
                databaseReference.setValue(true).addOnCompleteListener(task -> {
                    data.setEnableEditor(true);
                    notifyDataSetChanged();
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        holder.btnDecline.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyCustomAlertDialogTheme);
            builder.setTitle("Decline Editor");
            builder.setMessage("Are you sure you want to decline this editor?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Toast.makeText(activity, "Declined!", Toast.LENGTH_SHORT).show();
                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                DatabaseReference  databaseReference =
                        firebaseDatabase.getReference().child("EVENTLYDB/"+"Users" +
                                "/"+data.getUserUID());


                databaseReference.removeValue().addOnCompleteListener(task -> {
                    dataModelArrayList.remove(position);
                    notifyDataSetChanged();
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    /*For same return item*/
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    static class EditorListHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvMail;
        Button btnConfirm,btnDecline;
        LinearLayout lllayout;
        public EditorListHolder(@NonNull final View itemView, final int i) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMail = itemView.findViewById(R.id.tvMail);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            lllayout = itemView.findViewById(R.id.lllayout);
        }
    }
}