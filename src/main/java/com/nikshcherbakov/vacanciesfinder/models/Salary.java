package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Salary {

    @Id
    private Long id;

    private Integer value;
    private String currency;

    @OneToOne
    @MapsId
    private User user;

    public Salary() {
    }

    public Salary(User user, Integer value, String currency) {
        this.user = user;
        this.value = value;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salary salary = (Salary) o;
        return Objects.equals(value, salary.value) && Objects.equals(currency, salary.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }
}
