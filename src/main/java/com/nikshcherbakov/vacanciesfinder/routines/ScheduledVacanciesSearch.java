package com.nikshcherbakov.vacanciesfinder.routines;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.repositories.UserRepository;
import com.nikshcherbakov.vacanciesfinder.services.AsyncJobSearchService;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class ScheduledVacanciesSearch {

    private final AsyncJobSearchService asyncJobSearchService;
    private final UserService userService;

    public ScheduledVacanciesSearch(AsyncJobSearchService asyncJobSearchService, UserService userService) {
        this.asyncJobSearchService = asyncJobSearchService;
        this.userService = userService;
    }

    @Scheduled(fixedDelay = 120000) // TODO change to cron from app.props
//    @Transactional
    public void addNewVacanciesToAllActiveUsers() {
        List<User> users = userService.getAllActiveUsers();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Map<User, List<VacancyPreview>> userVacanciesMap = new HashMap<>();
        for (User user : users) {
            // Execute job search asynchronically on separate threads
            CompletableFuture<List<VacancyPreview>> futureVacancies = asyncJobSearchService.asyncSearchNextByUser(user);
            CompletableFuture<Void> future = futureVacancies.thenAccept(vacancies -> {
                userVacanciesMap.put(user, vacancies); // putting new found vacancies to the map
            });
            futures.add(future);
        }
        // Waiting for all futures to get done
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[users.size()])).join();

        // Adding found vacancies to the users and saving to the database
        userService.addFoundVacanciesAndSave(userVacanciesMap);

    }
}
