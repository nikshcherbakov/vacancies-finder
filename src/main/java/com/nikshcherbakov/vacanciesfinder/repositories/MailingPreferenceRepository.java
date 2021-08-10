package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// TODO GENERAL заменить тип id в MailingPreference на short
public interface MailingPreferenceRepository extends JpaRepository<MailingPreference, Long> {
    Optional<MailingPreference> findMailingPreferenceByUseEmailAndUseTelegram(
            boolean useEmail, boolean useTelegram);
}
