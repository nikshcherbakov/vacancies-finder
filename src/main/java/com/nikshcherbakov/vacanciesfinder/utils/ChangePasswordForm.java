package com.nikshcherbakov.vacanciesfinder.utils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuppressWarnings("unused")
public class ChangePasswordForm {

    @NotNull
    @Size(min = 6, max = 72)
    private String password;

    @NotNull
    private String passwordConfirm;

    @NotNull
    private String username;

    @NotNull
    private String hashValue;

    public ChangePasswordForm(@NotNull String username, @NotNull String hashValue) {
        this.username = username;
        this.hashValue = hashValue;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
}
