# Vacancies Finder

**Русский** ![:ru:](https://flagcdn.com/20x15/ru.png) | [**English**](docs-eng/README.md) ![:gb:](https://flagcdn.com/20x15/gb.png)

## Описание проекта

***Vacancies Finder*** - проект, который упростит людям поиск работы на территории СНГ. Для этого, пользователю требуется лишь зайти на сайт vacancies finder, зарегистрироваться, указать свои требования к вакансиям и методы уведомления. Затем сервис ежедневно будет самостоятельно подбирать вакансии по его требованиям и высылать их пользователю указанным способом (способами). Сервис подбирает вакансии, используя наиболее актуальную на текущий момент платформу по поиску работы [hh.ru](https://hh.ru).

Сервис *освобождает пользователя от постоянного посещения сайта по поиску вакансий*. Приложение запрашивает у пользователя информацию для поиска вакансий (опционально):
1. Пользовательские фильтры
2. Местоположение пользователя
3. Желаемое время в пути до работы
4. Пожелания по зарплате

Впоследствии приложение будет самостоятельно осуществлять поиск вакансий и их рассылку пользователям указанным способом.

<p align="center">
  <img src="https://user-images.githubusercontent.com/74429654/129240802-c93b4684-2d90-40ec-87dd-7e6878fbb1d9.png" />
</p>

## Основные особенности

:heavy_check_mark: Пользователь может *указать свое местоположение* и *задавать желаемое время в пути до места работы*. Для расчета времени в пути используется [Google Maps Directions Api](https://developers.google.com/maps/documentation/directions/overview). Так, сервис будет автоматически рассчитывать время в пути от заданного местоположения до местоположения вакансий и высылать только те из них, которые удовлетворяют указанным требованиям.

:heavy_check_mark: При подборе вакансий учитываются *пользовательские фильтры*. В итоге, пользователю будут высланы только те вакансии, которые содержат в описании указанные фильтры.

:heavy_check_mark: Удобное и краткое представление найденных вакансий в *сводной таблице* на сайте сервиса. Так, пользователь наглядно на одной странице может увидеть и сравнить подходящие ему вакансии "лицом к лицу".

:heavy_check_mark: Отдельный список с *понравившимися вакансиями*, которые выбирает пользователь на сайте.

:heavy_check_mark: Telegram-бот, позволяющий получать уведомления о новых актуальных вакансиях на мобильные устройства.

:heavy_check_mark: Возможность выбрать способы рассылки вакансий (Telegram и/или e-mail).

## Используемые технологии

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) Spring Framework (Spring Boot, Spring Data, Spring Security)

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) PostgreSQL

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) Thymeleaf

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) jQuery

![gears](https://user-images.githubusercontent.com/74429654/129231585-78765455-d571-422d-ba9e-3a384c81cdcc.png) Стили Bootstrap

## Используемые Api

<img width="20" alt="api-point" src="https://user-images.githubusercontent.com/74429654/129253188-07fee198-13fc-49ab-8150-d101e2029b48.png"/> [HeadHunter Api](https://github.com/hhru/api) - для взаимодействия с одноименной платформой по поиску вакансий (загрузка актуальной базы вакансий по пользовательским фильтрам)

<img width="20" alt="api-point" src="https://user-images.githubusercontent.com/74429654/129253188-07fee198-13fc-49ab-8150-d101e2029b48.png"/> [Google Maps Directions Api](https://developers.google.com/maps/documentation/directions/overview) - для поиска времени в пути на заданном виде транспорта при указании места жительства и желаемого времени в пути

## ER-диаграмма

Ниже на схеме представлены сущности, используемые в приложении и их связи


![Vacancies-Finder-ER](https://user-images.githubusercontent.com/74429654/130314613-78c84ed6-99cc-4505-83c2-45dadcdeb4c7.png)

## Сценарий взаимодействия с пользователем

Ниже описан типичный сценарий взаимодействия пользователя с сервисом

1. Пользователь заходит на сайт сервиса Vacancies Finder
2. Проходит процедуру регистрации
3. Ожидает подтверждения регистрации, переходит по ссылке в письме и подтверждает свой e-mail адрес (логин)
4. Заходит в личный кабинет по своему логину и паролю, указывает свой телеграм (опционально) и способы рассылки новых вакансий
5. Указывает место жительства на карте (опционально) и время, которое он хотел бы тратить на дорогу до работы с утра указанным способом (время по статистике трафика в 8 часов утра с помощью Google Directions Api)
6. Указывает свои пожелания по зарплате (опционально)
7. Проходит процедуру привязки Telegram к своему аккаунту Vacancies Finder
8. Ожидает ежедневной рассылки актульных вакансий по пользовательским фильтрам с платформы HeadHunter на e-mail и/или telegram (приходят ежедневно в 10 утра)
9. На сайте сервиса пользователь может отменить понравившиеся ему вакансии, либо удалить неподходящие
10. При необходимости пользователь может в любой момент изменить свои пользовательские фильтры

![VF-scenario](https://user-images.githubusercontent.com/74429654/130245250-1521c8ee-60e3-4bfc-807c-3ff6f6eacf34.png)

## Некоторые детали реализации

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Для аутентификации пользователя используется Spring Security. В проекте реализована одна роль (ROLE_USER), однако, в дальнейшем может быть добавлена роль администратора.

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Для рассылки по e-mail используется SMTP сервер Яндекса (smtp.yandex.ru). Для отправки электронных писем используется [Spring Java Mail](https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mail.html).

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Поиск актуальных вакансий осуществляется асинхронно, с использованием транзакций, рассылка найденных вакансий пользователям сервиса также производится асинхронно.

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Для работы с Telegram ботом используется библиотека [Telegrambots](https://github.com/rubenlagus/TelegramBots).

![detail-icon-16](https://user-images.githubusercontent.com/74429654/130313016-57a118cd-83b8-4c1a-893b-94cbb82650e5.png) Смена пароля также осуществляется через отправку письма на почту пользователя со ссылкой на подтверждение смены пароля.
