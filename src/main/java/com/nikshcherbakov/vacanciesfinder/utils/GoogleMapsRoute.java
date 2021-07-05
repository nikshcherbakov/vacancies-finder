package com.nikshcherbakov.vacanciesfinder.utils;

import com.sun.istack.NotNull;

import java.util.List;

public class GoogleMapsRoute {

    @NotNull
    private List<GoogleMapsLeg> legs;

    public GoogleMapsRoute(@NotNull List<GoogleMapsLeg> legs) {
        this.legs = legs;
    }

    public GoogleMapsRoute() {
    }

    public List<GoogleMapsLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<GoogleMapsLeg> legs) {
        this.legs = legs;
    }
}
