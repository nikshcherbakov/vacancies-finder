package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.repositories.*;
import com.nikshcherbakov.vacanciesfinder.routines.ScheduledVacanciesSearch;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import com.nikshcherbakov.vacanciesfinder.utils.UserAccountForm;
import com.nikshcherbakov.vacanciesfinder.utils.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;


@Service
public class UserService implements UserDetailsService {

    @Value("${app.maps.defaults.latitude}")
    private double defaultLatitude;

    @Value("${app.maps.defaults.longitude}")
    private double defaultLongitude;

    private static final Logger logger = LoggerFactory.getLogger(VacanciesFinderApplication.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailingPreferenceRepository mailingPreferenceRepository;
    private final AddressRepository addressRepository;
    private final VacancyEmployerRepository employerRepository;
    private final VacancyPreviewRepository vacancyRepository;
    private final VacancyAreaRepository areaRepository;

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                       RoleRepository roleRepository, MailingPreferenceRepository mailingPreferenceRepository,
                       AddressRepository addressRepository, VacancyEmployerRepository employerRepository,
                       VacancyPreviewRepository vacancyRepository, VacancyAreaRepository areaRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mailingPreferenceRepository = mailingPreferenceRepository;
        this.addressRepository = addressRepository;
        this.employerRepository = employerRepository;
        this.vacancyRepository = vacancyRepository;
        this.areaRepository = areaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userInDatabase = userRepository.findByUsername("username");

        if (userInDatabase != null) {
            // User found in the database
            return userInDatabase;
        } else {
            // No such user in the database
            throw new UsernameNotFoundException("There's no such a user in the database");
        }
    }

    /**
     * Method to save new user that is not in the database yet
     */
    public boolean saveNewUser(@NotNull User user) throws TelegramIsNotDefinedException {

        // Checking if a user exists in the database
        if (userRepository.findByUsername(user.getUsername()) != null) {
            // User already exists in the database
            return false;
        }

        // Initializing non-initialized fields by default
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
            user.getRoles().add(new Role("ROLE_USER"));
        }

        if (user.getMailingPreference() == null) {
            user.setMailingPreference(new MailingPreference(true, false));
        }

        bindUserDataToRecordsFromDb(user);

        // Save user with encoded password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    // TODO GENERAL подумать над тем, чтобы сделать метод transactional
    public boolean refreshUserDataWithUserAccountForm(@NotNull UserAccountForm form)
            throws TelegramIsNotDefinedException {

        User userFromDb = userRepository.findByUsername(form.getUsername());

        if (userFromDb == null) {
            // No such user in the database
            return false;
        }

        /* Saving data from UserAccountForm */
        MailingPreference mailingPreference = new MailingPreference(form.isUseEmail(),
                form.isUseTelegram());

        String telegram = form.getTelegram().equals("") ? null : form.getTelegram();

        String searchFilters = form.getSearchFilters().equals("") ? null : form.getSearchFilters();

        TravelOptions travelOptions = null;
        if (userFromDb.getTravelOptions() == null) {
            // Save user's location only if one changed default location or specified travel time
            boolean userChangedDefaultLocation = form.getLatitude() != defaultLatitude ||
                    form.getLongitude() != defaultLongitude;
            boolean userSpecifiedTravelTime = form.getTravelTimeInMins() != 0;

            if (userChangedDefaultLocation || userSpecifiedTravelTime) {
                travelOptions = new TravelOptions(new Location(form.getLatitude(), form.getLongitude()),
                        form.getTravelTimeInMins(), form.getTravelBy());
            }
        } else {
            // Just bind new travel options to current user
            travelOptions = new TravelOptions(new Location(form.getLatitude(), form.getLongitude()),
                    form.getTravelTimeInMins(), form.getTravelBy());
        }

        Salary salary = null;
        if (form.getSalaryValue() != null) {
            salary = new Salary(form.getSalaryValue(), form.getCurrency());
        }

        /* Adding changed fields to a user */
        userFromDb.setTelegram(telegram);
        userFromDb.setSearchFilters(searchFilters);
        userFromDb.setMailingPreference(mailingPreference);

        if (userFromDb.getSalary() != null) {
            if (!userFromDb.getSalary().equals(salary)) {
                // User changed salary - deleting one cause it's OneToOne relationship
                userFromDb.setSalary(salary);
            }
        } else {
            // There's nothing to delete - just adding new salary to a user
            userFromDb.setSalary(salary);
        }

        if (userFromDb.getTravelOptions() != null) {
            if (!userFromDb.getTravelOptions().equals(travelOptions)) {
                // User changed travel options - deleting it cause it's OneToOne relationship
                userFromDb.setTravelOptions(travelOptions);
            }
        } else {
            // There's nothing to delete - just adding new travel options to a user
            userFromDb.setTravelOptions(travelOptions);
        }

        bindUserDataToRecordsFromDb(userFromDb);

        userRepository.save(userFromDb);
        return true;
    }

    // TODO GENERAL Исправить связку объектов в соответствии с методом addFoundVacancies
    private void bindUserDataToRecordsFromDb(@NotNull User user) throws TelegramIsNotDefinedException {
        /* Adding corresponding records from db to the user */

        /* Roles */
        Set<Role> userRolesFromDb = new HashSet<>();

        for (Role userRole: user.getRoles()) {
            String roleName = userRole.getName();
            Role roleFromDb = roleRepository.findByName(roleName);

            if (roleFromDb == null) {
                // No such role in the db - adding one
                roleRepository.save(userRole);

                // Retrieving saved role from db
                roleFromDb = roleRepository.findByName(roleName);
            }

            if (roleFromDb.getUsers() == null) {
                roleFromDb.setUsers(new HashSet<>());
            }

            roleFromDb.getUsers().add(user);
            userRolesFromDb.add(roleFromDb);
        }

        // User's roles = objects retrieved from db
        user.setRoles(userRolesFromDb);

        /* Travel options */
        TravelOptions userTravelOptions = user.getTravelOptions();

        if (userTravelOptions != null) {
            /* User specified travel options */
            userTravelOptions.setUser(user);
            user.setTravelOptions(userTravelOptions);
        }

        /* Salary */
        Salary userSalary = user.getSalary();

        if (userSalary != null) {
            /* User specified salary */
            userSalary.setUser(user);
            user.setSalary(userSalary);
        }

        /* Mailing preferences */
        MailingPreference userMailingPreference = user.getMailingPreference();

        if (userMailingPreference.isUseTelegram() && user.getTelegram() == null) {
            // User is trying to use telegram for sending notifications
            // without providing telegram
            throw new TelegramIsNotDefinedException();
        }

        MailingPreference mailingPreferenceFromDb =
                mailingPreferenceRepository.findMailingPreferenceByUseEmailAndUseTelegram(
                        userMailingPreference.isUseEmail(), userMailingPreference.isUseTelegram());

        if (mailingPreferenceFromDb == null) {
            // There's no such record in the db - adding one
            user.setMailingPreference(userMailingPreference);
        } else {
            // User's mailing preferences = object retrieved from db
            if (mailingPreferenceFromDb.getUsers() == null) {
                mailingPreferenceFromDb.setUsers(new HashSet<>());
            }
            mailingPreferenceFromDb.getUsers().add(user);
            user.setMailingPreference(mailingPreferenceFromDb);
        }

    }

    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public User retrieveAuthenticatedUser() {
        if (isUserAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsername(username);
        } else {
            // User is not authenticated
            return null;
        }
    }

    public List<User> getAllActiveUsers() {
        List<User> users = userRepository.findAll();
        List<User> activeUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isEnabled()) {
                activeUsers.add(user);
            }
        }
        return activeUsers;
    }

    public void refreshUser(User user) throws UserNotFoundException {
        if (userRepository.findByUsername(user.getUsername()) == null) {
            // No such user in the database
            throw new UserNotFoundException();
        }
        userRepository.save(user);
    }

    // TODO GENERAL Подумать над тем, чтобы сделать метод transactional
    /**
     * Adds new found vacancies to user and saves the database. If a user does
     * not exist in the database the method will log out a corresponding message
     * @param userVacanciesMap a map where list of new found vacancies
     *                         is associated with corresponding user
     */
    public void addFoundVacanciesAndSave(Map<User, List<VacancyPreview>> userVacanciesMap) {
        for (Map.Entry<User, List<VacancyPreview>> entry : userVacanciesMap.entrySet()) {
            // Taking out each entry
            User user = entry.getKey();
            List<VacancyPreview> vacancies = entry.getValue();

            // Checking if user exists in the database
            String username = user.getUsername();
            if (userRepository.findByUsername(username) == null) {
                // No such user in the database
                logger.info(String.format("Attempt to save vacancy to a non-existing user %s is registered", username));
            }

            for (VacancyPreview vacancy : vacancies) {
                /* Checking if a vacancy is already in the user's list of vacancies */
                Optional<VacancyPreview> vacancyFromDb = vacancyRepository.findById(vacancy.getId());
                if (vacancyFromDb.isPresent()) {
                    /* Vacancy exists already */
                    VacancyPreview existingVacancyFromDb = vacancyFromDb.get();
                    if (!existingVacancyFromDb.getUsers().contains(user)) {
                        // User does not have this vacancy (needed if a vacancy is published not for the first time)
                        existingVacancyFromDb.addUser(user);
                        user.addVacancy(existingVacancyFromDb);
                    }
                } else {
                    /* Adding new vacancy to the database */

                    // Checking addresses
                    Address vacancyAddress = vacancy.getAddress();
                    if (vacancyAddress != null) {
                        Optional<Address> addressFromDb = addressRepository.findById(vacancyAddress.getId());
                        addressFromDb.ifPresentOrElse(vacancy::setAddress, () -> {
                            Address savedAddress = addressRepository.save(vacancyAddress);
                            vacancy.setAddress(savedAddress);
                        });
                    }

                    // Checking employer
                    VacancyEmployer vacancyEmployer = vacancy.getEmployer();
                    if (vacancyEmployer != null) {
                        Optional<VacancyEmployer> employerFromDb = employerRepository.findById(vacancyEmployer.getId());
                        employerFromDb.ifPresentOrElse(vacancy::setEmployer, () -> {
                            VacancyEmployer savedEmployer = employerRepository.save(vacancyEmployer);
                            vacancy.setEmployer(savedEmployer);
                        });
                    }

                    // Checking area
                    VacancyArea vacancyArea = vacancy.getArea();
                    if (vacancyArea != null) {
                        Optional<VacancyArea> areaFromDb = areaRepository.findById(vacancyArea.getId());
                        areaFromDb.ifPresentOrElse(vacancy::setArea, () -> {
                            VacancyArea savedArea = areaRepository.save(vacancyArea);
                            vacancy.setArea(savedArea);
                        });
                    }

                    vacancy.addUser(user);
                    user.addVacancy(vacancy);
                }
            }
        }

        userRepository.saveAll(userVacanciesMap.keySet());
        logger.info("Users are saved after adding vacancies");
    }

}
