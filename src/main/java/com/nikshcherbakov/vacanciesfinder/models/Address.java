package com.nikshcherbakov.vacanciesfinder.models;

import com.sun.istack.Nullable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
public class Address {

    @Id
    private Long id;

    @Nullable
    private String city;

    @Nullable
    private String street;

    @Nullable
    private String building;

    @Nullable
    private String description;

    @Transient
    @Nullable
    private Double lat;

    @Transient
    @Nullable
    private Double lng;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Nullable
    private Location location;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id")
    @Nullable
    private MetroStation metro;

    @OneToMany(mappedBy = "address", fetch = FetchType.EAGER)
    private Set<VacancyPreview> vacancyPreviews;

    public Address() {
        this.location = new Location();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public MetroStation getMetro() {
        return metro;
    }

    public void setMetro(MetroStation metro) {
        this.metro = metro;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
        this.location.setLatitude(lat);
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
        this.location.setLongitude(lng);
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

    /**
     * Returns address as string
     * @return string of address if at least city is specified, otherwise returns null
     */
    public String asString() {
        if (city != null && street != null && building != null) {
            // Returning full address (russian standard address format)
            return String.format("%s, %s, %s", city, street, building);
        }
        else if (city != null && street != null) {
            // If building is not specified - we return just city and street
            return String.format("%s, %s", city, street);
        }
        else if (city != null) {
            return city;
        } else {
            return null;
        }
    }
}
