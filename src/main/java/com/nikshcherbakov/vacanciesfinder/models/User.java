package com.nikshcherbakov.vacanciesfinder.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@SuppressWarnings("unused")
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
    @Size(min = 6, max = 72)
    private String password;

    @Transient
    private String passwordConfirm;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private MailingPreference mailingPreference;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private TelegramSettings telegramSettings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private TravelOptions travelOptions;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Salary salary;

    private String searchFilters;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastJobRequestDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVacancy> usersVacancies = new ArrayList<>();

    @Transient
    private List<VacancyPreview> lastJobRequestVacancies;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
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

    public List<VacancyPreview> getLastJobRequestVacancies() {
        return lastJobRequestVacancies;
    }

    public void setLastJobRequestVacancies(List<VacancyPreview> lastJobRequestVacancies) {
        this.lastJobRequestVacancies = lastJobRequestVacancies;
    }

    public List<UserVacancy> getUsersVacancies() {
        return usersVacancies;
    }

    public void setUsersVacancies(List<UserVacancy> usersVacancies) {
        this.usersVacancies = usersVacancies;
    }

    public void addUserVacancy(UserVacancy userVacancy) {
        if (usersVacancies == null) {
            usersVacancies = new ArrayList<>();
        }
        usersVacancies.add(userVacancy);
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
     * Adds saved vacancy to a user
     * @param vacancy vacancy that needs to be saved
     */
    public void addVacancy(VacancyPreview vacancy) {
        UserVacancy userVacancy = new UserVacancy(this, vacancy);
        usersVacancies.add(userVacancy);
        vacancy.getUsersVacancies().add(userVacancy);
    }

    /**
     * Adds a user's vacancy to favorites
     * @param vacancy vacancy from user's vacancies list to be added
     *                to favorite vacancies
     */
    public void likeVacancy(VacancyPreview vacancy) {
        for (UserVacancy userVacancy : usersVacancies) {
            if (userVacancy.getVacancy().equals(vacancy)) {
                userVacancy.setFavorite(true);
            }
        }
    }

    /**
     * Returns list of non-favorite vacancies
     * @return list of a user's vacancies
     */
    public List<VacancyPreview> getVacancies() {
        List<VacancyPreview> vacancies = new ArrayList<>();
        for (UserVacancy userVacancy : usersVacancies) {
            if (!userVacancy.getFavorite()) {
                vacancies.add(userVacancy.getVacancy());
            }
        }
        return vacancies;
    }


    /**
     * Returns user's favorite vacancies
     * @return list of a user's favorite vacancies
     */
    public List<VacancyPreview> getFavoriteVacancies() {
        List<VacancyPreview> favoriteVacancies = new ArrayList<>();
        for (UserVacancy userVacancy : usersVacancies) {
            if (userVacancy.getFavorite()) {
                favoriteVacancies.add(userVacancy.getVacancy());
            }
        }
        return favoriteVacancies;
    }

    /**
     * Removes vacancy from a user
     * @param vacancy vacancy to be removed
     * @return user vacancy that will finally be removed, null if vacancy
     * is not found in user's list of vacancies
     * @implNote Note that the method does not actually delete the vacancy
     * using cascading. It just removes the vacancy from the user. Also note
     * that input vacancy could differ from vacancy that will be removed, so
     * use returned vacancy for the future operations with cascade deleting
     */
    public VacancyPreview removeVacancy(VacancyPreview vacancy) {
        for (Iterator<UserVacancy> iterator = usersVacancies.iterator(); iterator.hasNext(); ) {
            UserVacancy userVacancy = iterator.next();

            if (userVacancy.getUser().equals(this) && userVacancy.getVacancy().equals(vacancy)) {
                iterator.remove();

                VacancyPreview vacancyToBeRemoved = userVacancy.getVacancy();

                vacancyToBeRemoved.getUsersVacancies().remove(userVacancy);
                userVacancy.setUser(null);
                userVacancy.setVacancy(null);

                return vacancyToBeRemoved;
            }
        }
        return null;
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
