package com.djdevelopment.comidarapidafindit.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by User on 3/25/2017.
 */

public class Restaurants {

    private String restName;
    private ArrayList<String> menu;
    private String latLong;
    private String creditCards;
    private String telephones;
    private boolean validated;
    private double rating;

    public Restaurants() {
    }

    public Restaurants(String restName, ArrayList<String> menu, String latLong, String creditCards, String telephones, boolean validated, double rating) {
        this.restName = restName;
        this.menu = menu;
        this.latLong = latLong;
        this.creditCards = creditCards;
        this.telephones = telephones;
        this.validated = validated;
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<String> menu) {
        this.menu = menu;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(String creditCards) {
        this.creditCards = creditCards;
    }

    public String getTelephones() {
        return telephones;
    }

    public void setTelephones(String telephones) {
        this.telephones = telephones;
    }
}
