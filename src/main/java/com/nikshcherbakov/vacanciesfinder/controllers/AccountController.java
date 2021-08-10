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


// TODO GLOBAL подумать о добавлении частоты рассылки и, возможно, других параметров
@Controller
public class AccountController {

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

        boolean telegramIsTaken = !userAccountForm.getTelegram().equals("") &&
                !userService.findUserByActiveTelegram(userAccountForm.getTelegram()).equals(user);

        model.addAttribute("userForm", userAccountForm);
        model.addAttribute("botName", botName);
        model.addAttribute("telegramIsTaken", telegramIsTaken);
        return "account";
    }

    @PostMapping("/account")
    public String handleAccountForm(@ModelAttribute UserAccountForm userForm, Model model) {

        try {
            boolean changesSaved = userService.refreshUserDataWithUserAccountForm(userForm);

            model.addAttribute("userForm", userForm);
            model.addAttribute("savedSuccessfully", changesSaved);
            model.addAttribute("botName", botName);
            return "account";
        } catch (TelegramIsNotDefinedException e) {
            userForm.setUseTelegram(false);
            model.addAttribute("userForm", userForm);
            model.addAttribute("telegramIsNotDefined", true);
            return "account";
        } catch (TelegramIsTakenException e) {
            model.addAttribute("userForm", userForm);
            model.addAttribute("telegramIsTaken", true);
            return "account";
        }

    }

}
