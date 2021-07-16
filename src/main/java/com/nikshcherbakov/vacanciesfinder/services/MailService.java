package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final MailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String from;

    public void sendConfirmMessage(User to) {
        String confirmURL = String.format("http://localhost:8080/accountconfirm?user=%s&hashval=%s",
                to.getUsername(), to.getPassword());

        String messageText = String.format("Здравствуйте!\n" +
                "\n" +
                "Ваш e-mail адрес был использован в качестве адреса для регистрации в веб-сервисе " +
                "Vacancies Finder. Для подтверждения регистрации просто перейдите по следующей ссылке:\n" +
                "\n" +
                "%s\n" +
                "\n" +
                "Внимание! Если Вы не выполняли регистрацию в сервисе Vacancies Finder - " +
                "не переходите по ссылке и не отвечайте на это письмо.\n" +
                "\n" +
                "С наилучшими пожеланиями, команда Vacancies Finder.", confirmURL);

        // Creating a new message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to.getUsername());
        message.setFrom(from);
        message.setSubject("Vacancies Finder - Подтверждение регистрации");
        message.setText(messageText);

        // Sending message
        mailSender.send(message);
    }

}
