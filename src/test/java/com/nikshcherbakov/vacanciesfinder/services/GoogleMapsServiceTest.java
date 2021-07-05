package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.Location;
import com.nikshcherbakov.vacanciesfinder.utils.GoogleMapsInvalidApiKeyException;
import com.nikshcherbakov.vacanciesfinder.utils.TravelType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GoogleMapsServiceTest {

    @Autowired
    GoogleMapsService googleMapsService;

    @Test
    void travelTimeIsCalculated() throws IOException, GoogleMapsInvalidApiKeyException {
        // Case 1 - Travelling by car
        Location origin1 = new Location(55.74845688866654, 37.53842385932942);
        Location destination1 = new Location(55.74815029874091, 37.70095204877878);
        Integer travelTime1 = googleMapsService.calculateTravelTimeInMins(origin1, destination1, TravelType.CAR);
        assertTrue(travelTime1 > 0);

        // Case 2 - Travelling by walking
        Location origin2 = new Location(55.74845688866654, 37.53842385932942);
        Location destination2 = new Location(55.74815029874091, 37.70095204877878);
        Integer travelTime2 = googleMapsService.calculateTravelTimeInMins(origin2, destination2, TravelType.WALKING);
        assertTrue(travelTime2 > 0);

        // Case 3 - Travelling by public transport
        Location origin3 = new Location(55.74845688866654, 37.53842385932942);
        Location destination3 = new Location(55.74815029874091, 37.70095204877878);
        Integer travelTime3 = googleMapsService.calculateTravelTimeInMins(origin3, destination3, TravelType.PUBLIC_TRANSPORT);
        assertTrue(travelTime3 > 0);
    }

    @Test
    void nonExistingRouteIsHandledCorrectly() throws IOException, GoogleMapsInvalidApiKeyException {
        // Try to build a route from Russia to the US by driving
        Location locationInRussia = new Location(55.74845688866654, 37.53842385932942);
        Location locationInUS = new Location(42.341536328921165, -71.10232147619853);
        Integer travelTime = googleMapsService.calculateTravelTimeInMins(locationInRussia, locationInUS, TravelType.CAR);
        assertEquals(-1, travelTime);
    }

    @Test
    void itReturnsCorrectTravelTimes() throws IOException, GoogleMapsInvalidApiKeyException {
        Location origin = new Location(54.5503322870497, 36.2972277553664);
        Location destination = new Location(54.53203438969385, 36.26749599769481);
        Integer travelTime = googleMapsService.calculateTravelTimeInMins(origin, destination, TravelType.CAR);
        assertTrue(travelTime > 5 && travelTime < 90);
    }
}