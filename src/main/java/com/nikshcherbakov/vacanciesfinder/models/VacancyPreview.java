package com.nikshcherbakov.vacanciesfinder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
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

        public Builder withUsersVacancies(@Nullable List<UserVacancy> users) {
            vacancyPreview.usersVacancies = users;
            return this;
        }

        public VacancyPreview build() {
            return vacancyPreview;
        }
    }

    @Id
    private Long id;

    @Nullable
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
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
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private VacancyArea vacancyArea;

    @NotNull
    @JsonProperty("employer")
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private VacancyEmployer vacancyEmployer;

    @NotNull
    @JsonProperty("snippet")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VacancySnippet vacancySnippet;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedAt;

    @OneToMany(mappedBy = "vacancy", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<UserVacancy> usersVacancies = new ArrayList<>();

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

    public void setSnippet(VacancySnippet snippet) {
        this.vacancySnippet = snippet;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty("published_at")
    public void setPublishedAt(String publishedAt) throws ParseException {
        this.publishedAt = convertISO8601ToDate(publishedAt);
    }

    public List<UserVacancy> getUsersVacancies() {
        return usersVacancies;
    }

    public void setUsersVacancies(List<UserVacancy> usersVacancies) {
        this.usersVacancies = usersVacancies;
    }

    public void addUserVacancy(UserVacancy user) {
        if (usersVacancies == null) {
            usersVacancies = new ArrayList<>();
        }
        usersVacancies.add(user);
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

    public void setSalary(VacancySalary salary) {
        this.salary = salary;
    }

    private static Date convertISO8601ToDate(String dateStr) throws ParseException {
        String dateWithoutTimezone = dateStr.substring(0, 10) + dateStr.substring(11, 19);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        return formatter.parse(dateWithoutTimezone);
    }

    /**
     * Generates string description of a vacancy in the following
     * format:
     *
     * "VacancyName - VacancyEmployer, VacancyAddress (VacancySalary, optional) - VacancyUrl"
     *
     * @return description of a vacancy
     */
    public String getDescription() {
        return getDescription(false);
    }


    /**
     * Generates string description of a vacancy in the following
     * format:
     *
     * "VacancyName - VacancyEmployer, VacancyAddress (VacancySalary, optional) - VacancyUrl"
     *
     * @param withMarkdown whether insert markdown or not
     * @return vacancy description with or without markdown
     */
    public String getDescription(boolean withMarkdown) {
        String vacancyName = withMarkdown ? String.format("<b>%s</b>", name) : name;
        String employerName = withMarkdown ? String.format("<i>%s</i>", vacancyEmployer.getName()) :
                vacancyEmployer.getName();

        String addressString = vacancyArea.getName();
        if (address != null) {
            if (address.asString() != null) {
                addressString = address.asString();
            }
        }

        String salaryString = null;
        if (salary != null) {
            String from = salary.getFrom() != null ? salary.getFrom().toString() : null;
            String to = salary.getTo() != null ? salary.getTo().toString() : null;
            String currency = salary.getCurrency();

            from = withMarkdown && from != null ? String.format("<b>%s</b>", from) : from;
            to = withMarkdown && to != null ? String.format("<b>%s</b>", to) : to;
            currency = withMarkdown ? String.format("<b>%s</b>", currency) : currency;

            if (from != null && to != null) {
                salaryString = String.format("от %s до %s %s", from, to, currency);
            }
            if (from != null && to == null) {
                salaryString = String.format("от %s %s", from, currency);
            }
            if (from == null && to != null) {
                salaryString = String.format("до %s %s", to, currency);
            }
        }

        return String.format("%s - %s, %s", vacancyName, employerName, addressString) +
                (salaryString != null ? String.format(" (%s)", salaryString) : "") + String.format(" - %s", url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyPreview that = (VacancyPreview) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
