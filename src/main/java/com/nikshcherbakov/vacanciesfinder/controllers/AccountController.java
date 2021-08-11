package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsTakenException;
import com.nikshcherbakov.vacanciesfinder.utils.UserAccountForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    public AccountController(UserService userService) {
        this.userService = userService;
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

        TelegramSettings telegramSettings = null;
        if (telegram != null) {
            telegramSettings = new TelegramSettings(telegram);
        }

        /* Adding changed fields to a user */
        userFromDb.setSearchFilters(searchFilters);
        userFromDb.setMailingPreference(mailingPreference);

        if (userFromDb.getTelegramSettings() != null) {
            if (!userFromDb.getTelegramSettings().getTelegram().equals(telegram)) {
                // User changed telegram - deleting one cause it's OneToOne relationship
                userFromDb.setTelegramSettings(telegram != null ? new TelegramSettings(telegram) : null);
            }
        } else {
            // Nothing to delete - adding new telegram settings to a user
            userFromDb.setTelegramSettings(telegramSettings);
        }

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

        userService.saveUser(userFromDb);
        return true;
    }

}
