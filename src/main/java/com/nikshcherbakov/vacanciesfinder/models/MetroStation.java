package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class MetroStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String stationId;

    @NotNull
    private String stationName;

    @NotNull
    private Integer lineId;

    @NotNull
    private String lineName;

    @Transient
    @Nullable
    private Double lat;

    @Transient
    @Nullable
    private Double lng;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private Location stationLocation;

    public MetroStation() {
        stationLocation = new Location();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
