package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amiablecore.warehouse.beans.Inward;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class WarehouseUserDataSync extends AppCompatActivity {

    private Session session;//global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        session = new Session(getApplicationContext());
        getInwardFromLocalDB();
        //  syncDataWithServer();
    }

    public void getInwardFromLocalDB() {
        List<Inward> inwardList = new ArrayList<>();

        syncDataWithServer(inwardList);
        return;
    }

    public void syncDataWithServer(List<Inward> inwardList) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/synchronize/inward/" + session.getFromSession("whUser_id");
                    try {
                        HttpURLConnection conn = HttpUtils.getGetConnection(urlAdress);

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 200) {
                            BufferedReader in = null;
                            StringBuilder answer = new StringBuilder(100000);
                            try {
                                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String inputLine;
                            try {
                                while ((inputLine = in.readLine()) != null) {
                                    answer.append(inputLine);
                                    answer.append("\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.i("Response :", answer.toString());
                            JSONArray obj = new JSONArray(answer.toString());
                            Log.i("JSON :", obj.toString());
                            String[] commodities = new String[obj.length() + 1];
                            commodities[0] = StaticConstants.SELECT_COMMODITY;
                            int j = 1;
                            for (int i = 0; i < obj.length(); i++) {
                                commodities[j] = obj.getJSONObject(i).get("commodityName").toString();
                                j++;
                            }
                            Log.i("Comm Length:", String.valueOf(commodities.length));
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
