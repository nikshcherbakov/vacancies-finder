package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.Nullable;

import javax.persistence.*;
import java.util.List;

@Entity
public class Address {

    public static class Builder {
        private final Address newAddress;

        public Builder() {
            newAddress = new Address();
        }

        public Builder withCity(String city) {
            newAddress.city = city;
            return this;
        }

        public Builder withStreet(String street) {
            newAddress.street = street;
            return this;
        }

        public Builder withBuilding(String building) {
            newAddress.building = building;
            return this;
        }

        public Builder withDescription(String description) {
            newAddress.description = description;
            return this;
        }

        public Builder withLocation(Location location) {
            newAddress.location = location;
            return this;
        }

        public Builder withMetroStations(List<MetroStation> metroStations) {
            newAddress.metroStations = metroStations;
            return this;
        }

        public Address build() {
            return newAddress;
        }
    }

    @Id
    private Long id;

    @Nullable
    private String city;

    @Nullable
    private String street;

    @Nullable
    private String building;

    @Nullable
    private String description;

    @Transient
    @Nullable
    private Double lat;

    @Transient
    @Nullable
    private Double lng;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private Location location;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private MetroStation metro;

    @ManyToMany(cascade = CascadeType.ALL)
    @Nullable
    private List<MetroStation> metroStations;

    public Address() {
        this.location = new Location();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public MetroStation getMetro() {
        return metro;
    }

    public void setMetro(MetroStation metro) {
        this.metro = metro;
    }

    public List<MetroStation> getMetroStations() {
        return metroStations;
    }

    public void setMetroStations(List<MetroStation> metroStations) {
        this.metroStations = metroStations;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
        this.location.setLatitude(lat);
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
        this.location.setLongitude(lng);
    }
}
