package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.utils.GoogleMapsInvalidApiKeyException;
import com.nikshcherbakov.vacanciesfinder.utils.HTTPEmptyGetParameterException;
import com.nikshcherbakov.vacanciesfinder.utils.JobsRequest;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IJobSearchService {
    List<VacancyPreview> searchNextByUser(User user) throws IOException, GoogleMapsInvalidApiKeyException;
    JobsRequest requestVacanciesByUrlParams(Map<String, String> params) throws IOException, HTTPEmptyGetParameterException;
}
