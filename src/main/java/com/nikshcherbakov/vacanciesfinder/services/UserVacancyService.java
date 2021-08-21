package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.UserVacancy;
import com.nikshcherbakov.vacanciesfinder.models.UserVacancyId;
import com.nikshcherbakov.vacanciesfinder.repositories.UserVacancyRepository;
import org.springframework.stereotype.Service;

@Service
public class UserVacancyService {

    private final UserVacancyRepository userVacancyRepository;

    public UserVacancyService(UserVacancyRepository userVacancyRepository) {
        this.userVacancyRepository = userVacancyRepository;
    }

    /**
     * Saves userVacancy to the database
     * @param userVacancy userVacancy that needs to be saved
     */
    public void save(UserVacancy userVacancy) {
        userVacancyRepository.save(userVacancy);
    }

    /**
     * Checks if a given user vacancy exists in the database
     * @param userVacancyId embedded id by which a search is performed
     * @return true if there is such a userVacancy in the database,
     * false otherwise
     */
    public boolean existsById(UserVacancyId userVacancyId) {
        return userVacancyRepository.existsById(userVacancyId);
    }
}
