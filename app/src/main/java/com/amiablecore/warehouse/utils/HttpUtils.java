package com.amiablecore.warehouse.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    private static final String BASE_URL = "http://10.0.2.2:8096/Warehouse";

    private static final String TAG = "HttpUtil";

    static HttpURLConnection connection;

    public static HttpURLConnection getConnection(String urlAdress) {

        try {
            URL url = new URL(getAbsoluteUrl(urlAdress));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
