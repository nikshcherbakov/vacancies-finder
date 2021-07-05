package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.Location;
import com.nikshcherbakov.vacanciesfinder.utils.GoogleMapsInvalidApiKeyException;
import com.nikshcherbakov.vacanciesfinder.utils.TravelType;

import java.io.IOException;
import java.util.Date;

public interface IMapsService {
    Integer calculateTravelTimeInMins(Location origin, Location destination, TravelType travelBy, Date departure)
            throws IOException, GoogleMapsInvalidApiKeyException;

    Integer calculateTravelTimeInMins(Location origin, Location destination, TravelType travelBy)
            throws IOException, GoogleMapsInvalidApiKeyException;
}
