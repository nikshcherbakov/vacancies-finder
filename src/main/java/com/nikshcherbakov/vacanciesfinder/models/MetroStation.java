package com.nikshcherbakov.vacanciesfinder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.Nullable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
public class MetroStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @JsonProperty("station_id")
    private String stationId;

    @JsonProperty("station_name")
    private String stationName;

    @JsonProperty("line_id")
    private Integer lineId;

    @JsonProperty("line_name")
    private String lineName;

    @Transient
    @Nullable
    private Double lat;

    @Transient
    @Nullable
    private Double lng;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Nullable
    private Location stationLocation;

    @OneToMany(mappedBy = "metro", fetch = FetchType.EAGER, orphanRemoval = true)
    @Nullable
    private Set<Address> addresses;

    public MetroStation() {
        this.stationLocation = new Location();
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public Location getStationLocation() {
        return stationLocation;
    }

    public void setStationLocation(Location stationLocation) {
        this.stationLocation = stationLocation;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
        this.stationLocation.setLatitude(lat);
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
        this.stationLocation.setLongitude(lng);
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        if (addresses == null) {
            addresses = new HashSet<>();
        }
        addresses.add(address);
    }
}
