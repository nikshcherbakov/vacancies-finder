package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.Location;
import com.nikshcherbakov.vacanciesfinder.utils.*;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsService implements IMapsService {

    @Value("${app.google.api.urls.directions}")
    private String googleDirectionsApiUrl;

    @Value("${app.google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GoogleMapsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Integer calculateTravelTimeInMins(Location origin, Location destination,
                                             TravelType travelBy, @Nullable Date departure)
            throws GoogleMapsInvalidApiKeyException {
        try {
            GoogleRouteRequest routeRequest = requestRoute(origin, destination, travelBy, departure);
            if (routeRequest.getStatus().equals("REQUEST_DENIED")) throw new GoogleMapsInvalidApiKeyException();
            return routeRequest.getTravelTime();
        } catch (HTTPEmptyGetParameterException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer calculateTravelTimeInMins(Location origin, Location destination, TravelType travelBy) throws GoogleMapsInvalidApiKeyException {
        return calculateTravelTimeInMins(origin, destination, travelBy, null);
    }

    private GoogleRouteRequest requestRoute(Location origin, Location destination, TravelType travelBy, Date departure)
            throws HTTPEmptyGetParameterException {
        Map<String, String> params = new HashMap<>();
        params.put("origin", String.format("%s,%s", origin.getLatitude(), origin.getLongitude()));
        params.put("destination", String.format("%s,%s", destination.getLatitude(), destination.getLongitude()));
        switch (travelBy) {
            case CAR:
                params.put("mode", "driving");
                break;
            case WALKING:
                params.put("mode", "walking");
                break;
            case PUBLIC_TRANSPORT:
                params.put("mode", "transit");
                break;
        }
        params.put("key", apiKey);
        if (departure != null) {
            params.put("departure_time", String.valueOf(departure.getTime() / 1000));
        }

        String urlParams = HTTPParameterStringBuilder.getParamsString(params);
        return restTemplate.getForObject(googleDirectionsApiUrl + urlParams, GoogleRouteRequest.class);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
