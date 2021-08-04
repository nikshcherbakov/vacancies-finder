package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.VacancyEmployer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyEmployerRepository extends JpaRepository<VacancyEmployer, Long> {

}
