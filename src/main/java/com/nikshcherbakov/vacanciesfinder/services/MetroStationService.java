package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.MetroStation;
import com.nikshcherbakov.vacanciesfinder.repositories.MetroStationRepository;
import org.springframework.stereotype.Service;

@Service
public class MetroStationService {

    private final MetroStationRepository metroStationRepository;

    public MetroStationService(MetroStationRepository metroStationRepository) {
        this.metroStationRepository = metroStationRepository;
    }

    /**
     * Saves metro station to the database and returns object with id
     * @param station metro station that needs to be saved
     * @return metro station with id
     */
    public MetroStation save(MetroStation station) {
        return metroStationRepository.save(station);
    }

    /**
     * Removes metro station from the database
     * @param station metro station to be removed
     */
    public void removeStation(MetroStation station) {
        metroStationRepository.delete(station);
    }
}
