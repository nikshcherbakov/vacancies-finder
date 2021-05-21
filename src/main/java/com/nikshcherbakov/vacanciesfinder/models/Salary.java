package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;
    private String currency;

    @OneToMany(mappedBy = "salary")
    private Set<User> users;

    public Salary() {
    }

    public Salary(Integer value, String currency) {
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
}
