package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
@Embeddable
public class UserVacancyId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "vacancy_id")
    private Long vacancyId;

    public UserVacancyId() {
    }

    public UserVacancyId(Long userId, Long vacancyId) {
        this.userId = userId;
        this.vacancyId = vacancyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVacancyId that = (UserVacancyId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(vacancyId, that.vacancyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, vacancyId);
    }
}
