package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import com.nikshcherbakov.vacanciesfinder.models.TravelOptions;
import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import com.nikshcherbakov.vacanciesfinder.utils.UserAccountForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    @Autowired
    UserService userService;

    @Value("${app.maps.defaults.latitude}")
    float defaultLatitude;

    @Value("${app.maps.defaults.longitude}")
    float defaultLongitude;

    @GetMapping("/account")
    public String showAccountPage(Model model) {
        User user = userService.retrieveAuthenticatedUser();
        model.addAttribute("userForm", new UserAccountForm(user));

        return "account";
    }

    @PostMapping("/account")
    public String handleAccountForm(@ModelAttribute UserAccountForm userForm, Model model)
            throws TelegramIsNotDefinedException {
        User user = userService.retrieveAuthenticatedUser();

        // TODO исправить код внизу
        // Saving changes by user
//        user.setMailingPreference(new MailingPreference(userForm.isUseEmail(),
//                userForm.isUseTelegram()));
//        user.setTelegram(userForm.getTelegram());
//
//        // Save user's location only if one changed default location or specified travel time
//        if (user.getTravelOptions() == null) {
//
//        } else {
//            user.setTravelOptions(new TravelOptions());
//        }


        boolean changesSaved = userService.refreshUser(user);
        model.addAttribute("userForm", userForm);
        model.addAttribute("savedSuccessfully", changesSaved);
        return "account";
    }

}
