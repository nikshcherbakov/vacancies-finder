package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class VacancyArea {

    @Id
    @NotNull
    private Short id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "vacancyArea")
    private Set<VacancyPreview> vacancyPreviews;

    public VacancyArea() {
    }

    public VacancyArea(Short id, String name) {
        this.id = id;
        this.name = name;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<VacancyPreview> getVacancyPreviews() {
        return vacancyPreviews;
    }

    public void setVacancyPreviews(Set<VacancyPreview> vacancyPreviews) {
        this.vacancyPreviews = vacancyPreviews;
    }
}
