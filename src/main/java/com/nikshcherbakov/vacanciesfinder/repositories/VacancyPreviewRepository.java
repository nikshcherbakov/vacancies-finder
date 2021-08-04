package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyPreviewRepository extends JpaRepository<VacancyPreview, Long> {
}
