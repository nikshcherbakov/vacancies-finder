package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        model.addAttribute("user", user);
        System.out.println(user);
        model.addAttribute("useEmail", user.getMailingPreference().isUseEmail());
        model.addAttribute("useTelegram", user.getMailingPreference().isUseTelegram());

        if (user.getTravelOptions() != null) {
            model.addAttribute("travelTimeInMins",
                    user.getTravelOptions().getTravelTimeInMinutes());
            model.addAttribute("travelBy",
                    user.getTravelOptions().getTravelBy());

            model.addAttribute("latitude",
                    user.getTravelOptions().getLocation().getLatitude());
            model.addAttribute("longitude",
                    user.getTravelOptions().getLocation().getLongitude());
        } else {
            model.addAttribute("latitude", defaultLatitude);
            model.addAttribute("longitude", defaultLongitude);
        }

        if (user.getSalary() != null) {
            model.addAttribute("salaryValue", user.getSalary().getValue());
            model.addAttribute("salaryCurrency", user.getSalary().getCurrency());
        }

        return "account";
    }

    // TODO MAIN_PRIORITY add saving user changes
//    @PostMapping("/account")
//    public String handleAccountForm() {
//
//    }

}
