package com.nikshcherbakov.vacanciesfinder.utils;

public abstract class RouteRequest {
    private String status;
    private Integer travelTime;

    public RouteRequest() {
    }

    public RouteRequest(String status, Integer travelTime) {
        this.status = status;
        this.travelTime = travelTime;
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
