package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.utils.GoogleMapsInvalidApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncJobSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VacanciesFinderApplication.class);

    private final IJobSearchService jobSearchService;

    public AsyncJobSearchService(IJobSearchService jobSearchService) {
        this.jobSearchService = jobSearchService;
    }


    /**
     * This method asynchronically finds vacancies for a user using
     * {@link IJobSearchService#searchNextByUser(User)} method on separate threads.
     * @param user user to find vacancies for
     * @return completable future of a list of vacancies if vacancies were found successfully;
     * null - if an error occurred while performing a search
     */
    @Async
    public CompletableFuture<List<VacancyPreview>> asyncSearchNextByUser(User user) {
        String username = user.getUsername();
        List<VacancyPreview> vacanciesFound = null;
        try {
            vacanciesFound = jobSearchService.searchNextByUser(user);
        } catch (IOException e) {
            logger.error(String.format("Cannot complete vacancies request for user %s: can't reach the job resource",
                    username));
        } catch (GoogleMapsInvalidApiKeyException e) {
            logger.error(String.format("Cannot complete vacancies request for user %s: Google Maps api key is invalid",
                    username));
        }
        if (vacanciesFound != null) {
            logger.info(String.format("New vacancies are found for user %s", username));
        } else {
            logger.error(String.format("No vacancies are found for user %s", username));
        }

        return CompletableFuture.completedFuture(vacanciesFound);
    }
}
