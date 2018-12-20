package com.amiablecore.warehouse.utils;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    private static final String BASE_URL = "http://10.0.2.2:8096/Warehouse";

    private static final String TAG = "HttpUtil";

    public static HttpURLConnection connectionPost, connectionGet;

    public static HttpURLConnection getPostConnection(String urlAdress) {

        try {
            URL url = new URL(getAbsoluteUrl(urlAdress));
            Log.i(TAG + " POST ", String.valueOf(urlAdress));
            connectionPost = (HttpURLConnection) url.openConnection();
            connectionPost.setRequestMethod("POST");
            connectionPost.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connectionPost.setRequestProperty("Accept", "application/json");
            connectionPost.setDoOutput(true);
            connectionPost.setDoInput(true);
            connectionPost.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectionPost;
    }

    public static HttpURLConnection getGetConnection(String urlAdress) {

        try {
            URL urlGet = new URL(getAbsoluteUrl(urlAdress));
            Log.i(TAG + " GET ", getAbsoluteUrl(urlAdress));
            connectionGet = (HttpURLConnection) urlGet.openConnection();
            connectionGet.setRequestMethod("GET");
            connectionGet.setRequestProperty("Accept", "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectionGet;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
