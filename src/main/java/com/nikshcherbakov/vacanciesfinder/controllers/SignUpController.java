package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.MailingPreference;
import com.nikshcherbakov.vacanciesfinder.models.Role;
import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.repositories.UserRepository;
import com.nikshcherbakov.vacanciesfinder.services.EmailService;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@Controller
public class SignUpController {

    private final UserService userService;

    private final EmailService emailService;

    private final UserRepository userRepository;

    public SignUpController(UserService userService, EmailService emailService, UserRepository userRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/signup")
    public String showSignUp(Model model) {
        // Checking if a user is authenticated already
        if (userService.isUserAuthenticated()) {
            return "redirect:/account";
        }

        model.addAttribute("user", new User());
        return "/signup";
    }

    @PostMapping(value = "/signup")
    public String handleSignUp(@Valid User user, BindingResult bindingResult, Model model) {

        // Checking if a form does not contain errors
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // Checking if user.password and user.passwordConfirm are equal
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordDoesNotMatchPasswordConfirm", true);
            model.addAttribute("user", user);
            return "signup";
        }

        // Giving a user ROLE_USER by default
        user.setRoles(Collections.singleton(new Role("ROLE_USER")));

        // By default user gets notifications only by email
        user.setMailingPreference(new MailingPreference(true, false));

        if (!userService.saveNewUser(user)) {
            // User already exists in the database
            model.addAttribute("userExists", true);
            model.addAttribute("user", user);
            return "signup";
        }

        // Sending a confirmation message to a user by email
        emailService.sendConfirmMessage(user);

        return "confirmation-sent";
    }

    @GetMapping("/accountconfirm")
    String confirmUserByEmail(@RequestParam(name = "user") String username,
                              @RequestParam(name = "hashval") String hash) {
        // Handling incorrect URLs
        if (username == null || hash == null) {
            return "403";
        }

        Optional<User> userFromDb = userRepository.findByUsername(username);

        if (userFromDb.isPresent()) {
            // User exists
            User user = userFromDb.get();
            if (user.getPassword().equals(hash) && !user.isEnabled()) {
                user.setEnabled(true);
                userRepository.save(user);
                return "successful-confirmation";
            } else {
                return "403";
            }
        } else {
            // No such user in the database
            return "403";
        }

    }

}
