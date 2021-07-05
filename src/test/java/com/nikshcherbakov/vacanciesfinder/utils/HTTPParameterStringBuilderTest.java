package com.nikshcherbakov.vacanciesfinder.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HTTPParameterStringBuilderTest {
    @Test
    void itGeneratesCorrectParametersString() throws HTTPEmptyGetParameterException {
        // Case 1
        Map<String, String> params1 = new HashMap<>();
        params1.put("параметр1", "значение1");
        params1.put("параметр2", "значение2");
        String result1 = HTTPParameterStringBuilder.getParamsString(params1);
        assertEquals("?%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%802=%D0%B7%D0%BD" +
                "%D0%B0%D1%87%D0%B5%D0%BD%D0%B8%D0%B52&%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%" +
                "D1%82%D1%801=%D0%B7%D0%BD%D0%B0%D1%87%D0%B5%D0%BD%D0%B8%D0%B51", result1);

        // Case 2
        Map<String, String> params2 = new HashMap<>();
        params2.put("nonEmptyParam", "nonEmptyValue");
        params2.put("", "value");
        assertThrows(HTTPEmptyGetParameterException.class, () -> HTTPParameterStringBuilder.getParamsString(params2));

        // Case 3
        Map<String, String> params3 = new HashMap<>();
        params3.put("param1", "value1");
        String result3 = HTTPParameterStringBuilder.getParamsString(params3);
        assertEquals("?param1=value1", result3);

        // Case 4
        Map<String, String> params4 = new HashMap<>();
        String result4 = HTTPParameterStringBuilder.getParamsString(params4);
        assertEquals("", result4);

        // Case 5
        Map<String, String> params5 = new HashMap<>();
        params5.put("параметр1", "значение1");
        params5.put("параметр2", null);
        assertThrows(HTTPEmptyGetParameterException.class, () -> HTTPParameterStringBuilder.getParamsString(params5));

        // Case 6
        Map<String, String> params6 = new HashMap<>();
        params6.put("param1", "someNotNullParam");
        params6.put(null, "notNullValue");
        assertThrows(HTTPEmptyGetParameterException.class, () -> HTTPParameterStringBuilder.getParamsString(params6));
    }
}