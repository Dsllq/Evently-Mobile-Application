package com.evently.eventlyapp.Model;

import android.graphics.Bitmap;

public class EventModel {

    String eventTitle;
    String eventDetails;
    String eventDate;
    String eventorUID;
    String eventKey;

   String imageBase64;
    int eventActor;
    String eventLocation;
     Bitmap eventImageBitmap;
    public EventModel() {
    }

    public EventModel(String imageBase64,String eventTitle, String eventDetails, String eventDate, String eventorUID,
                      int eventActor, String eventLocation ) {
        this.imageBase64=imageBase64;
        this.eventTitle = eventTitle;
        this.eventDetails = eventDetails;
        this.eventDate = eventDate;
        this.eventActor = eventActor;
        this.eventLocation = eventLocation;
        this.eventorUID = eventorUID;

    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventorUID(String eventorUID) {
        this.eventorUID = eventorUID;
    }


    public void setEventImageBitmap(Bitmap eventImageBitmap) {
        this.eventImageBitmap = eventImageBitmap;
    }
    public void setEventActor(int eventActor) {
        this.eventActor = eventActor;
    }
    public void setImageUrl(String imageBase64){this.imageBase64=imageBase64;}
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventTitle() {
        return eventTitle;
    }
    public String getImageUrl(){return imageBase64;}

    public String getEventorUID() {
        return eventorUID;
    }
    public Bitmap getEventImageBitmap() {
        return eventImageBitmap;
    }
    public String getEventDetails() {
        return eventDetails;
    }

    public String getEventDate() {
        return eventDate;
    }

    public int getEventActor() {
        return eventActor;
    }

    public String getEventLocation() {
        return eventLocation;
    }
}
