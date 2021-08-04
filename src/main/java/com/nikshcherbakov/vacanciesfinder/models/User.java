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

    private String telegram;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private TravelOptions travelOptions;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Salary salary;

    private String searchFilters;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastJobRequestDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VacancyPreview> vacancies;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VacancyPreview> favoriteVacancies;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;

        // Account is disabled by default
        this.enabled = false;

        // Telegram is optional
        this.telegram = null;

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

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
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
