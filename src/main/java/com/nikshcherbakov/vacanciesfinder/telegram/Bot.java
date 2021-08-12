package com.nikshcherbakov.vacanciesfinder.telegram;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.TelegramSettings;
import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.repositories.UserRepository;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.services.VacanciesService;
import com.nikshcherbakov.vacanciesfinder.utils.HighlightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${app.telegram.bot.token}")
    private String botToken;

    @Value("${app.telegram.bot.name}")
    private String botName;

    private static final Logger logger = LoggerFactory.getLogger(VacanciesFinderApplication.class);

    private final UserService userService;
    private final VacanciesService vacanciesService;

    private final UserRepository userRepository;

    public Bot(UserService userService, UserRepository userRepository, VacanciesService vacanciesService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.vacanciesService = vacanciesService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            try {
                String messageFrom = update.getMessage().getFrom().getUserName();
                String messageText = update.getMessage().getText();

                Long chatId = update.getMessage().getChatId();

                String text;
                User userByChatId = userService.findUserByChatId(chatId);
                switch (messageText) {
                    case "/start":
                        if (!isTelegramTaken(messageFrom)) {
                            // User is not registered for telegram so far
                            text = "Здравствуйте! Я телеграм бот, который будет регулярно высылать Вам " +
                                    "список найденных для Вас вакансий. Для аутентификации, пожалуйста, укажите " +
                                    "e-mail адрес, который вы указывали при регистрации в системе Vacancies Finder.";
                        } else {
                            // Active telegram user writes '/start' again
                            text = String.format("Данный Telegram аккаунт уже привязан к пользователю %s. " +
                                            "Чтобы привязать другого пользователя, пожалуйста, измените идентификатор " +
                                            "Telegram в вашем аккаунте на сайте Vacancies Finder.",
                                    userService.findUserByActiveTelegram(messageFrom).getUsername());
                        }
                        sendMessage(chatId, text);
                        break;

                    case "/help":
                        text = "Я телеграм бот, предназначенный для регулярной рассылки найденных вакансий на сервисе " +
                                "Vacancies Finder. Ежедневно я буду стараться радовать Вас свежими предложениями о " +
                                "работе, подобранными специально по вашим фильтрам, указанным в личном кабинете " +
                                "Vacancies Finder.\n\n" +
                                "Вот список доступных команд\n" +
                                "/favorites - вывести список понравившихся вакансий\n" +
                                "/searchfilters - вывести указанные поисковые фильтры";
                        sendMessage(chatId, text);
                        break;

                    case "/favorites":
                        if (userByChatId != null) {
                            // User is registered
                            List<VacancyPreview> favoriteVacancies = userByChatId.getFavoriteVacancies();
                            StringBuilder builder = new StringBuilder("Вот список понравившихся Вам вакансий:\n\n");

                            String favoriteVacanciesDescription = vacanciesService.generateVacanciesListMessage(
                                    favoriteVacancies,
                                    HighlightType.HTML_TELEGRAM);

                            builder.append(favoriteVacanciesDescription);

                            text = !favoriteVacanciesDescription.equals("") ? builder.toString() :
                                    "Список понравившихся Вам вакансий пока что пуст. Для добавления вакансий " +
                                            "воспользуйтесь сайтом Vacancies Finder.";
                            sendMessage(chatId, text, true);
                        } else {
                            // User is not registered
                            text = "Ваш Telegram не зарегистрирован в системе. Пожалуйста, " +
                                    "проверьте Ваш Telegram в личном кабинете на сайте Vacancies Finder и введите " +
                                    "e-mail повторно";
                            sendMessage(chatId, text);
                        }
                        break;

                    case "/searchfilters":
                        if (userByChatId != null) {
                            // User is registered
                            String searchFiltersList =
                                    userByChatId.getSearchFiltersListMessage(true);
                            text = !searchFiltersList.isEmpty() ?
                                    "Вот список Ваших фильтров для поиска вакансий:\n\n" + searchFiltersList :
                                    "Список Ваших поисковых фильтров пуст";
                            text += "\n\nДля изменения поисковых фильтров, пожалуйста, воспользуйтесь " +
                                    "сайтом Vacancies Finder.";
                            sendMessage(chatId, text, true);
                        } else {
                            // User is not registered
                            text = "Ваш Telegram не зарегистрирован в системе. Пожалуйста, " +
                                    "проверьте Ваш Telegram в личном кабинете на сайте Vacancies Finder и введите " +
                                    "e-mail повторно";
                            sendMessage(chatId, text);
                        }
                        break;


                    default:
                        // User entered a non-command
                        if (userByChatId == null) {
                            // This telegram is not active yet
                            boolean telegramIsActivated = activateTelegram(messageText, messageFrom, chatId);

                            if (telegramIsActivated) {
                                text = String.format("Ваш Telegram был успешно привязан к пользователю %s. " +
                                                "Чтобы узнать, что я могу, используйте /help.", messageText);
                            } else {
                                // User's telegram does not match user's username
                                text = "Указанный e-mail не соответствует Вашему Telegram. Пожалуйста, " +
                                        "проверьте Ваш Telegram в личном кабинете на сайте Vacancies Finder и введите " +
                                        "e-mail повторно";
                            }

                        } else {
                            // Telegram is active already
                            text = "Простите, не понимаю Вас. Для просмотра списка команд, пожалуйста, " +
                                    "воспользуйтесь /help.";
                        }
                        sendMessage(chatId, text);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
                logger.error(String.format("Error handling message by user %s",
                        update.getMessage().getFrom().getUserName()));
            }

        }
    }

    private boolean activateTelegram(String username, String messageFrom, Long chatId) {
        User userByUsername = userService.findUserByUsername(username);
        if (userByUsername != null) {
            TelegramSettings userTelegramSettings = userByUsername.getTelegramSettings();

            // Checking if a user specified telegram settings
            if (userTelegramSettings != null) {
                if (userTelegramSettings.getTelegram().equals(messageFrom)) {
                    // User's telegram matches user's username - updating user's telegram settings
                    userByUsername.getTelegramSettings().setChatId(chatId);
                    userRepository.save(userByUsername);
                    return true;
                }
            }
        }
        return false;
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        sendMessage(chatId, text, false);
    }

    private void sendMessage(Long chatId, String text, boolean withMarkdown) throws TelegramApiException {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(chatId.toString());
        sendMessageRequest.setText(text);
        sendMessageRequest.setDisableWebPagePreview(true);
        if (withMarkdown) {
            sendMessageRequest.setParseMode(ParseMode.HTML);
        }

        execute(sendMessageRequest);
    }

    private boolean isTelegramTaken(@NotNull String telegram) {
        return userService.findUserByActiveTelegram(telegram) != null;
    }

}
