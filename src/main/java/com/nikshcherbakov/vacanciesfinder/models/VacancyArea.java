package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
public class VacancyArea {

    @Id
    @NotNull
    private Short id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "vacancyArea", fetch = FetchType.EAGER)
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

    public void addVacancyPreview(VacancyPreview vacancy) {
        if (vacancyPreviews == null) {
            vacancyPreviews = new HashSet<>();
        }
        vacancyPreviews.add(vacancy);
    }
}
