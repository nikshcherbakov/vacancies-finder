package com.nikshcherbakov.vacanciesfinder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class VacancySnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Type(type = "text")
    @JsonProperty("requirement")
    private String requirement;

    @NotNull
    @Type(type = "text")
    @JsonProperty("responsibility")
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
