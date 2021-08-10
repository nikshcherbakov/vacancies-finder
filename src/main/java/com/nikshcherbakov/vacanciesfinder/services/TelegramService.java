package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.telegram.Bot;
import com.nikshcherbakov.vacanciesfinder.utils.HighlightType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class TelegramService implements IDistributorService {

    private final Bot bot;

    private final VacanciesService vacanciesService;

    public TelegramService(Bot bot, VacanciesService vacanciesService) {
        this.bot = bot;
        this.vacanciesService = vacanciesService;
    }

    /**
     * Sends message to a user by telegram
     * @param to a user to which a message will be sent
     * @param text message text, if null or empty message will not be sent
     * @param parseMode parse mode, null or one of the following values:
     * {@link org.telegram.telegrambots.meta.api.methods.ParseMode#HTML},
     * {@link org.telegram.telegrambots.meta.api.methods.ParseMode#MARKDOWN},
     * {@link org.telegram.telegrambots.meta.api.methods.ParseMode#MARKDOWNV2}
     * @return true if message was sent successfully, false if user's
     * telegram is not active or not set up yet
     * @throws TelegramApiException if an error occurred while sending a message
     */
    public boolean sendMessage(@NotNull User to, String text, String parseMode) throws TelegramApiException {
        if (to.getTelegramSettings() != null) {
            Long chatId = to.getTelegramSettings().getChatId();
            if (chatId != null) {
                if (text != null) {
                    if (!text.isBlank()) {
                        // User's telegram is active
                        SendMessage sendMessageRequest = new SendMessage();
                        sendMessageRequest.setChatId(chatId.toString());
                        sendMessageRequest.setText(text);
                        sendMessageRequest.setDisableWebPagePreview(true);
                        sendMessageRequest.setParseMode(parseMode);

                        // Trying to send a message
                        bot.execute(sendMessageRequest);
                        return true;
                    }
                }
            }
        }
        // User's telegram is not active
        return false;
    }

    /**
     * Sends new found vacancies to a user using telegram if
     * there are vacancies to be sent. If {@link User#getLastJobRequestVacancies()}
     * returns empty or null array, message will not be sent
     * @param to user to which found vacancies will be sent
     * @return true if vacancies were sent, otherwise false
     */
    @Override
    public boolean sendFoundVacancies(@NotNull User to) {
        List<VacancyPreview> foundVacancies = to.getLastJobRequestVacancies();
        if (foundVacancies != null) {
            if (foundVacancies.size() > 0) {
                // Vacancies are found for the user
                String messagePrefix = "Список новых вакансий, найденных специально для Вас:\n\n";

                String vacanciesDescription =
                        vacanciesService.generateVacanciesListMessage(foundVacancies, HighlightType.HTML_TELEGRAM);

                String messageText = !vacanciesDescription.isEmpty() ? messagePrefix + vacanciesDescription : "";
                try {
                    return sendMessage(to, messageText, ParseMode.HTML);
                } catch (TelegramApiException e) {
                    return false;
                }
            }
        }
        return false;
    }

}
