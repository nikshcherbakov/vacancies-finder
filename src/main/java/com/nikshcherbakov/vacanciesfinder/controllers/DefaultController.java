package com.nikshcherbakov.vacanciesfinder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DefaultController {

    @GetMapping(value = "/index")
    public String showIndexPage() {
        return "/index";
    }

    @GetMapping(value = "/login")
    public String showSignIn() {
        return "login";
    }

}
