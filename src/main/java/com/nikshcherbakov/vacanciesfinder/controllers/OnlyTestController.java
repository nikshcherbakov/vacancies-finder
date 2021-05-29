package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.utils.TelegramIsNotDefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// TODO DELETE AFTERWARDS

@Controller
public class OnlyTestController {

    @Autowired
    UserService userService;

    @GetMapping("/testUserService")
    public String testUserService() throws TelegramIsNotDefinedException {

        User testUser = new User("test@test.com", "123456");
        testUser.setTelegram("some_telegram");
        testUser.setSalary(new Salary(10000, "RUB"));

        Location location = new Location(30.0f, 40.0f);
        testUser.setTravelOptions(new TravelOptions(
                location, 15, "public_transport"));

        userService.saveUser(testUser);

        return "successful-confirmation";
    }

}
