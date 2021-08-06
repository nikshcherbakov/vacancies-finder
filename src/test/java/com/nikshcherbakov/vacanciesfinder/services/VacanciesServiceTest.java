package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.utils.VacancyTableRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VacanciesServiceTest {

    @Value("${app.vacanciesrendering.perpage}")
    private Integer perPage;

    @Autowired
    private VacanciesService vacanciesService;

    @Test
    void getPageWorksCorrectly() {
        // Case 1
        User user1 = new User("someUsername", "password");

        // Generating simple list of vacancies
        for (long i = 1; i <= perPage * 2 + 5; i++) {
            VacancyPreview vacancy = new VacancyPreview.Builder()
                    .withId(i)
                    .withName("Some vacancy")
                    .withEmployer(new VacancyEmployer(0L, "Some employer"))
                    .withPublishedAt(new Date())
                    .withUsers(Collections.singleton(user1))
                    .withVacancySnippet(new VacancySnippet("requirement", "responsibility"))
                    .withArea(new VacancyArea((short) 0, "Some area"))
                    .build();

            user1.addVacancy(vacancy);
        }

        List<VacancyTableRow> page_case1 = vacanciesService.getPage(user1.getVacancies(), 3);
        assertEquals(5, page_case1.size());
        assertEquals(perPage * 2 + 1, page_case1.get(0).getVacancyId());

        // Case 2
        User user2 = new User("someUsername", "password");

        // Generating simple list of vacancies
        for (long i = 1; i <= perPage / 2; i++) {
            VacancyPreview vacancy = new VacancyPreview.Builder()
                    .withId(i)
                    .withName("Some vacancy")
                    .withEmployer(new VacancyEmployer(0L, "Some employer"))
                    .withPublishedAt(new Date())
                    .withUsers(Collections.singleton(user1))
                    .withVacancySnippet(new VacancySnippet("requirement", "responsibility"))
                    .withArea(new VacancyArea((short) 0, "Some area"))
                    .build();

            user1.addVacancy(vacancy);
        }

        List<VacancyTableRow> pageCase2 = vacanciesService.getPage(user2.getVacancies(), 2);
        assertNull(pageCase2);

        // Case 3
        List<VacancyTableRow> pageCase3 = vacanciesService.getPage(user2.getVacancies(), 0);
        assertNull(pageCase3);
    }

    @Test
    void itReturnsCorrectNumberOfPages() {
        // Case 1
        User user1 = new User("someUsername", "password");

        // Generating simple list of vacancies
        for (long i = 1; i <= perPage * 2 + 5; i++) {
            VacancyPreview vacancy = new VacancyPreview.Builder()
                    .withId(i)
                    .withName("Some vacancy")
                    .withEmployer(new VacancyEmployer(0L, "Some employer"))
                    .withPublishedAt(new Date())
                    .withUsers(Collections.singleton(user1))
                    .withVacancySnippet(new VacancySnippet("requirement", "responsibility"))
                    .withArea(new VacancyArea((short) 0, "Some area"))
                    .build();

            user1.addVacancy(vacancy);
        }

        int user1Pages = vacanciesService.pages(user1.getVacancies());
        assertEquals(3, user1Pages);

        // Case 2
        User user2 = new User("username", "password");
        user2.setVacancies(null);

        int user2Pages = vacanciesService.pages(user2.getVacancies());
        assertEquals(0, user2Pages);

    }

}