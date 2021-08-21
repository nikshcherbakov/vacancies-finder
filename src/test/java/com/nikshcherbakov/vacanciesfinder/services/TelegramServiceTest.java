package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.TelegramSettings;
import com.nikshcherbakov.vacanciesfinder.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TelegramServiceTest {

    @Value("${app.telegram.test.chatid}")
    private Long testChatId;

    @Autowired
    private TelegramService telegramService;

    @Test
    void itShouldSendMessageByTelegram() {
        // Case 1
        User testUser = new User("test@test.com", "password");
        TelegramSettings telegramSettings = new TelegramSettings(testUser, "example_telegram");
        telegramSettings.setChatId(testChatId);
        testUser.setTelegramSettings(telegramSettings);

        assertDoesNotThrow(() -> {
            boolean sentSuccessfully = telegramService.sendMessage(testUser, "Test message", null);
            assertTrue(sentSuccessfully);
        });

        // Case 2
        assertDoesNotThrow(() -> {
            boolean sentSuccessfully = telegramService.sendMessage(testUser, "   ", null);
            assertFalse(sentSuccessfully);
        });

        // Case 3
        assertDoesNotThrow(() -> {
            boolean sentSuccessfully = telegramService.sendMessage(testUser, "<i>Test message with markdown</i>",
                    ParseMode.HTML);
            assertTrue(sentSuccessfully);
        });
    }
}