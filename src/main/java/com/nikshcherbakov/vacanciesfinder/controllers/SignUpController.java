package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class SignUpController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping(value = "/signup")
    public String showSignUp(Model model) {
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

        // Getting user credentials
        String username = user.getUsername();
        String password = user.getPassword(); // original password is needed because bCrypt is one-way coder

        if (!userService.saveUser(user)) {
            // User already exists in the database
            model.addAttribute("userExists", true);
            model.addAttribute("user", user);
            return "signup";
        }

        // Authenticate user after successful registration
        authenticateUserAndSetSession(username, password, request);

        return "redirect:/index"; // todo CHANGE_AFTER
    }

    private void authenticateUserAndSetSession(String username, String password,
                                               HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // Generate session if one doesn't exist
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

    }

}
