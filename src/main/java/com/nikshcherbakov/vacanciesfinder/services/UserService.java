package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.repositories.*;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import com.nikshcherbakov.vacanciesfinder.utils.UserNotFoundException;
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
            Optional<Role> roleFromDb = roleRepository.findByName(role.getName());
            roleFromDb.ifPresentOrElse(userRolesFromDb::add, () -> userRolesFromDb.add(roleRepository.save(role)));
        }
        user.setRoles(userRolesFromDb);

        /* Mailing preferences */
        MailingPreference userMailingPreference = user.getMailingPreference();
        if (userMailingPreference.isUseTelegram() && user.getTelegramSettings() == null) {
            // User is trying to use telegram for sending notifications
            // without providing telegram
            throw new TelegramIsNotDefinedException();
        }

        Optional<MailingPreference> mailingPreferenceFromDb =
                mailingPreferenceRepository.findMailingPreferenceByUseEmailAndUseTelegram(
                        userMailingPreference.isUseEmail(), userMailingPreference.isUseTelegram());

        mailingPreferenceFromDb.ifPresentOrElse(user::setMailingPreference,
                () -> user.setMailingPreference(userMailingPreference));

        /* Travel options */
        TravelOptions userTravelOptions = user.getTravelOptions();
        if (userTravelOptions != null) {
            // User specified travel options
            userTravelOptions.setUser(user);
        }

        /* Salary */
        Salary userSalary = user.getSalary();
        if (userSalary != null) {
            // User specified salary
            userSalary.setUser(user);
        }

        // Saving user to the database
        saveBondedUser(user);
    }

    /**
     * Safely stores a user to the database after binding. By binding it is meant
     * that a user is associated to all records from the database that should not
     * be rewritten to the database (e.g. user role)
     * @param user a user that needs to be saved or updated
     */
    @Transactional
    public void saveBondedUser(@NotNull User user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            logger.error(String.format("Error occurred while saving user %s to the database", user.getUsername()));
            e.printStackTrace();
        }
    }

    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public User retrieveAuthenticatedUser() {
        if (isUserAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsername(username).orElse(null);
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

    /**
     * Adds new found vacancies to a user and saves the database. If a user does
     * not exist in the database the method will log out a corresponding message
     * @param user a user to who found vacancies will be added
     */
    @Transactional
    public void addFoundVacanciesAndSave(User user) {
        List<VacancyPreview> vacancies = user.getLastJobRequestVacancies();

        // Checking if user exists in the database
        String username = user.getUsername();
        if (userRepository.findByUsername(username).isEmpty()) {
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

        userRepository.save(user);
        logger.info(String.format(user.getLastJobRequestVacancies().size() > 0 ?
                "User %s is saved after adding vacancies" :
                "No new vacancies are found for user %s",
                user.getUsername()));
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
     * Finds users from the database by telegram
     * @param telegram telegram to find users by
     * @return list of users with specified telegram
     */
    public List<User> findUsersByTelegram(@NotNull String telegram)  {
        return userRepository.findByTelegram(telegram);
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
}
