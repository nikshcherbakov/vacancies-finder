package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.MetroStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetroStationRepository extends JpaRepository<MetroStation, Short> {
}
