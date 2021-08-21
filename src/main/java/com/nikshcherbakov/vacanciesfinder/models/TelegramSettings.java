package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import javax.persistence.*;

@Entity
public class TelegramSettings {
    @Id
    private Long id;

    @NotNull
    @Column(unique = true)
    private String telegram;

    @Nullable
    private Long chatId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    public TelegramSettings() {
    }

    public TelegramSettings(User user, String telegram) {
        this.telegram = telegram;
        this.chatId = null;
        this.user = user;
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
