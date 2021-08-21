package com.nikshcherbakov.vacanciesfinder.utils;

import com.sun.istack.NotNull;

import java.util.Map;

public class HTTPParameterStringBuilder {

    public static String getParamsString(@NotNull Map<String, String> params) throws HTTPEmptyGetParameterException {
        StringBuilder result = new StringBuilder("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String entryKey = entry.getKey();
            String entryValue = entry.getValue();
            if (entryKey == null || entryValue == null) throw new HTTPEmptyGetParameterException();
            if (entryKey.isEmpty() || entryValue.isEmpty()) throw new HTTPEmptyGetParameterException();

            result.append(entryKey);
            result.append("=");
            result.append(entryValue);
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 1 ? resultString.substring(0, resultString.length() - 1) : "";
    }

}
