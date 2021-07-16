package com.nikshcherbakov.vacanciesfinder.utils;

public class UserAccountForm {

    private String username;
    private boolean useEmail;
    private String telegram;
    private boolean useTelegram;
    private double longitude;
    private double latitude;
    private int travelTimeInMins;
    private String travelBy;
    private Integer salaryValue;
    private String currency;
    private String searchFilters;

    public UserAccountForm(String username, boolean useEmail, String telegram,
                           boolean useTelegram, double longitude, double latitude,
                           int travelTimeInMins, String travelBy, Integer salaryValue,
                           String currency, String searchFilters) {
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
        this.searchFilters = searchFilters;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
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

    public void setTravelTimeInMins(int travelTimeInMins) {
        this.travelTimeInMins = travelTimeInMins;
    }

    public String getSearchFilters() {
        return searchFilters;
    }

    public void setSearchFilters(String searchFilters) {
        this.searchFilters = searchFilters;
    }
}
