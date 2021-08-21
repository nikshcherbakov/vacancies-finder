package com.nikshcherbakov.vacanciesfinder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@SuppressWarnings("unused")
@Entity
public class VacancySnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "text")
    @JsonProperty("requirement")
    private String requirement;

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
