package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.services.EmailService;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.utils.ChangePasswordForm;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsTakenException;
import com.nikshcherbakov.vacanciesfinder.utils.UserAccountForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Controller
public class AccountController {

    @Value("${app.google.api.key}")
    private String googleMapsApiKey;

    @Value("${app.maps.defaults.latitude}")
    private double defaultLatitude;

    @Value("${app.maps.defaults.longitude}")
    private double defaultLongitude;

    @Value("${app.telegram.bot.name}")
    private String botName;

    private final UserService userService;
    private final EmailService emailService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountController(UserService userService, EmailService emailService,
                             BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/account")
    public String showAccountPage(Model model) {
        User user = userService.retrieveAuthenticatedUser();

        UserAccountForm userAccountForm = new UserAccountForm(
                user.getUsername(),
                user.getMailingPreference().isUseEmail(),
                user.getTelegramSettings() != null ?
                        user.getTelegramSettings().getTelegram() : "",
                user.getMailingPreference().isUseTelegram(),
                user.getTravelOptions() == null ? defaultLongitude :
                        user.getTravelOptions().getLocation().getLongitude(),
                user.getTravelOptions() == null ? defaultLatitude :
                        user.getTravelOptions().getLocation().getLatitude(),
                user.getTravelOptions() == null ? 0 : user.getTravelOptions().getTravelTimeInMinutes(),
                user.getTravelOptions() == null ? "car" : user.getTravelOptions().getTravelBy(),
                user.getSalary() == null ? null : user.getSalary().getValue(),
                user.getSalary() == null ? "RUB" : user.getSalary().getCurrency(),
                user.getSearchFilters()
        );

        // Checking if telegram is taken already
        User userWithActiveTelegram = userService.findUserByActiveTelegram(userAccountForm.getTelegram());
        boolean telegramIsTaken = !userAccountForm.getTelegram().equals("") &&
                userWithActiveTelegram != null && !user.equals(userWithActiveTelegram);

        // Mailing is active only when user provided at least search filters, location or salary
        boolean mailingIsActive = user.getSearchFilters() != null ||
                user.getTravelOptions() != null ||
                user.getSalary() != null;

        model.addAttribute("userForm", userAccountForm);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("botName", botName);
        model.addAttribute("telegramIsTaken", telegramIsTaken);
        model.addAttribute("mailingIsActive", mailingIsActive);
        return "account";
    }

    @PostMapping("/account")
    public String handleAccountForm(@ModelAttribute UserAccountForm userForm, Model model) {

        model.addAttribute("userForm", userForm);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        try {
            boolean changesSaved = refreshUserDataWithUserAccountForm(userForm);

            boolean mailingIsActive = !userForm.getSearchFilters().isEmpty() ||
                    userForm.getLatitude() != defaultLatitude ||
                    userForm.getLongitude() != defaultLongitude ||
                    userForm.getSalaryValue() != null;

            model.addAttribute("savedSuccessfully", changesSaved);
            model.addAttribute("botName", botName);
            model.addAttribute("mailingIsActive", mailingIsActive);
            return "account";
        } catch (TelegramIsNotDefinedException e) {
            userForm.setUseTelegram(false);
            model.addAttribute("telegramIsNotDefined", true);
            return "account";
        } catch (TelegramIsTakenException e) {
            model.addAttribute("telegramIsTaken", true);
            return "account";
        }

    }

    private boolean refreshUserDataWithUserAccountForm(@NotNull UserAccountForm form)
            throws TelegramIsNotDefinedException, TelegramIsTakenException {

        User userFromDb = userService.findUserByUsername(form.getUsername());

        if (userFromDb == null) {
            // No such user in the database
            return false;
        }

        /* Saving data from UserAccountForm */
        MailingPreference mailingPreference = new MailingPreference(form.isUseEmail(),
                form.isUseTelegram());
        String searchFilters = form.getSearchFilters().equals("") ? null : form.getSearchFilters();
        String telegram = form.getTelegram().equals("") ? null : form.getTelegram();

        // Checking if a telegram is taken already by another user
        if (telegram != null) {
            User userWithActiveTelegram = userService.findUserByActiveTelegram(telegram);
            if (userWithActiveTelegram != null && !userFromDb.equals(userWithActiveTelegram)) {
                throw new TelegramIsTakenException();
            }
        }

        if (form.isUseTelegram() && telegram == null) {
            // User is trying to use telegram for sending notifications
            // without providing telegram
            throw new TelegramIsNotDefinedException();
        }

        /* Travel options */
        if (userFromDb.getTravelOptions() == null) {
            // Save user's location only if one changed default location or specified travel time
            boolean userChangedDefaultLocation = form.getLatitude() != defaultLatitude ||
                    form.getLongitude() != defaultLongitude;
            boolean userSpecifiedTravelTime = form.getTravelTimeInMins() != 0;

            if (userChangedDefaultLocation || userSpecifiedTravelTime) {
                userFromDb.setTravelOptions(new TravelOptions(userFromDb,
                        new Location(form.getLatitude(), form.getLongitude()),
                        form.getTravelTimeInMins(), form.getTravelBy()));
            }
        } else {
            // Refreshing travel options
            TravelOptions userTravelOptions = userFromDb.getTravelOptions();
            userTravelOptions.setLocation(new Location(form.getLatitude(), form.getLongitude()));
            userTravelOptions.setTravelTimeInMinutes(form.getTravelTimeInMins());
            userTravelOptions.setTravelBy(form.getTravelBy());
        }

        /* Salary */
        if (form.getSalaryValue() != null) {
            if (userFromDb.getSalary() == null) {
                // Setting up salary
                userFromDb.setSalary(new Salary(userFromDb, form.getSalaryValue(), form.getCurrency()));
            } else {
                // Refreshing user's salary
                Salary userSalary = userFromDb.getSalary();
                userSalary.setValue(form.getSalaryValue());
                userSalary.setCurrency(form.getCurrency());
            }
        } else {
            userFromDb.setSalary(null);
        }

        /* Telegram */
        if (telegram != null) {
            if (userFromDb.getTelegramSettings() == null) {
                // Setting up telegram
                userFromDb.setTelegramSettings(new TelegramSettings(userFromDb, telegram));
            } else {
                TelegramSettings userTelegramSettings = userFromDb.getTelegramSettings();
                userTelegramSettings.setTelegram(telegram);
            }
        } else {
            userFromDb.setTelegramSettings(null);
            mailingPreference = new MailingPreference(form.isUseEmail(), false);
        }

        /* Adding changed fields to a user */
        userFromDb.setSearchFilters(searchFilters);
        userFromDb.setMailingPreference(mailingPreference);

        userService.saveUser(userFromDb);
        return true;
    }

    @GetMapping("/changePassword")
    public String showChangePassword(@RequestParam(name = "user", required = false) String username,
                                     @RequestParam(name = "hash", required = false) String hashValue,
                                     Model model) {
        if (username == null && hashValue == null) {
            User user = userService.retrieveAuthenticatedUser();

            // Sending a user a message with the instructions to change password
            emailService.sendChangePasswordMessage(user);

            return "change-password-instructions-sent";
        } else {
            // Handling password change
            if (username != null && hashValue != null) {
                // Checking if a user exists
                User userFromDb = userService.findByUsername(username);
                if (userFromDb != null) {
                    if (userFromDb.isEnabled() && hashValue.equals(userFromDb.getPassword())) {
                        model.addAttribute("changePasswordForm",
                                new ChangePasswordForm(userFromDb.getUsername(), userFromDb.getPassword()));
                        return "change-password";
                    }
                }
            }
        }
        return "404";
    }

    @PostMapping("/changePassword")
    public String handleChangePassword(@RequestParam(name = "user") String username,
                                       @RequestParam(name = "hash") String hashValue, @Valid ChangePasswordForm form,
                                       BindingResult bindingResult, Model model) {
        // Checking if a form does not contain errors
        if (bindingResult.hasErrors()) {
            return "change-password";
        }
        User userFromDb = userService.findUserByUsername(username);
        if (userFromDb != null) {
            if (userFromDb.isEnabled() && hashValue.equals(userFromDb.getPassword())) {
                // User is confirmed
                if (form.getPassword().equals(form.getPasswordConfirm())) {
                    // Setting up a new password
                    userFromDb.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
                    userService.save(userFromDb);
                    return "changed-password-confirmed";
                } else {
                    // Password does not match password confirmation
                    model.addAttribute("passwordDoesNotMatchPasswordConfirm", true);
                    model.addAttribute("changePasswordForm", form);
                    return "change-password";
                }
            }
        }
        return "404";
    }

}
