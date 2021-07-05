package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VacancyEmployer {

    @Id
    private Long id;

    @NotNull
    private String name;

    public VacancyEmployer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public VacancyEmployer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
