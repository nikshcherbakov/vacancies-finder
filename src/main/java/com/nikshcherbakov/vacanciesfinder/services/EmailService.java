package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.VacanciesFinderApplication;
import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.utils.HighlightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class EmailService implements IDistributorService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.urls.basicurl}")
    private String basicUrl;

    @Value("${app.urls.protocol}")
    private String protocol;

    private static final Logger logger = LoggerFactory.getLogger(VacanciesFinderApplication.class);

    private final JavaMailSender mailSender;

    private final VacanciesService vacanciesService;

    public EmailService(JavaMailSender mailSender, VacanciesService vacanciesService) {
        this.mailSender = mailSender;
        this.vacanciesService = vacanciesService;
    }

    /**
     * Sends registration confirmation message to a user by email
     * @param to a user to which message will be sent
     */
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

        // Sending message
        String subject = "Vacancies Finder - Подтверждение регистрации";
        try {
            sendMessage(to.getUsername(), subject, messageText);
        } catch (MessagingException e) {
            logger.error(String.format("Error occurred while sending confirmation e-mail to %s", to.getUsername()));
        }

    }

    /** Sends an email
     * @param to email of an email receiver
     * @param subject subject of an email
     * @param text text of an email message, if null or empty
     *             message will not be sent
     * @throws MessagingException if error while working on email is registered
     */
    public void sendMessage(@NotNull String to, @NotNull String subject, String text) throws MessagingException {
        sendMessage(to, subject, text, false);
    }

    /** Sends an email
     * @param to email of an email receiver
     * @param subject subject of an email
     * @param text text of an email message, if null or empty
     *             message will not be sent
     * @param withHtml whether to use html in the email
     * @throws MessagingException if error while working on email is registered
     */
    public void sendMessage(@NotNull String to, @NotNull String subject, String text, boolean withHtml)
            throws MessagingException {
        if (!to.isBlank() && !subject.isBlank()) {
            if (text != null) {
                if (!text.isBlank()) {
                    if (!withHtml) {
                        // Creating a new simple message
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(to);
                        message.setFrom(from);
                        message.setSubject(subject);
                        message.setText(text);

                        // Sending message
                        mailSender.send(message);
                    } else {
                        // Creating mime message
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
                        helper.setTo(to);
                        helper.setFrom(from);
                        helper.setSubject(subject);
                        helper.setText(text, true);

                        // Sending message
                        mailSender.send(message);
                    }
                }
            }
        }
    }

    /**
     * Sends new found vacancies to a user by email if
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

                String vacanciesDescription =
                        vacanciesService.generateVacanciesListMessage(foundVacancies, HighlightType.HTML_EMAIL);

                // Sending message
                String htmlTemplate = "<html>\n" +
                        "<h2>Добрый день, это Vacancies Finder!</h2>\n" +
                        "<h4>Список новых вакансий, найденных специально для Вас:</h4>\n" +
                        "%s\n" +
                        "<h4>Спасибо за то, что пользуетесь нашим сервисом!</h4>\n" +
                        "</html>";

                String messageSubject = "Vacancies Finder - Найденные вакансии";
                String messageText = !vacanciesDescription.isEmpty() ?
                        String.format(htmlTemplate, vacanciesDescription) : "";
                try {
                    sendMessage(to.getUsername(), messageSubject, messageText, true);
                } catch (MessagingException e) {
                    logger.error(String.format("Error occurred while sending found vacancies by email to %s",
                            to.getUsername()));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a user instructions how to change password by email
     * @param user a user to which the instructions are going to be sent
     */
    public void sendChangePasswordMessage(User user) {
        String userEmail = user.getUsername();
        String serviceUrl = String.format("%s://%s", protocol, basicUrl);
        String changePasswordConfirmUrl =
                String.format("%s/changePassword?user=%s&hash=%s", serviceUrl, userEmail, user.getPassword());

        String subject = "Vacancies Finder - Смена пароля";
        String text = "Здравствуйте, это Vacancies Finder!\n" +
                "\n" +
                "С вашего аккаунта поступил запрос на смену пароля. Для подтвержения смены пароля, пожалуйста, " +
                String.format("перейдите по ссылке %s.\n", changePasswordConfirmUrl) +
                "\n" +
                "С уважением, команда Vacancies Finder.";
        try {
            sendMessage(userEmail, subject, text);
        } catch (MessagingException e) {
            logger.error(String.format("Error occurred while sending changing " +
                    "password confirmation message to a user %s", userEmail));
        }
    }
}
