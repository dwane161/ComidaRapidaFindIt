package com.djdevelopment.comidarapidafindit.data;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Dwane Jimenez on 5/2/2017.
 */

public class Ratings implements Comparator<Ratings> {
    private String comment;
    private float rating;
    private String userName;
    private String photoURL;
    private String fecha;

    public Ratings(String comment, float rating, String userName, String photoURL, String fecha) {
        this.comment = comment;
        this.rating = rating;
        this.userName = userName;
        this.photoURL = photoURL;
        this.fecha = fecha;
    }


    public Ratings() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public int compare(Ratings ratings1, Ratings ratings2) {
        Date fecha1 = new Date(ratings1.getFecha());
        Date fecha2 = new Date(ratings2.getFecha());
        return fecha1.compareTo(fecha2);
    }
}
