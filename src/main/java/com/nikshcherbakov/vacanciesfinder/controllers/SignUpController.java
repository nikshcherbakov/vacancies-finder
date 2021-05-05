package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.repositories.UserRepository;
import com.nikshcherbakov.vacanciesfinder.services.MailService;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class SignUpController {

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/signup")
    public String showSignUp(Model model) {
        // Checking if a user is authenticated already
        if (isUserAuthenticated()) {
            return "redirect:/account";
        }

        model.addAttribute("user", new User());
        return "/signup";
    }

    @PostMapping(value = "/signup")
    public String handleSignUp(@Valid User user, BindingResult bindingResult,
                               Model model, HttpServletRequest request) {
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

        // Getting user credentials
        String username = user.getUsername();
        String password = user.getPassword(); // original password is needed because bCrypt is one-way coder

        if (!userService.saveUser(user)) {
            // User already exists in the database
            model.addAttribute("userExists", true);
            model.addAttribute("user", user);
            return "signup";
        }

        // Sending a confirmation message to a user by email
        mailService.sendConfirmMessage(user);

        return "confirmation-sent";
    }

    @GetMapping("/accountconfirm")
    String confirmUserByEmail(@RequestParam(name = "user") String username,
                              @RequestParam(name = "hashval") String hash) {
        // Handling incorrect URLs
        if (username == null || hash == null) {
            return "403";
        }

        User user = userRepository.findByUsername(username);

        if (user != null) {
            // User exists
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

    private boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

}
