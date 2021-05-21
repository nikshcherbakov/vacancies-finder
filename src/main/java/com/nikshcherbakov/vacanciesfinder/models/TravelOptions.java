package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class TravelOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Location location;

    private Integer travelTimeInMinutes;

    private String travelBy;

    @OneToMany(mappedBy = "travelOptions")
    private Set<User> users;

    public TravelOptions() {
    }

    public TravelOptions(@NotNull Location location, Integer travelTimeInMinutes, String travelBy) {
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
