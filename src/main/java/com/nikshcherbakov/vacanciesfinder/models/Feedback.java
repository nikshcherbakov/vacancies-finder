package com.nikshcherbakov.vacanciesfinder.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@SuppressWarnings("unused")
@Entity
public class Feedback {

    @Id
    @GeneratedValue
    private Long id;

    @Type(type = "text")
    private String feedbackText;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date publishedAt;

    public Feedback() {
    }

    public Feedback(String feedbackText) {
        this.feedbackText = feedbackText;
        publishedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

}
