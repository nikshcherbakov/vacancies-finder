package com.nikshcherbakov.vacanciesfinder.configs;

import com.nikshcherbakov.vacanciesfinder.telegram.Bot;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final Bot bot;

    public TelegramBotConfig(Bot bot) {
        this.bot = bot;
    }

    @EventListener({ApplicationReadyEvent.class})
    public void initTelegramBot() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
    }
}
