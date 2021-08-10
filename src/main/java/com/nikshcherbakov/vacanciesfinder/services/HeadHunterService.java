package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.utils.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HeadHunterService implements IJobSearchService {

    @Value("${app.headhunter.urls.vacancies}")
    private String vacanciesUrl;

    @Value("${app.headhunter.settings.perpage}")
    private Integer perPage;

    @Value("${app.google.api.defaults.departure.hour}")
    private Integer departureHour;

    @Value("${app.google.api.defaults.departure.minute}")
    private Integer departureMinute;

    @Value("${app.google.api.defaults.departure.dayofweek}")
    private Integer departureDayOfWeek;

    @Value("${app.searchvacancies.defaults.maxvacanciesperrequest}")
    private Integer maxVacanciesPerRequest;

    private final GoogleMapsService googleMapsService;

    private final RestTemplate restTemplate;

    public HeadHunterService(GoogleMapsService googleMapsService, RestTemplateBuilder restTemplateBuilder) {
        this.googleMapsService = googleMapsService;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * The method to search vacancies by a user using russian job search platform
     * <a href="http://hh.ru">hh.ru</a> using corresponding
     * <a href="https://github.com/hhru/api">api</a>.
     * @implNote
     * The method uses function {@link #searchVacanciesByUserSinceDate(User, Date)} where
     * the second parameter represents the date of a user's last job request. To let the
     * method work correctly {@code User.lastJobRequestDate} should not be updated manually.
     * Note that the method will always return an empty array in case a user specifies no
     * information. This means that if {@code User.searchFilters}, {@code User.travelOptions}
     * and {@code User.salary} is {@code null} then the result will also be an empty array.
     * It's also important to know that there is a higher bound for number of vacancies to
     * be returned. The maximum number of possible vacancies is defined in the
     * {@code application.properties} file (parameter
     * {@code app.searchvacancies.defaults.maxvacanciesperrequest}).
     *
     * @param user a user by which vacancies are searched up
     * @return list of vacancy previews that were found for a user by user constraints
     * @throws IOException in case there occurred a problem while getting stream of bytes from api.hh.ru
     * @throws GoogleMapsInvalidApiKeyException if a GoogleMapsAPI key defined in
     * {@code application.properties} is not valid.
     */
    @Override
    public List<VacancyPreview> searchNextByUser(User user) throws IOException, GoogleMapsInvalidApiKeyException {
        // Defining search start date
        Date searchSince = user.getLastJobRequestDate() != null ?
                user.getLastJobRequestDate() :
                user.getRegistrationDate();

        // Searching vacancies since the date
        List<VacancyPreview> newVacancies = searchVacanciesByUserSinceDate(user, searchSince);
        Date newJobRequestDate = new Date();

        // Sort vacancies by date
        newVacancies.sort((o1, o2) -> {
            if (o1.getPublishedAt().after(o2.getPublishedAt())) {
                return -1;
            } else if (o1.getPublishedAt().before(o2.getPublishedAt())) {
                return 1;
            } else {
                return 0;
            }
        });

        user.setLastJobRequestDate(newJobRequestDate);
        return newVacancies;
    }

    /**
     * The method to search vacancies by a user using russian job search platform
     * <a href="http://hh.ru">hh.ru</a> using corresponding
     * <a href="https://github.com/hhru/api">api</a> since a particular date
     * @param user a user by which vacancies are searched up
     * @param date date since which the vacancies are searched up
     * @return list of vacancy previews that were found for a user by user constraints
     * @throws IOException in case there occurred a problem while getting stream of bytes from api.hh.ru
     * @throws GoogleMapsInvalidApiKeyException if a GoogleMapsAPI key defined in
     * {@code application.properties} is not valid or does not allow to obtain information via api.
     */
    public List<VacancyPreview> searchVacanciesByUserSinceDate(User user, Date date)
            throws IOException, GoogleMapsInvalidApiKeyException {
        Date dateSearchFrom = roundDateToClosestFiveMins(date);
        List<VacancyPreview> vacanciesSinceSearchFrom = loadVacanciesByUserConstraintsSinceDate(user, dateSearchFrom);

        // Getting rid of vacancies that the user has already
        List<VacancyPreview> vacanciesAfterDate = new ArrayList<>();
        for (VacancyPreview vacancy : vacanciesSinceSearchFrom) {
            Date vacancyDate = vacancy.getPublishedAt();
            if (vacancyDate.after(date)) {
                vacanciesAfterDate.add(vacancy);
            }
        }

        return vacanciesAfterDate;
    }

    /** This method rounds date to closest five minutes downward **/
    private static Date roundDateToClosestFiveMins(Date date) {
        long mins = date.getTime() / 1000 / 60;
        long closestFiveMins = (mins / 5) * 5;
        long resultInMillisec = closestFiveMins * 60 * 1000;
        return new Date(resultInMillisec);
    }

    /**
     * The method to load vacancies by a user using russian job search platform
     * <a href="http://hh.ru">hh.ru</a> using corresponding
     * <a href="https://github.com/hhru/api">api</a> since a particular date. This means that
     * only vacancies published after provided date will be returned.
     * {@code application.properties} file (parameter
     * {@code app.searchvacancies.defaults.maxvacanciesperrequest}).
     *
     * @param user a user by which vacancies are searched up
     * @param date date since which vacancies are loaded from hh.ru
     * @return list of vacancy previews that were found for a user by user constraints
     * @throws IOException in case there occurred a problem while getting stream of bytes from api.hh.ru
     * @throws GoogleMapsInvalidApiKeyException if a GoogleMapsAPI key defined in
     * {@code application.properties} is not valid.
     */
    private List<VacancyPreview> loadVacanciesByUserConstraintsSinceDate(@NotNull User user, Date date)
            throws IOException, GoogleMapsInvalidApiKeyException {

        // If user specified nothing he gets nothing
        if (user.getSearchFilters() == null && user.getSalary() == null && user.getTravelOptions() == null) {
            return new ArrayList<>();
        }

        Map<String, String> params = setupBasicUrlParamsByUserAndDate(user, date);
        List<VacancyPreview> appropriateVacancies = new ArrayList<>();

        if (user.getTravelOptions() != null) {
            // User specified location
            Location userLocation = user.getTravelOptions().getLocation();
            params.put("order_by", "distance");
            params.put("sort_point_lat", String.valueOf(userLocation.getLatitude()));
            params.put("sort_point_lng", String.valueOf(userLocation.getLongitude()));
            params.put("label", "with_address");

            try {
                List<VacancyPreview> vacanciesByDistance = requestVacanciesFromHH(params);

                // Looking for appropriate vacancies by travel time
                Date departureDate = generateDepartureDate(); // departure date to calculate travel time in traffic
                for (VacancyPreview vacancy : vacanciesByDistance) {
                    TravelOptions userTravelOptions = user.getTravelOptions();

                    Integer travelTimeInMins;
                    Location vacancyLocation;
                    TravelType userTravelBy = convertUserTravelByToTravelType(userTravelOptions.getTravelBy());
                    if (vacancy.getAddress().getLocation().getLatitude() != null) {
                        // Vacancy has explicitly stated location
                        vacancyLocation = vacancy.getAddress().getLocation();
                    } else {
                        // Vacancy has only closest metro station or stations
                        if (vacancy.getAddress().getMetro() != null) {
                            // Closest metro station exists
                            vacancyLocation = vacancy.getAddress().getMetro().getStationLocation();
                        } else {
                            // There's no closest metro station - calculating average lat and lng
                            List<MetroStation> vacancyMetroStations = vacancy.getAddress().getMetroStations();
                            if (vacancyMetroStations.size() > 0) {
                                Double sumLat = 0.0;
                                Double sumLng = 0.0;
                                for (MetroStation metroStation : vacancyMetroStations) {
                                    sumLat += metroStation.getLat();
                                    sumLng += metroStation.getLng();
                                }

                                vacancyLocation = new Location(sumLat / vacancyMetroStations.size(),
                                        sumLng / vacancyMetroStations.size());
                            } else {
                                // No information about vacancy location obtained
                                break;
                            }
                        }
                    }

                    // Calculating travel time using GoogleMapsService
                    travelTimeInMins = googleMapsService.calculateTravelTimeInMins(userLocation, vacancyLocation,
                            userTravelBy, departureDate);

                    if (travelTimeInMins <= userTravelOptions.getTravelTimeInMinutes()
                            && travelTimeInMins != -1) {
                        appropriateVacancies.add(vacancy);
                    }
                }
                return appropriateVacancies;

            } catch (HTTPEmptyGetParameterException e) {
                e.printStackTrace();
            }

        } else {
            // User did not specify location
            try {
                appropriateVacancies = requestVacanciesFromHH(params);
            } catch (HTTPEmptyGetParameterException e) {
                e.printStackTrace();
            }
        }
        return appropriateVacancies;
    }

    private List<VacancyPreview> requestVacanciesFromHH(Map<String, String> params)
            throws HTTPEmptyGetParameterException {
        // Uploading all vacancies by user filters from hh.ru
        JobsRequest newJobsRequest = requestVacanciesByUrlParams(params);
        List<VacancyPreview> appropriateVacancies = new ArrayList<>(newJobsRequest.getItems());
        for (int currentPage = 1; currentPage < newJobsRequest.getPages() &&
                appropriateVacancies.size() < maxVacanciesPerRequest; currentPage++) {
            params.put("page", String.valueOf(currentPage));
            newJobsRequest = requestVacanciesByUrlParams(params);
            appropriateVacancies.addAll(newJobsRequest.getItems());
        }

        // Shorten vacanciesByDistance if it contains too many vacancies
        if (appropriateVacancies.size() > maxVacanciesPerRequest) {
            appropriateVacancies = appropriateVacancies.subList(0, maxVacanciesPerRequest);
        }

        return appropriateVacancies;
    }

    private static String convertDateToISO8601(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        String dateFormatted = formatter.format(date);
        return dateFormatted.substring(0, 10) + 'T' + dateFormatted.substring(10);
    }

    private static TravelType convertUserTravelByToTravelType(String userTravelBy) {
        switch (userTravelBy) {
            case "walking":
                return TravelType.WALKING;
            case "public_transport":
                return TravelType.PUBLIC_TRANSPORT;
            default:
                return TravelType.CAR;
        }
    }

    /** Method generates the departure date to calculate travel time using google service.
     * It finds the closest date to the current time with the following parameters:
     * hours = departureHours;
     * minutes = departureMinutes;
     * dayOfWeek = departureDayOfWeek **/
    private Date generateDepartureDate() {
        Calendar calendar = new GregorianCalendar();
        Date now = calendar.getTime();

        calendar.set(Calendar.DAY_OF_WEEK, departureDayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, departureHour);
        calendar.set(Calendar.MINUTE, departureMinute);
        calendar.set(Calendar.SECOND, 0);
        Date targetDateThisWeek = calendar.getTime();

        if (targetDateThisWeek.after(now)) {
            return targetDateThisWeek;
        } else {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            return calendar.getTime();
        }
    }

    /** Method sets up the basic url params (salary, search filters and date to start search with) without location **/
    public Map<String, String> setupBasicUrlParamsByUserAndDate(User user, Date date) {
        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));
        params.put("date_from", convertDateToISO8601(date));
        if (user.getSearchFilters() != null) {
            params.put("text", user.getSearchFilters().replace(';', ' '));
        }
        if (user.getSalary() != null) {
            params.put("salary", user.getSalary().getValue().toString());
            params.put("currency", user.getSalary().getCurrency().equals("RUB") ?
                    "RUR" : user.getSalary().getCurrency());
            params.put("only_with_salary", "true");
        }
        return params;
    }

    @Override
    public JobsRequest requestVacanciesByUrlParams(Map<String, String> params)
            throws HTTPEmptyGetParameterException {
        return sendJobsRequest(params);
    }

    private HeadHunterJobsRequest sendJobsRequest(Map<String, String> params)
            throws HTTPEmptyGetParameterException {
        String url;
        if (params != null) {
            String paramsUrl = HTTPParameterStringBuilder.getParamsString(params);
            url = vacanciesUrl + paramsUrl;
        } else {
            url = vacanciesUrl;
        }
        return restTemplate.getForObject(url, HeadHunterJobsRequest.class);
    }

}
