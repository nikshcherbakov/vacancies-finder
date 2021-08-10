package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.sun.istack.NotNull;

public interface IDistributorService {
    boolean sendFoundVacancies(@NotNull User to);
}
