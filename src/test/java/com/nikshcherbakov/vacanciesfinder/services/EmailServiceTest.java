package com.nikshcherbakov.vacanciesfinder.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceTest {

    @Value("${app.mail.testemail}")
    private String testEmail;

    @Autowired
    private EmailService emailService;

    @Test
    void itSendsEmailsWithHtml() {
        assertDoesNotThrow(() -> {
            String subject = "Test subject";
            String textWithHtml = "<h1>Hello!</h1>\n\n<h3>How are you doing?</h3>";
            emailService.sendMessage(testEmail, subject, textWithHtml, true);
        });
    }
}