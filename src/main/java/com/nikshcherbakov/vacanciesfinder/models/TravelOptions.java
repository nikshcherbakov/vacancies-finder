package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class TravelOptions {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private Location location;

    private Integer travelTimeInMinutes;

    private String travelBy;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    public TravelOptions() {
    }

    public TravelOptions(@NotNull User user, @NotNull Location location, Integer travelTimeInMinutes, String travelBy) {
        this.user = user;
        this.location = location;
        this.travelTimeInMinutes = travelTimeInMinutes;
        this.travelBy = travelBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getTravelTimeInMinutes() {
        return travelTimeInMinutes;
    }

    public void setTravelTimeInMinutes(Integer travelTimeInMinutes) {
        this.travelTimeInMinutes = travelTimeInMinutes;
    }

    public String getTravelBy() {
        return travelBy;
    }

    public void setTravelBy(String travelBy) {
        this.travelBy = travelBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User users) {
        this.user = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelOptions that = (TravelOptions) o;
        return Objects.equals(location, that.location) && Objects.equals(travelTimeInMinutes, that.travelTimeInMinutes) && Objects.equals(travelBy, that.travelBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, travelTimeInMinutes, travelBy);
    }
}
