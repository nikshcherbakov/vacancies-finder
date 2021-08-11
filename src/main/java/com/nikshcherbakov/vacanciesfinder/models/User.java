package com.nikshcherbakov.vacanciesfinder.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

// TODO Разбраться как работает BCryptEncoder, установить корректный максимальный размер для пароля

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 5, max = 25)
    private String username;

    @Column(nullable = false)
    @NotNull
    @Size(min = 6, max = 500)
    private String password;

    @Transient
    private String passwordConfirm;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private MailingPreference mailingPreference;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TelegramSettings telegramSettings;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TravelOptions travelOptions;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Salary salary;

    private String searchFilters;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastJobRequestDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VacancyPreview> vacancies;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VacancyPreview> favoriteVacancies;

    @Transient
    private List<VacancyPreview> lastJobRequestVacancies;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "feedback")
    private List<Feedback> feedbackList;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;

        // Account is disabled by default
        this.enabled = false;

        // Telegram is optional
        this.telegramSettings = null;

        // Travel options are not defined by default
        this.travelOptions = null;

        // Salary is not defined by default
        this.salary = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public MailingPreference getMailingPreference() {
        return mailingPreference;
    }

    public void setMailingPreference(MailingPreference mailingPreference) {
        this.mailingPreference = mailingPreference;
    }

    public TelegramSettings getTelegramSettings() {
        return telegramSettings;
    }

    public void setTelegramSettings(TelegramSettings telegramSettings) {
        this.telegramSettings = telegramSettings;
    }

    public TravelOptions getTravelOptions() {
        return travelOptions;
    }

    public void setTravelOptions(TravelOptions travelOptions) {
        this.travelOptions = travelOptions;
    }

    public Salary getSalary() {
        return salary;
    }

    public void setSalary(Salary salary) {
        this.salary = salary;
    }

    public String getSearchFilters() {
        return searchFilters;
    }

    public void setSearchFilters(String searchFilters) {
        this.searchFilters = searchFilters;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastJobRequestDate() {
        return lastJobRequestDate;
    }

    public void setLastJobRequestDate(Date lastJobRequestDate) {
        this.lastJobRequestDate = lastJobRequestDate;
    }

    public List<VacancyPreview> getVacancies() {
        return vacancies;
    }

    public void setVacancies(List<VacancyPreview> vacancies) {
        this.vacancies = vacancies;
    }

    public void addVacancy(VacancyPreview vacancy) {
        if (vacancies == null) {
            vacancies = new ArrayList<>();
        }
        vacancies.add(vacancy);
    }

    public void addVacancies(Collection<VacancyPreview> vacancies) {
        if (vacancies == null) {
            return;
        }
        if (this.vacancies == null) {
            this.vacancies = new ArrayList<>();
        }
        this.vacancies.addAll(vacancies);
    }

    public void removeVacancy(VacancyPreview vacancy) {
        if (vacancies == null) {
            return;
        }
        vacancies.remove(vacancy);
    }

    public List<VacancyPreview> getFavoriteVacancies() {
        return favoriteVacancies;
    }

    public void setFavoriteVacancies(List<VacancyPreview> favoriteVacancies) {
        this.favoriteVacancies = favoriteVacancies;
    }

    public void addFavoriteVacancy(VacancyPreview vacancy) {
        if (vacancy == null) {
            return;
        }
        if (favoriteVacancies == null) {
            favoriteVacancies = new ArrayList<>();
        }
        favoriteVacancies.add(vacancy);
    }

    public void addFavoriteVacancies(Collection<VacancyPreview> vacancies) {
        if (vacancies == null) {
            return;
        }
        if (favoriteVacancies == null) {
            favoriteVacancies = new ArrayList<>();
        }
        favoriteVacancies.addAll(vacancies);
    }

    public void removeFavoriteVacancy(VacancyPreview vacancy) {
        if (favoriteVacancies == null) {
            return;
        }
        favoriteVacancies.remove(vacancy);
    }

    public List<VacancyPreview> getLastJobRequestVacancies() {
        return lastJobRequestVacancies;
    }

    public void setLastJobRequestVacancies(List<VacancyPreview> lastJobRequestVacancies) {
        this.lastJobRequestVacancies = lastJobRequestVacancies;
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public void addFeedback(Feedback feedback) {
        if (feedbackList == null) {
            feedbackList = new ArrayList<>();
        }
        feedbackList.add(feedback);
    }

    /**
     * Returns string description of user's search filters
     * @param withMarkdown whether to insert HTML-markdown in the description
     *                     string
     * @return string description, empty string ("") if search filters is null
     */
    public String getSearchFiltersListMessage(boolean withMarkdown) {
        if (searchFilters != null) {
            if (!searchFilters.isEmpty()) {
                String[] filters = searchFilters.split(";");

                int number = 1;
                StringBuilder builder = new StringBuilder();
                for (String filter : filters) {
                    if (withMarkdown) filter = String.format("<b>%s</b>", filter);
                    builder.append(String.format("%d. %s\n", number, filter));
                    number++;
                }
                return builder.substring(0, builder.length() - 1);
            }
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
