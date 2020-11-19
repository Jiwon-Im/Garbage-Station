package com.example.gs;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GsBin {

    public double capacity;
    public int id;
    public double lat;
    public double lng;
    public String size;
    public String url;

    public GsBin(){}

    public int getGsId(){
        return id;
    }

    public double getGsCapacity(){
        return capacity;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
}
