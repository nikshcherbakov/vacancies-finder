package com.nikshcherbakov.vacanciesfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


// TODO GENERAL изменить классы в логгерах на соответствующие
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class VacanciesFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacanciesFinderApplication.class, args);
    }

}
