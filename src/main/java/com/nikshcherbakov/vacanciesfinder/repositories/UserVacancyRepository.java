package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.UserVacancy;
import com.nikshcherbakov.vacanciesfinder.models.UserVacancyId;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserVacancyRepository extends JpaRepository<UserVacancy, UserVacancyId> {

    List<UserVacancy> findUserVacancyByUserAndIsFavorite(User user, Boolean isFavorite);
    Optional<UserVacancy> findUserVacancyByUserAndVacancy(User user, VacancyPreview vacancy);

    @Query(value = "select user_id, vacancy_id, is_favorite from user_vacancy uv join vacancy_preview vp " +
            "on uv.vacancy_id = vp.id where user_id = ? and is_favorite = false order by published_at asc",
            nativeQuery = true)
    List<UserVacancy> findNonFavoriteVacanciesOrderedByPublishingTime(Long userId);
}
