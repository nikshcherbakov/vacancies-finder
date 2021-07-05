package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class VacancyPreview {

    @Id
    private Long id;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VacancySalary salary;

    @NotNull
    private String name;

    @NotNull
    @Transient
    private VacancyEmployer vacancyEmployer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VacancySnippet vacancySnippet;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedAt;

    public VacancyPreview() {
    }

    public VacancyPreview(Long id, Address address, VacancySalary salary, String name, VacancyEmployer vacancyEmployer,
                          VacancySnippet vacancySnippet, String publishedAt) throws ParseException {
        this.id = id;
        this.address = address;
        this.salary = salary;
        this.name = name;
        this.vacancyEmployer = vacancyEmployer;
        this.vacancySnippet = vacancySnippet;
        this.publishedAt = convertISO8601ToDate(publishedAt);
    }

    private static Date convertISO8601ToDate(String dateStr) throws ParseException {
        String dateWithoutTimezone = dateStr.substring(0, 10) + dateStr.substring(11, 19);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        return formatter.parse(dateWithoutTimezone);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public VacancySalary getSalary() {
        return salary;
    }

    public void setSalary(VacancySalary salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VacancyEmployer getEmployer() {
        return vacancyEmployer;
    }

    public void setEmployer(VacancyEmployer vacancyEmployer) {
        this.vacancyEmployer = vacancyEmployer;
    }

    public VacancySnippet getSnippet() {
        return vacancySnippet;
    }

    public void setSnippet(VacancySnippet vacancySnippet) {
        this.vacancySnippet = vacancySnippet;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) throws ParseException {
        this.publishedAt = convertISO8601ToDate(publishedAt);
    }
}
