package com.nikshcherbakov.vacanciesfinder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VacanciesController {

    @GetMapping("/vacancies")
    public String showVacanciesPage(Model model) {
        // TODO MAIN_PRIORITY 1. Продумать отображение вакансий в виде таблицы
        // TODO MAIN_PRIORITY 2. Реазизовать getmapping на /vacancies и отображение найденных вакансий
        return "vacancies";
    }

    // TODO MAIN_PRIORITY 3. Реализовать getmapping на /vacancies/favorites

}
