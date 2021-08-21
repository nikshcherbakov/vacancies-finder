package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.utils.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HeadHunterServiceTest {

    @Autowired
    private HeadHunterService headHunterService;

    @Autowired
    private GoogleMapsService googleMapsService;

    @Test
    void simpleJobRequestReturnsSomething() throws HTTPEmptyGetParameterException {
        Map<String, String> params = new HashMap<>();
        params.put("text", "Менеджер");
        params.put("per_page", "100");

        JobsRequest jobsRequest = headHunterService.requestVacanciesByUrlParams(params);
        List<VacancyPreview> vacancies = jobsRequest.getItems();
        assertTrue(vacancies.size() > 0);
        assertTrue(vacancies.get(0).getId() != 0);
        assertNotNull(vacancies.get(0).getName());
    }

    @Test
    void testRequestReturnsVacanciesUsingDifferentLanguages() throws HTTPEmptyGetParameterException {

        // English
        Map<String, String> paramsEng = new HashMap<>();
        paramsEng.put("text", "Manager");
        paramsEng.put("per_page", "100");

        JobsRequest jobsRequestEng = headHunterService.requestVacanciesByUrlParams(paramsEng);
        List<VacancyPreview> vacanciesEng = jobsRequestEng.getItems();
        assertTrue(vacanciesEng.size() > 0);
        assertTrue(vacanciesEng.get(0).getId() != 0);
        assertNotNull(vacanciesEng.get(0).getName());

        // Russian
        Map<String, String> paramsRus = new HashMap<>();
        paramsRus.put("text", "Менеджер");
        paramsRus.put("per_page", "100");

        JobsRequest jobsRequestRus = headHunterService.requestVacanciesByUrlParams(paramsRus);
        List<VacancyPreview> vacanciesRus= jobsRequestRus.getItems();
        assertTrue(vacanciesRus.size() > 0);
        assertTrue(vacanciesRus.get(0).getId() != 0);
        assertNotNull(vacanciesRus.get(0).getName());
    }

    @Test
    void searchVacanciesByUserSinceDateWorksFine() throws GoogleMapsInvalidApiKeyException {
        // Case 1 - Just a simple user without location
        User user1 = new User("simpleUser", "password");
        user1.setSearchFilters("Junior;Java;Backend");

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayDate = calendar.getTime();

        List<VacancyPreview> vacanciesFound1 = headHunterService.searchVacanciesByUserSinceDate(user1, yesterdayDate);
        assertTrue(vacanciesFound1.size() > 0);
        assertNotNull(vacanciesFound1.get(0).getName());

        // Case 2 - User specified filters and salary
        User user2 = new User("simpleUser", "password");
        user2.setSearchFilters("Junior;Java;Backend");
        user2.setSalary(new Salary(user2, 80000, "RUB"));

        calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date dateWeekAgo = calendar.getTime();

        List<VacancyPreview> vacanciesFound2 = headHunterService.searchVacanciesByUserSinceDate(user2, dateWeekAgo);
        assertTrue(vacanciesFound2.size() > 0);
        assertNotNull(vacanciesFound2.get(0).getName());
        // Checking if all vacancies in RUR satisfy user's wished salary
        for (VacancyPreview vacancyFound : vacanciesFound2) {
            assertNotNull(vacancyFound.getSalary());
            if (vacancyFound.getSalary().getCurrency().equals("RUR")) {
                Integer userSalary = user2.getSalary().getValue();
                Integer vacancyFromSalary = vacancyFound.getSalary().getFrom();
                Integer vacancyToSalary = vacancyFound.getSalary().getTo();
                assertTrue(vacancyFromSalary != null || vacancyToSalary != null);
                if (vacancyToSalary != null) {
                    assertTrue(userSalary <= vacancyToSalary);
                }
            }
        }

        // Case 3 - User specified filters and location
        User user3 = new User("userWithLocation", "password");
        user3.setSearchFilters("Junior;Java;Backend");
        user3.setSalary(new Salary(user3, 80000, "RUB"));
        user3.setTravelOptions(new TravelOptions(user3, new Location(55.59225991787999, 37.598054613086425),
                90, "car"));

        List<VacancyPreview> vacanciesFound3 = headHunterService.searchVacanciesByUserSinceDate(user3, yesterdayDate);
        if (vacanciesFound3.size() > 0) {
            assertNotNull(vacanciesFound3.get(0).getName());

            // Checking if each of the vacancies found has got a non-null address
            for (VacancyPreview vacancyFound : vacanciesFound3) {
                assertNotNull(vacancyFound.getAddress());
            }

            Integer travelTimeToWorkInMins = googleMapsService.calculateTravelTimeInMins(
                    user3.getTravelOptions().getLocation(),
                    vacanciesFound3.get(0).getAddress().getLocation(), TravelType.CAR);
            assertNotEquals(-1, travelTimeToWorkInMins);
            assertTrue(user3.getTravelOptions().getTravelTimeInMinutes() >= travelTimeToWorkInMins);
        }

        // Case 4 - User did not specify search filters
        User user4 = new User("userWithoutSearchFilters", "password");
        user4.setSearchFilters(null);
        user4.setSalary(new Salary(user4, 80000, "RUB"));
        user4.setTravelOptions(new TravelOptions(user4, new Location(55.59225991787999, 37.598054613086425),
                30, "public_transport"));

        List<VacancyPreview> vacanciesFound4 = headHunterService.searchVacanciesByUserSinceDate(user4, yesterdayDate);

        assertTrue(vacanciesFound4.size() > 0);
        assertNotNull(vacanciesFound4.get(0).getName());
        assertNotNull(vacanciesFound4.get(0).getAddress());

        // Case 5 - User specified nothing - he gets nothing in response
        User user5 = new User("idleUser", "password");

        List<VacancyPreview> vacanciesFound5 = headHunterService.searchVacanciesByUserSinceDate(user5, yesterdayDate);
        assertEquals(0, vacanciesFound5.size());

        // Case 6 - Walking route
        User user6 = new User("simpleUser", "password");
        user6.setTravelOptions(new TravelOptions(user6, new Location(55.59225991787999, 37.598054613086425),
                90, "walking"));

        List<VacancyPreview> vacanciesFound6 = headHunterService.searchVacanciesByUserSinceDate(user6, yesterdayDate);
        assertTrue(vacanciesFound6.size() > 0);
        assertNotNull(vacanciesFound6.get(0).getName());
        assertNotNull(vacanciesFound6.get(0).getAddress());
    }

    @Test
    void searchNextByUserWorksFine() throws GoogleMapsInvalidApiKeyException {
        // Case 1 - New user without vacancies found
        User user1 = new User("newUser", "password");
        user1.setRegistrationDate(new Date());
        user1.setSearchFilters("Программист;Java");
        assertNull(user1.getLastJobRequestDate());
        List<VacancyPreview> vacancies1 = headHunterService.searchNextByUser(user1);
        assertEquals(0, vacancies1.size());
        assertNotNull(user1.getLastJobRequestDate());

        // Case 2 - User with lastJobRequestDate set up
        Date registrationDate = new GregorianCalendar(2021, Calendar.JULY, 2,
                23, 0, 0).getTime();

        User user2 = new User("existingUser", "password");
        user2.setRegistrationDate(registrationDate);
        user2.setSearchFilters("Java;Backend;Junior");

        Date lastJobRequestDate = new GregorianCalendar(2021, Calendar.JULY, 3,
                23, 0, 0).getTime();

        user2.setLastJobRequestDate(lastJobRequestDate);
        List<VacancyPreview> vacancies2 = headHunterService.searchNextByUser(user2);
        assertTrue(vacancies2.size() > 1);
        assertNotNull(vacancies2.get(0).getName());
        if (vacancies2.size() > 2) {
            assertFalse(vacancies2.get(0).getPublishedAt().before(vacancies2.get(1).getPublishedAt()));
        }
        Date newJobRequestDate = user2.getLastJobRequestDate();
        assertTrue(lastJobRequestDate.before(newJobRequestDate));

        // Case 3 - User with all possible information set up
        User user3 = new User("fullUser", "password");
        user3.setRegistrationDate(registrationDate);
        user3.setLastJobRequestDate(lastJobRequestDate);
        user3.setSearchFilters("Менеджер;Продажи");
        user3.setSalary(new Salary(user3, 80000, "RUB"));
        user3.setTravelOptions(new TravelOptions(user3, new Location(55.59225991787999, 37.598054613086425),
                90, "public_transport"));
        List<VacancyPreview> vacancies3 = headHunterService.searchNextByUser(user3);
        assertTrue(vacancies3.size() > 0);
        assertNotNull(vacancies3.get(0).getName());
        if (vacancies3.size() > 2) {
            assertFalse(vacancies3.get(0).getPublishedAt().before(vacancies3.get(1).getPublishedAt()));
        }
        newJobRequestDate = user2.getLastJobRequestDate();
        assertTrue(lastJobRequestDate.before(newJobRequestDate));

        // Case 4 - User's getting empty list of vacancies if he did not provide any information
        User user4 = new User("emptyUser", "password");
        user4.setRegistrationDate(registrationDate);
        user4.setLastJobRequestDate(lastJobRequestDate);
        List<VacancyPreview> vacancies4 = headHunterService.searchNextByUser(user4);
        assertEquals(0, vacancies4.size());
    }

}
