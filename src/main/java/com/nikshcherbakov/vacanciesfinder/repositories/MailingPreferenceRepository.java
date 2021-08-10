package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailingPreferenceRepository extends JpaRepository<MailingPreference, Short> {
    Optional<MailingPreference> findMailingPreferenceByUseEmailAndUseTelegram(
            boolean useEmail, boolean useTelegram);
}
