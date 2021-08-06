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

    public static class Builder {
        private final VacancyPreview vacancyPreview;

        public Builder() {
            vacancyPreview = new VacancyPreview();
        }

        public Builder withId(@NotNull Long id) {
            vacancyPreview.id = id;
            return this;
        }

        public Builder withAddress(@Nullable Address address) {
            vacancyPreview.address = address;
            return this;
        }

        public Builder withSalary(@Nullable VacancySalary salary) {
            vacancyPreview.salary = salary;
            return this;
        }

        public Builder withName(@NotNull String name) {
            vacancyPreview.name = name;
            return this;
        }

        public Builder withArea(@NotNull VacancyArea area) {
            vacancyPreview.vacancyArea = area;
            return this;
        }

        public Builder withEmployer(@NotNull VacancyEmployer employer) {
            vacancyPreview.vacancyEmployer = employer;
            return this;
        }

        public Builder withVacancySnippet(@NotNull VacancySnippet snippet) {
            vacancyPreview.vacancySnippet = snippet;
            return this;
        }

        public Builder withPublishedAt(@NotNull Date publishedAt) {
            vacancyPreview.publishedAt = publishedAt;
            return this;
        }

        public Builder withUsers(@Nullable Set<User> users) {
            vacancyPreview.users = users;
            return this;
        }

        public VacancyPreview build() {
            return vacancyPreview;
        }
    }

    @Id
    @NotNull
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
    @JsonProperty("alternate_url")
    private String url;

    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private VacancyArea vacancyArea;

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

    public VacancyArea getArea() {
        return vacancyArea;
    }

    public void setArea(VacancyArea vacancyArea) {
        this.vacancyArea = vacancyArea;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private static Date convertISO8601ToDate(String dateStr) throws ParseException {
        String dateWithoutTimezone = dateStr.substring(0, 10) + dateStr.substring(11, 19);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        return formatter.parse(dateWithoutTimezone);
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
