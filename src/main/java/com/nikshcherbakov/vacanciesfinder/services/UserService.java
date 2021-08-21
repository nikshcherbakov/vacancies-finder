package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.repositories.*;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.*;


@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(VacanciesFinderApplication.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    private final RoleService roleService;
    private final MailingPreferenceService mailingPreferenceService;
    private final AddressService addressService;
    private final VacanciesService vacanciesService;
    private final UserVacancyService userVacancyService;
    private final VacancyEmployerService employerService;
    private final VacancyAreaService areaService;

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                       RoleService roleService, MailingPreferenceService mailingPreferenceService, AddressService addressService,
                       VacanciesService vacanciesService, UserVacancyService userVacancyService, VacancyEmployerService employerService, VacancyAreaService areaService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.mailingPreferenceService = mailingPreferenceService;
        this.addressService = addressService;
        this.vacanciesService = vacanciesService;
        this.userVacancyService = userVacancyService;
        this.employerService = employerService;
        this.areaService = areaService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userInDatabase = userRepository.findByUsername("username").orElse(null);

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
     * @param user a new user with specified at least username and password
     * @return true if user is saved successfully otherwise - false
     */
    public boolean saveNewUser(@NotNull User user) {

        // Checking if a user exists in the database
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // User already exists in the database
            return false;
        }

        // Initializing non-initialized fields by default
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
            user.getRoles().add(new Role("ROLE_USER"));
        }

        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(new Date());
        }

        if (user.getMailingPreference() == null) {
            user.setMailingPreference(new MailingPreference(true, false));
        }

        try {
            saveUser(user);
        } catch (TelegramIsNotDefinedException e) {
            e.printStackTrace();
        }

        // Save user with encoded password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    /**
     * Saves user to a database associating user's data with existing
     * records in database
     * @param user a user that needs to be saved
     * @throws TelegramIsNotDefinedException if user wants to use telegram
     * without providing it
     */
    public void saveUser(@NotNull User user) throws TelegramIsNotDefinedException {
        /* Adding corresponding records from db to the user */

        /* Roles */
        Set<Role> userRolesFromDb = new HashSet<>();
        for (Role role : user.getRoles()) {
            Role roleFromDb = roleService.findByName(role.getName());
            userRolesFromDb.add(Objects.requireNonNullElseGet(roleFromDb, () -> roleService.save(role)));
        }
        user.setRoles(userRolesFromDb);

        /* Mailing preferences */
        MailingPreference userMailingPreference = user.getMailingPreference();
        MailingPreference mailingPreferenceFromDb =
                mailingPreferenceService.findByMailingPreference(userMailingPreference);
        if (mailingPreferenceFromDb != null) {
            user.setMailingPreference(mailingPreferenceFromDb);
        }

        // Saving user to the database
        save(user);
    }

    /**
     * Safely stores a user to the database after binding. By binding it is meant
     * that a user is associated to all records from the database that should not
     * be rewritten to the database (e.g. user role)
     * @param user a user that needs to be saved or updated
     */
    public void save(@NotNull User user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            logger.error(String.format("Error occurred while saving user %s to the database", user.getUsername()));
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a user authenticated
     * @return true if a user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Retrieves authenticated user from context
     * @return user if one is authenticated, otherwise null
     */
    public User retrieveAuthenticatedUser() {
        if (isUserAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsername(username).orElse(null);
        } else {
            // User is not authenticated
            return null;
        }
    }

    /**
     * Returns list of active users from the database for which
     * {@link User#isEnabled()} returns true
     * @return list of active users
     */
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

    /**
     * Adds new found vacancies to a user and saves the database. If a user does
     * not exist in the database the method will log out a corresponding message
     * @param user a user to who found vacancies will be added
     */
    @Transactional
    public void addFoundVacancies(User user) {
        List<VacancyPreview> vacancies = user.getLastJobRequestVacancies();

        // Checking if user exists in the database
        String username = user.getUsername();
        if (userRepository.findByUsername(username).isEmpty()) {
            // No such user in the database
            logger.info(String.format("Attempt to save vacancy to a non-existing user %s is registered", username));
        }

        for (VacancyPreview vacancy : vacancies) {
            /* Checking if a vacancy is already in the user's list of vacancies */
            VacancyPreview vacancyFromDb = vacanciesService.findById(vacancy.getId());
            if (vacancyFromDb != null) {
                /* Vacancy exists already */
                if (!userVacancyService.existsById(
                        new UserVacancyId(user.getId(), vacancyFromDb.getId()))) {
                    // User does not have this vacancy (needed if a vacancy is published not for the first time)
                    user.addVacancy(vacancyFromDb);
                }
            } else {
                /* Adding new vacancy to the database */

                // Checking addresses
                Address vacancyAddress = vacancy.getAddress();
                if (vacancyAddress != null) {
                    Address addressFromDb = addressService.findById(vacancyAddress.getId());
                    if (addressFromDb != null) {
                        vacancy.setAddress(addressFromDb);
                    } else {
                        addressService.save(vacancyAddress);
                        vacancyAddress.addVacancyPreview(vacancy);
                        vacancy.setAddress(vacancyAddress);
                    }
                }

                // Checking employer
                VacancyEmployer vacancyEmployer = vacancy.getEmployer();
                if (vacancyEmployer != null) {
                    VacancyEmployer employerFromDb = employerService.findById(vacancyEmployer.getId());
                    if (employerFromDb != null) {
                        vacancy.setEmployer(employerFromDb);
                    } else {
                        employerService.save(vacancyEmployer);
                        vacancyEmployer.addVacancyPreview(vacancy);
                        vacancy.setEmployer(vacancyEmployer);
                    }
                }

                // Checking area
                VacancyArea vacancyArea = vacancy.getArea();
                if (vacancyArea != null) {
                    VacancyArea areaFromDb = areaService.findById(vacancyArea.getId());
                    if (areaFromDb != null) {
                        vacancy.setArea(areaFromDb);
                    } else {
                        areaService.save(vacancyArea);
                        vacancyArea.addVacancyPreview(vacancy);
                        vacancy.setArea(vacancyArea);
                    }
                }

                vacanciesService.save(vacancy);
                user.addVacancy(vacancy);

            }
        }

    }

    /**
     * Finds a user by a username
     * @param username username (e-mail) by which the search is performed
     * @return found user if one is found, otherwise - null
     */
    public User findUserByUsername(@NotNull String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    /**
     * Finds a user from the database by active telegram (with defined
     * chat id)
     * @param telegram telegram identifier
     * @return user if user with such telegram is found, null if there's
     * no user with such telegram in the database
     */
    public User findUserByActiveTelegram(@NotNull String telegram) {
        Optional<User> user = userRepository.findByActiveTelegram(telegram);
        return user.orElse(null);
    }

    /**
     * Finds user from the database by chat id
     * @param chatId chat id by which user is found
     * @return user with specified chat id, if there's no such user returns null
     */
    public User findUserByChatId(@NotNull long chatId) {
        Optional<User> user = userRepository.findByChatId(chatId);
        return user.orElse(null);
    }

    /**
     * Removes vacancy from user and deletes all rows associated
     * with vacancy if they are not used anymore
     * @param user a user from which vacancy will be removed
     * @param vacancy vacancy to be removed
     */
    @Transactional
    public void removeVacancyFromUser(User user, VacancyPreview vacancy) {
        // Removing vacancy from user
        VacancyPreview removedVacancy = user.removeVacancy(vacancy);

        // Checking if vacancy is not used by anybody anymore
        if (removedVacancy != null) {
            if (removedVacancy.getUsersVacancies().isEmpty()) {
                vacanciesService.removeVacancy(removedVacancy);
            }
        }
    }

    /**
     * Looks up for a user in the database by username
     * @param username a username by which a search is done
     * @return user if one exists in the database, null otherwise
     */
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
}
