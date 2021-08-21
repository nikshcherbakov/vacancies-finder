# Vacancies Finder

[**Русский**](README.md) ![:ru:](https://flagcdn.com/20x15/ru.png) | **English** ![:gb:](https://flagcdn.com/20x15/gb.png)

## Project Description

***Vacancies Finder*** is a project that is going to help people living in CIS to find a job. To do that one has just to visit the Vacancies Finder website, sign up, provide job requirements and the ways to deliver found vacancies. Afterwards, the service will automatically search for available vacancies for the user every day by his requirements and send them using user's mailing preference. The service looks up for vacancies using the most actual and popular Job Search Platform in CIS [hh.ru](https://hh.ru).

The service *sets a user free from permanent visiting job search website*. The application asks a user to provide a job search criteria, consisting of (optionally):
1. Job search filters
2. User's location
3. Travel time from user's location to a future job
4. Wished salary

Then, the application will perform a job search on its own and send the results to the user by the user's mailing preference.

<p align="center">
  <img src="https://user-images.githubusercontent.com/74429654/129240802-c93b4684-2d90-40ec-87dd-7e6878fbb1d9.png" />
</p>

## Major Features

:heavy_check_mark: A user is able to provide *his location (place where user lives on a Google Map)* and *wished time he would like to spend on a road from home to his future job*. [Google Maps Directions Api](https://developers.google.com/maps/documentation/directions/overview) is used to calculate travel time. This way, the service will automatically calculate travel time from a user's location to job's locations and send only those that meet a user's travel time requirement.

:heavy_check_mark: A user's search filters are taken into account while the service is searching for user's vacancies. Eventually, the user will get only the vacancies that have user's search filters in their description.

:heavy_check_mark: Concise and simple vacancies presentation in the *vacancies summary table* on the service website. So, a user can easily compare all found vacancies "face-to-face".

:heavy_check_mark: Ability to like favorite vacancies on the website, so a user will have another favorite vacancies list.

:heavy_check_mark: Telegram bot, sending new found vacancies to mobile devices.

:heavy_check_mark: Возможность выбрать способы рассылки вакансий (Telegram и/или e-mail).

:heavy_check_mark: It is possible to choose mailing preference (Telegram and/or e-mail).

## Technologies Used

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) Spring Framework (Spring Boot, Spring Data, Spring Security)

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) PostgreSQL

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) Thymeleaf

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) jQuery

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) Bootstrap CSS styles

## Used Api

<img width="20" alt="api-point" src="https://user-images.githubusercontent.com/74429654/129253188-07fee198-13fc-49ab-8150-d101e2029b48.png"/> [HeadHunter Api](https://github.com/hhru/api) - for interaction with HeadHunter job search platform (loading actual vacancies by user's filters)

<img width="20" alt="api-point" src="https://user-images.githubusercontent.com/74429654/129253188-07fee198-13fc-49ab-8150-d101e2029b48.png"/> [Google Maps Directions Api](https://developers.google.com/maps/documentation/directions/overview) - for calculating travel time in case a user provided his location and wished travel time (a transport type is also taken into account)

## ER-Diagram

Entities and relationships used in the application are given below


![Vacancies-Finder-ER](https://user-images.githubusercontent.com/74429654/130235491-820266e7-6662-42cf-9594-4fb7e447de80.png)

## User-Service Interactions Scenario

A typical scenario of interaction between the service and a user is described down below

1. A user visits the service website
2. Signs up in the system
3. Waits for a registration confirmation message on his e-mail and confirms his e-mail address by clicking on a link
4. Logs into his account using his login (e-mail) and password, provides his telegram (optional) and mailing preference
5. Specifies his place of residence (optional) by dragging a marker on the map and maximum time he would like to spend on a road to work (time is calculated using Google Directions Api by setting up the route start at 8 a. m.)
6. Specifies his wished salary (optional)
7. Associate his telegram with his Vacancies Finder account
8. Waits for a list of vacancies gathered for him using the information provided from HeadHunter job search platform. The vacancies normally get delivered at 10 a. m. every day. User receives list of vacancies only in case the service found at least one vacancy for him.
9. Likes favorite vacancies or remove the ones he did not like
10. If needed, a user can always change any job search property he specified

![VF-scenario](https://user-images.githubusercontent.com/74429654/130245250-1521c8ee-60e3-4bfc-807c-3ff6f6eacf34.png)

## Some Implementation Details

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Spring Security is user for user authentication. There exist only one role in the service for now (ROLE_USER), however, administrator role could be added in the future.

Yandex SMTP server (smtp.yande.ru) is used for e-mail delivery. For sending e-mails [Spring Java Mail](https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mail.html) is used.

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Vacancies searching procedure is done asynchronically, on separate threads, using transaction mechanism. Vacancies delivery is performed asynchronically as well.

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) [Telegrambots](https://github.com/rubenlagus/TelegramBots) library is used to interact with telegram bot.

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Password changing procedure is also done by sending password changing confirmation message on a user's e-mail.
