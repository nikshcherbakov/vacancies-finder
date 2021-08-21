package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.VacancyEmployer;
import com.nikshcherbakov.vacanciesfinder.repositories.VacancyEmployerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VacancyEmployerService {

    private final VacancyEmployerRepository vacancyEmployerRepository;

    public VacancyEmployerService(VacancyEmployerRepository vacancyEmployerRepository) {
        this.vacancyEmployerRepository = vacancyEmployerRepository;
    }

    /**
     * Removes vacancy employer from the database
     * @param employer employer that needs to be deleted
     */
    public void removeEmployer(VacancyEmployer employer) {
        vacancyEmployerRepository.delete(employer);
    }

    /**
     * Looks up for a vacancy employer in the database
     * @param id id by which a serach is performed
     * @return vacancy employer if one exists in the database,
     * null otherwise
     */
    public VacancyEmployer findById(Long id) {
        Optional<VacancyEmployer> vacancyEmployer = vacancyEmployerRepository.findById(id);
        return vacancyEmployer.orElse(null);
    }

    /**
     * Saves a given employer to the database
     * @param vacancyEmployer employer that needs to be saved
     */
    public void save(VacancyEmployer vacancyEmployer) {
        vacancyEmployerRepository.save(vacancyEmployer);
    }
}
