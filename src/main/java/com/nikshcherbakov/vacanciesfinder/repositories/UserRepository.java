package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.id, u.enabled, u.last_job_request_date, u.password, u.registration_date, u.search_filters, " +
            "u.username, u.mailing_preference_id " +
            "FROM users AS u INNER JOIN telegram_settings AS ts ON u.id = ts.user_id " +
            "WHERE telegram = ?1 AND ts.chat_id IS NOT null",
            nativeQuery = true)
    Optional<User> findByActiveTelegram(String telegram);

    @Query(value = "SELECT u.id, u.enabled, u.last_job_request_date, u.password, u.registration_date, u.search_filters, " +
            "u.username, u.mailing_preference_id " +
            "FROM users AS u INNER JOIN telegram_settings AS ts ON u.id = ts.user_id WHERE telegram = ?1",
            nativeQuery = true)
    List<User> findByTelegram(String telegram);

    @Query(value = "SELECT u.id, u.enabled, u.last_job_request_date, u.password, u.registration_date, u.search_filters, " +
            "u.username, u.mailing_preference_id " +
            "FROM users AS u INNER JOIN telegram_settings AS ts ON u.id = ts.user_id WHERE chat_id = ?1",
            nativeQuery = true)
    Optional<User> findByChatId(long chatId);
}
