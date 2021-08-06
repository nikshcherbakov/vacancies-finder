package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.VacancyArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyAreaRepository extends JpaRepository<VacancyArea, Short> {
}
