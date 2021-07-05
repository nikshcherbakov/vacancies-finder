package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VacancySnippet {

    @Id
    private Long id;

    @NotNull
    private String requirement;

    @NotNull
    private String responsibility;

    public VacancySnippet(String requirement, String responsibility) {
        this.requirement = requirement;
        this.responsibility = responsibility;
    }

    public VacancySnippet() {
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
