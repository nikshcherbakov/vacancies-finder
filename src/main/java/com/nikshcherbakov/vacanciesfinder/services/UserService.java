package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.repositories.*;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailingPreferenceRepository mailingPreferenceRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TravelOptionsRepository travelOptionsRepository;

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

    public boolean saveUser(User user) throws TelegramIsNotDefinedException {

        // Checking if a user exists in the database
        if (userRepository.findByUsername(user.getUsername()) != null) {
            // User already exists in the database
            return false;
        }

        refreshUserData(user);

        // Save user with encoded password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean refreshUser(User user) throws TelegramIsNotDefinedException {
        // Checking if a user exists in the database
        if (userRepository.findByUsername(user.getUsername()) == null) {
            // User already exists in the database
            return false;
        }
        refreshUserData(user);
        userRepository.save(user);
        return true;
    }

    private void refreshUserData(User user) throws TelegramIsNotDefinedException {
        /* Initializing non-initialized fields by default */

        if (user.getRoles() == null) {
            user.setRoles(Collections.singleton(new Role("ROLE_USER")));
        }

        if (user.getMailingPreference() == null) {
            user.setMailingPreference(new MailingPreference(true, false));
        }

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

            userRolesFromDb.add(roleFromDb);
        }

        // User's roles = objects retrieved from db
        user.setRoles(userRolesFromDb);

        /* Mailing preferences */
        MailingPreference userMailingPreference = user.getMailingPreference();

        MailingPreference mailingPreferenceFromDb =
                mailingPreferenceRepository.findMailingPreferenceByUseEmailAndUseTelegram(
                        userMailingPreference.isUseEmail(), userMailingPreference.isUseTelegram());

        if (mailingPreferenceFromDb == null) {
            // There's no such record in the db - adding one
            mailingPreferenceRepository.save(userMailingPreference);

            // Retrieving saved object from db
            mailingPreferenceFromDb =
                    mailingPreferenceRepository.findMailingPreferenceByUseEmailAndUseTelegram(
                            userMailingPreference.isUseEmail(), userMailingPreference.isUseTelegram());
        }

        // User's mailing preferences = object retrieved from db
        user.setMailingPreference(mailingPreferenceFromDb);

        if (userMailingPreference.isUseTelegram() && user.getTelegram() == null) {
            // User is trying to use telegram for sending notifications
            // without providing telegram
            throw new TelegramIsNotDefinedException();
        }

        if (user.getTravelOptions() != null) {
            /* User specified travel options */

            // Checking if locations exists
            Location userLocation = user.getTravelOptions().getLocation();

            Location locationFromDb = locationRepository.findLocationByLongitudeAndLatitude(
                    userLocation.getLongitude(), userLocation.getLatitude());
            if (locationFromDb == null) {
                locationRepository.save(userLocation);
                locationFromDb = locationRepository.findLocationByLongitudeAndLatitude(
                        userLocation.getLongitude(), userLocation.getLatitude());
            }

            TravelOptions userTravelOptions = user.getTravelOptions();

            TravelOptions travelOptionsFromDb = travelOptionsRepository
                    .findTravelOptionsByLocationAndTravelTimeInMinutesAndTravelBy(locationFromDb,
                            userTravelOptions.getTravelTimeInMinutes(),
                            userTravelOptions.getTravelBy());

            if (travelOptionsFromDb == null) {
                userTravelOptions.setLocation(locationFromDb);
                travelOptionsRepository.save(userTravelOptions);

                // Retrieving saved travelOptions from db
                travelOptionsFromDb = travelOptionsRepository
                        .findTravelOptionsByLocationAndTravelTimeInMinutesAndTravelBy(locationFromDb,
                                userTravelOptions.getTravelTimeInMinutes(),
                                userTravelOptions.getTravelBy());
            }

            // User's travelOptions = object retrieved from db
            user.setTravelOptions(travelOptionsFromDb);

        }

        Salary userSalary = user.getSalary();

        Salary salaryFromDb = salaryRepository.findByValueAndCurrency(userSalary.getValue(),
                userSalary.getCurrency());

        if (salaryFromDb == null) {
            // No such salary in db - adding one
            salaryRepository.save(userSalary);

            // Retrieving saved object from db
            salaryFromDb = salaryRepository.findByValueAndCurrency(userSalary.getValue(),
                    userSalary.getCurrency());
        }

        // Setting user's salary
        user.setSalary(salaryFromDb);
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
}
