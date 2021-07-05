package com.nikshcherbakov.vacanciesfinder.utils;

import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;

import java.util.List;

public class HeadHunterJobsRequest extends JobsRequest {

    public HeadHunterJobsRequest() {
    }

    public HeadHunterJobsRequest(List<VacancyPreview> items, Integer found, Integer pages,
                                 Integer perPage, Integer page) {
        super(items, found, pages, perPage, page);
    }

}
