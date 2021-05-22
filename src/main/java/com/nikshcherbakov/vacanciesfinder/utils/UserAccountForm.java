package com.nikshcherbakov.vacanciesfinder.utils;

import com.nikshcherbakov.vacanciesfinder.models.User;
import org.springframework.beans.factory.annotation.Value;

public class UserAccountForm {

    @Value("${app.maps.defaults.latitude}")
    String defaultLatitude;

    @Value("${app.maps.defaults.longitude}")
    String defaultLongitude;

    private String username;
    private boolean useEmail;
    private String telegram;
    private boolean useTelegram;
    private float longitude;
    private float latitude;
    private int travelTimeInMins;
    private String travelBy;
    private Integer salaryValue;
    private String currency;

    public UserAccountForm() {
    }

    public UserAccountForm(String username, boolean useEmail, String telegram,
                           boolean useTelegram, float longitude, float latitude,
                           int travelTimeInMins, String travelBy, Integer salaryValue,
                           String currency) {
        this.username = username;
        this.useEmail = useEmail;
        this.telegram = telegram;
        this.useTelegram = useTelegram;
        this.longitude = longitude;
        this.latitude = latitude;
        this.travelTimeInMins = travelTimeInMins;
        this.travelBy = travelBy;
        this.salaryValue = salaryValue;
        this.currency = currency;
    }

    public UserAccountForm(User user) {
        this.username = user.getUsername();
        this.useEmail = user.getMailingPreference().isUseEmail();
        this.telegram = user.getTelegram();
        this.useTelegram = user.getMailingPreference().isUseTelegram();

        if (user.getTravelOptions() != null) {
            this.latitude = user.getTravelOptions().getLocation().getLatitude();
            this.longitude = user.getTravelOptions().getLocation().getLongitude();
            this.travelTimeInMins = user.getTravelOptions().getTravelTimeInMinutes();
            this.travelBy = user.getTravelOptions().getTravelBy();

        } else {
            this.latitude = Float.parseFloat(defaultLatitude);
            this.longitude = Float.parseFloat(defaultLongitude);
            this.travelTimeInMins = 0;
            this.travelBy = "car";
        }

        if (user.getSalary() != null) {
            this.salaryValue = user.getSalary().getValue();
            this.currency = user.getSalary().getCurrency();
        } else {
            this.salaryValue = null;
            this.currency = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUseEmail() {
        return useEmail;
    }

    public void setUseEmail(boolean useEmail) {
        this.useEmail = useEmail;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public boolean isUseTelegram() {
        return useTelegram;
    }

    public void setUseTelegram(boolean useTelegram) {
        this.useTelegram = useTelegram;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Integer getTravelTimeInMins() {
        return travelTimeInMins;
    }

    public void setTravelTimeInMins(Integer travelTimeInMins) {
        this.travelTimeInMins = travelTimeInMins;
    }

    public String getTravelBy() {
        return travelBy;
    }

    public void setTravelBy(String travelBy) {
        this.travelBy = travelBy;
    }

    public Integer getSalaryValue() {
        return salaryValue;
    }

    public void setSalaryValue(Integer salaryValue) {
        this.salaryValue = salaryValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
