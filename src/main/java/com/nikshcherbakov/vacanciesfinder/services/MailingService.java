package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import com.nikshcherbakov.vacanciesfinder.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MailingService {

    private static final Logger logger = LoggerFactory.getLogger(VacanciesFinderApplication.class);

    private final TelegramService telegramService;
    private final EmailService emailService;

    public MailingService(TelegramService telegramService, EmailService emailService) {
        this.telegramService = telegramService;
        this.emailService = emailService;
    }

    /**
     * Sends found vacancies to all users
     * @implNote Note that the method runs mailing procedure for all
     * users on separate threads, so make sure your task executor's queue
     * size is large enough to run mailing asynchronically.
     * @param users collection of users to which messages will be sent
     */
    public void sendFoundVacanciesToAllUsers(Collection<User> users) {
        // Sending found vacancies to all users
        for (User user : users) {
            asyncSendFoundVacanciesByMailingPreference(user);
        }
    }

    /**
     * Sends found vacancies to a user by one's mailing preference
     * asynchronically
     * @param to user to which list of vacancies will be sent
     */
    @Async
    public void asyncSendFoundVacanciesByMailingPreference(User to) {
        MailingPreference userMailingPreference = to.getMailingPreference();
        if (userMailingPreference != null) {
            boolean useTelegram = userMailingPreference.isUseTelegram();
            boolean useEmail = userMailingPreference.isUseEmail();

            if (useTelegram) {
                boolean sentSuccessfullyByTelegram = telegramService.sendFoundVacancies(to);
                logger.info(String.format(sentSuccessfullyByTelegram ?
                                "Found vacancies are sent to a user %s by telegram" :
                                "Found vacancies are not sent to a user %s by telegram",
                        to.getUsername()));
            }

            if (useEmail) {
                boolean sentSuccessfullyByEmail = emailService.sendFoundVacancies(to);
                logger.info(String.format(sentSuccessfullyByEmail ?
                                "Found vacancies are sent to a user %s by email" :
                                "Found vacancies are not sent to a user %s by email",
                        to.getUsername()));
            }
        }
    }

}
