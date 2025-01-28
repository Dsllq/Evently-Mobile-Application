package com.evently.eventlyapp.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.evently.eventlyapp.R;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View infoWindowView;

    public CustomInfoWindowAdapter(Context context) {
        infoWindowView = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView titleTextView = infoWindowView.findViewById(R.id.titleTextView);
        titleTextView.setText(marker.getTitle());
        return infoWindowView;
    }
}
