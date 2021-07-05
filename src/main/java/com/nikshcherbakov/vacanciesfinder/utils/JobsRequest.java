package com.nikshcherbakov.vacanciesfinder.utils;

import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;

import java.util.List;

public abstract class JobsRequest {

    private List<VacancyPreview> items;
    private Integer found;
    private Integer pages;
    private Integer perPage;
    private Integer page;

    public JobsRequest() {
    }

    public JobsRequest(List<VacancyPreview> items, Integer found, Integer pages, Integer perPage, Integer page) {
        this.items = items;
        this.found = found;
        this.pages = pages;
        this.perPage = perPage;
        this.page = page;
    }

    public List<VacancyPreview> getItems() {
        return items;
    }

    public void setItems(List<VacancyPreview> items) {
        this.items = items;
    }

    public Integer getFound() {
        return found;
    }

    public void setFound(Integer found) {
        this.found = found;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
