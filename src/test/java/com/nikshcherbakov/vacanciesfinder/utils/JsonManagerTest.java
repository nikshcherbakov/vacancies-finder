package com.nikshcherbakov.vacanciesfinder.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonManagerTest {

    @Test
    void itReturnsValidJsonFromHeadhunter() throws IOException {
        String json = JsonManager.getJsonByUrl("https://api.hh.ru/vacancies");
        assertNotNull(json);
        assertTrue(json.contains("items"));
    }

    @Test
    void itReturnsValidJsonFromHeadhunterByUlrWithParams() throws IOException, HTTPEmptyGetParameterException {
        // Case 1
        Map<String, String> params1 = new HashMap<>();
        params1.put("text", "Менеджер");
        String json1 = JsonManager.getJsonByUrl("https://api.hh.ru/vacancies", params1);
        assertTrue(json1.contains("items"));
        assertTrue(json1.contains("Менеджер"));

        // Case 2
        Map<String, String> params2 = new HashMap<>();
        params2.put("", "Менеджер");
        assertThrows(HTTPEmptyGetParameterException.class,
                () -> JsonManager.getJsonByUrl("https://api.hh.ru/vacancies", params2));

        // Case 3
        Map<String, String> params3 = new HashMap<>();
        params3.put("text", null);
        assertThrows(HTTPEmptyGetParameterException.class,
                () -> JsonManager.getJsonByUrl("https://api.hh.ru/vacancies", params3));

        // Case 4
        Map<String, String> params4 = new HashMap<>();
        params4.put(null, null);
        assertThrows(HTTPEmptyGetParameterException.class,
                () -> JsonManager.getJsonByUrl("https://api.hh.ru/vacancies", params4));

        // Case 5
        String json5 = JsonManager.getJsonByUrl("https://api.hh.ru/vacancies", null);
        assertTrue(json5.contains("items"));
        assertTrue(json5.contains("Менеджер"));
    }
}