package edu.depaul.csc472.kuraszj_fieldme;

import android.location.Location;

import java.util.Comparator;

/**
 * Created by jasekurasz on 11/13/14.
 */
public class Park {

    public String name;
    public String number;
    public String facility;
    public String latitude;
    public String longitude;
    public String sport;
    public Location location = new Location("dummy");
    public float distToLoc;

    public Park(){

    }

    public String toString() { return name; }

    //Getters
    public String getName() { return this.name; }

    public String getSport() { return this.sport; }

    public String getNumber() { return this.number; }

    public String getFacility() {
        return this.facility;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public Location getLocation() { return this.location; }

    public float getDistToLoc() { return this.distToLoc; }

    //Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setSport(String sport) { this.sport = sport; }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) { this.longitude = longitude; }

    public void setLocation(String latitude, String longitude) {
        this.location.setLatitude(Double.parseDouble(latitude));
        this.location.setLongitude(Double.parseDouble(longitude));
    }

    public void setDistToLoc(float f) {
        this.distToLoc = f;
    }
    
}
