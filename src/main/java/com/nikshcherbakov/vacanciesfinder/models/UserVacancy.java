package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_vacancy")
public class UserVacancy {

    @EmbeddedId
    private UserVacancyId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("vacancyId")
    private VacancyPreview vacancy;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    public UserVacancy() {
    }

    public UserVacancy(User user, VacancyPreview vacancy) {
        this.user = user;
        this.vacancy = vacancy;
        this.id = new UserVacancyId(user.getId(), vacancy.getId());
    }

    public UserVacancyId getId() {
        return id;
    }

    public void setId(UserVacancyId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VacancyPreview getVacancy() {
        return vacancy;
    }

    public void setVacancy(VacancyPreview vacancy) {
        this.vacancy = vacancy;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVacancy that = (UserVacancy) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(vacancy, that.vacancy) && Objects.equals(isFavorite, that.isFavorite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, vacancy, isFavorite);
    }
}
