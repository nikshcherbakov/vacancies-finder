package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.Nullable;

import javax.persistence.*;

@Entity
public class VacancySalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    @Column(name = "from_value")
    private Integer from;

    @Nullable
    @Column(name = "to_value")
    private Integer to;

    @Nullable
    private Boolean gross;

    private String currency;

    public VacancySalary() {
    }

    public VacancySalary(Integer from, Integer to, Boolean gross, String currency) {
        this.from = from;
        this.to = to;
        this.gross = gross;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Boolean getGross() {
        return gross;
    }

    public void setGross(Boolean gross) {
        this.gross = gross;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}