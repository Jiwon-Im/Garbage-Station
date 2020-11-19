package com.example.gs;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GsBin {

    public int id;
    public String size;
    public double lat;
    public double lng;
    public double capacity;
    public String url;

    public GsBin(){}

    public int getGsId(){
        return this.id;
    }

    public String getUrl(){
        return this.url;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public double getGsCapacity(){
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
}
