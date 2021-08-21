package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
public class VacancyEmployer {

    @Id
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "vacancyEmployer", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<VacancyPreview> vacancyPreviews;

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
