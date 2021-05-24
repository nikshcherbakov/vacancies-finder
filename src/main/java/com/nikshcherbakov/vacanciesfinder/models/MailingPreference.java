package com.nikshcherbakov.vacanciesfinder.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class MailingPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean useEmail;
    private boolean useTelegram;

    @OneToMany(mappedBy = "mailingPreference")
    private Set<User> users;

    public MailingPreference() {
    }

    public MailingPreference(boolean useEmail, boolean useTelegram) {
        this.useEmail = useEmail;
        this.useTelegram = useTelegram;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUseEmail() {
        return useEmail;
    }

    public void setUseEmail(boolean useEmail) {
        this.useEmail = useEmail;
    }

    public boolean isUseTelegram() {
        return useTelegram;
    }

    public void setUseTelegram(boolean useTelegram) {
        this.useTelegram = useTelegram;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailingPreference that = (MailingPreference) o;
        return useEmail == that.useEmail && useTelegram == that.useTelegram;
    }

    @Override
    public int hashCode() {
        return Objects.hash(useEmail, useTelegram);
    }
}
