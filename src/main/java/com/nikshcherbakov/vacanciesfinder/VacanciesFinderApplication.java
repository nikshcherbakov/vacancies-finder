package com.nikshcherbakov.vacanciesfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO MAIN_PRIORITY Добавить Location Picker на страницу /account
//  подобно https://codepen.io/jayawiratha/pen/mEQLVd только
//  для Longitude и Latitude использовать скрыте поля как в
//  http://htmlbook.ru/samhtml5/formy/skrytoe-pole
//  Возможно, добавить стилизацию карты как в видео https://www.youtube.com/watch?v=AgwhiHLu2H4

@SpringBootApplication
public class VacanciesFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacanciesFinderApplication.class, args);
    }

}
