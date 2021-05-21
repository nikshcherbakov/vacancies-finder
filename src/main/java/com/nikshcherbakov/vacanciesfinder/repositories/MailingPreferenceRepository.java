package com.nikshcherbakov.vacanciesfinder.repositories;

import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailingPreferenceRepository extends JpaRepository<MailingPreference, Long> {
    MailingPreference findMailingPreferenceByUseEmailAndUseTelegram(
            boolean useEmail, boolean useTelegram);
}
