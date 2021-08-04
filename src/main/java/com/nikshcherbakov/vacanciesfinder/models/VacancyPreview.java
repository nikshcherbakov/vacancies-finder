package com.nikshcherbakov.vacanciesfinder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class VacancyPreview {

    @Id
    private Long id;

    @Nullable
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Address address;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VacancySalary salary;

    @NotNull
    private String name;

    @NotNull
    @JsonProperty("employer")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private VacancyEmployer vacancyEmployer;

    @NotNull
    @JsonProperty("snippet")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VacancySnippet vacancySnippet;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedAt;

    @ManyToMany(mappedBy = "vacancies", fetch = FetchType.EAGER)
    private Set<User> users;

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

    public Date getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty("published_at")
    public void setPublishedAt(String publishedAt) throws ParseException {
        this.publishedAt = convertISO8601ToDate(publishedAt);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyPreview that = (VacancyPreview) o;
        return id.equals(that.id) && Objects.equals(address, that.address) && Objects.equals(salary, that.salary) &&
                name.equals(that.name) && Objects.equals(vacancyEmployer, that.vacancyEmployer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, salary, name, vacancyEmployer);
    }
}
