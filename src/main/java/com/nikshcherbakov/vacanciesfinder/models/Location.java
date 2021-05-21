package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float longitude;
    private float latitude;

    @OneToMany(mappedBy = "location")
    Set<TravelOptions> travelOptions;

    public Location() {
    }

    public Location(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<TravelOptions> getTravelOptions() {
        return travelOptions;
    }

    public void setTravelOptions(Set<TravelOptions> travelOptions) {
        this.travelOptions = travelOptions;
    }

}
