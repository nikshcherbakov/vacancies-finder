package com.nikshcherbakov.vacanciesfinder.utils;

@SuppressWarnings("unused")
public class VacancyTableRow {
    public static class Builder {
        private final VacancyTableRow row;

        public Builder() {
            row = new VacancyTableRow();
        }

        public Builder withNumber(int number) {
            row.number = number;
            return this;
        }

        public Builder withVacancyId(Long id) {
            row.vacancyId = id;
            return this;
        }

        public Builder withName(String name) {
            row.name = name;
            return this;
        }

        public Builder withEmployer(String employer) {
            row.employer = employer;
            return this;
        }

        public Builder withSalaryFrom(Integer from) {
            row.salaryFrom = from;
            return this;
        }

        public Builder withSalaryTo(Integer to) {
            row.salaryTo = to;
            return this;
        }

        public Builder withAddress(String address) {
            row.address = address;
            return this;
        }

        public Builder withResponsibility(String responsibility) {
            row.responsibility = responsibility;
            return this;
        }

        public Builder withLink(String link) {
            row.link = link;
            return this;
        }

        public Builder withCurrency(String currency) {
            row.currency = currency;
            return this;
        }

        public VacancyTableRow build() {
            return row;
        }
    }

    private Integer number;
    private Long vacancyId;
    private String name;
    private String employer;
    private Integer salaryFrom;
    private Integer salaryTo;
    private String address;
    private String responsibility;
    private String link;
    private String currency;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public Integer getSalaryFrom() {
        return salaryFrom;
    }

    public void setSalaryFrom(Integer salaryFrom) {
        this.salaryFrom = salaryFrom;
    }

    public Integer getSalaryTo() {
        return salaryTo;
    }

    public void setSalaryTo(Integer salaryTo) {
        this.salaryTo = salaryTo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
