package com.nikshcherbakov.vacanciesfinder.utils;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class JsonManager {

    public static String getJsonByUrl(@NotNull String basicUrl, @Nullable Map<String, String> params)
            throws IOException, HTTPEmptyGetParameterException {
        if (params != null) {
            String paramsUrl = HTTPParameterStringBuilder.getParamsString(params);
            return getJsonByUrl(basicUrl + paramsUrl);
        } else {
            return getJsonByUrl(basicUrl);
        }
    }

    public static String getJsonByUrl(@NotNull String basicUrl) throws IOException {
        URL url = new URL(basicUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();

        return content.toString();
    }

}
