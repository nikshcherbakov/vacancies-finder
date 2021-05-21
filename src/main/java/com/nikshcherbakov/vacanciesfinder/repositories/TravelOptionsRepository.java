package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.Location;
import com.nikshcherbakov.vacanciesfinder.models.TravelOptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelOptionsRepository extends JpaRepository<TravelOptions, Long> {
    TravelOptions findTravelOptionsByLocationAndTravelTimeInMinutesAndTravelBy(
            Location location, int travelTimeInMins, String travelBy);
}
