package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.id, u.enabled, u.last_job_request_date, u.password, u.search_filters, u.username, " +
            "u.mailing_preference_id, u.salary_id, u.telegram_settings_id, u.travel_options_id, u.registration_date " +
            "FROM users AS u INNER JOIN telegram_settings AS ts ON u.telegram_settings_id = ts.id " +
            "WHERE telegram = ?1 AND ts.chat_id IS NOT null",
            nativeQuery = true)
    Optional<User> findByActiveTelegram(String telegram);

    @Query(value = "SELECT u.id, u.enabled, u.last_job_request_date, u.password, u.search_filters, u.username, " +
            "u.mailing_preference_id, u.salary_id, u.telegram_settings_id, u.travel_options_id, u.registration_date " +
            "FROM users AS u INNER JOIN telegram_settings AS ts ON u.telegram_settings_id = ts.id WHERE telegram = ?1",
            nativeQuery = true)
    List<User> findByTelegram(String telegram);

    @Query(value = "SELECT u.id, u.enabled, u.last_job_request_date, u.password, u.search_filters, u.username, " +
            "u.mailing_preference_id, u.salary_id, u.telegram_settings_id, u.travel_options_id, u.registration_date " +
            "FROM users AS u INNER JOIN telegram_settings AS ts ON u.telegram_settings_id = ts.id WHERE chat_id = ?1",
            nativeQuery = true)
    Optional<User> findByChatId(long chatId);
}
