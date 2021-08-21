package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.Feedback;
import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedbackController {

    private final UserService userService;

    public FeedbackController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/feedback")
    public String showFeedbackPage() {
        return "feedback";
    }

    @PostMapping(value = "/feedback")
    public String handleFeedback(@RequestParam String feedbackText) {
        User user = userService.retrieveAuthenticatedUser();
        user.addFeedback(new Feedback(feedbackText));
        userService.save(user);
        return "thanks-for-feedback";
    }

}
