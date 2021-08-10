package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import javax.persistence.*;

@Entity
public class TelegramSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String telegram;

    @Nullable
    private Long chatId;

    @OneToOne(mappedBy = "telegramSettings")
    private User user;

    public TelegramSettings() {
    }

    public TelegramSettings(String telegram) {
        this.telegram = telegram;
        this.chatId = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
