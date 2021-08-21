package com.nikshcherbakov.vacanciesfinder.utils;

@SuppressWarnings("unused")
public abstract class RouteRequest {
    private String status;
    private Integer travelTime;

    public RouteRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }
}
