package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.VacancyArea;
import com.nikshcherbakov.vacanciesfinder.repositories.VacancyAreaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VacancyAreaService {

    private final VacancyAreaRepository vacancyAreaRepository;

    public VacancyAreaService(VacancyAreaRepository vacancyAreaRepository) {
        this.vacancyAreaRepository = vacancyAreaRepository;
    }

    /**
     * Removes area from the database
     * @param area area to be removed
     */
    public void removeVacancyArea(VacancyArea area) {
        vacancyAreaRepository.delete(area);
    }

    /**
     * Looks up for an area in the database
     * @param id id by which a search is performed
     * @return vacancy area if one exists in the database, null otherwise
     */
    public VacancyArea findById(Short id) {
        Optional<VacancyArea> area = vacancyAreaRepository.findById(id);
        return area.orElse(null);
    }

    /**
     * Saves vacancy area to the database
     * @param area area that needs to be saved
     */
    public void save(VacancyArea area) {
        vacancyAreaRepository.save(area);
    }
}
