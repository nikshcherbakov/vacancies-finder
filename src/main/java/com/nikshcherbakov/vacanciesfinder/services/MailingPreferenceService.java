package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import com.nikshcherbakov.vacanciesfinder.repositories.MailingPreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailingPreferenceService {

    private final MailingPreferenceRepository mailingPreferenceRepository;

    public MailingPreferenceService(MailingPreferenceRepository mailingPreferenceRepository) {
        this.mailingPreferenceRepository = mailingPreferenceRepository;
    }

    /**
     * Looks up for a mailing preference in the database
     * @param mailingPreference mailing preference which is searched in
     *                          the database
     * @return mailing preference from the database if one exists in the database, null otherwise
     */
    public MailingPreference findByMailingPreference(MailingPreference mailingPreference) {
        Optional<MailingPreference> mailingPreferenceFromDb =
                mailingPreferenceRepository.findMailingPreferenceByUseEmailAndUseTelegram(
                        mailingPreference.isUseEmail(), mailingPreference.isUseTelegram());

        return mailingPreferenceFromDb.orElse(null);
    }
}
