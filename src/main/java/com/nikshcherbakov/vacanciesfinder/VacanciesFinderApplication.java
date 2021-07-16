package com.nikshcherbakov.vacanciesfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VacanciesFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacanciesFinderApplication.class, args);
    }

}
