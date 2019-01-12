package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amiablecore.warehouse.beans.Inward;
import com.amiablecore.warehouse.beans.Outward;
import com.amiablecore.warehouse.db.DatabaseHelper;
import com.amiablecore.warehouse.db.DbQueryExecutor;
import com.amiablecore.warehouse.utils.HttpUtils;
import com.amiablecore.warehouse.utils.Session;
import com.amiablecore.warehouse.utils.StaticConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDataSyncActivity extends AppCompatActivity implements View.OnClickListener {

    private Session session;//global variable
    Button btnCancel;
    private CardView cardInwardSync, cardOutwardSync;
    private static final String TAG = "WarehouseDataSync";
    private DbQueryExecutor databaseObject;
    private List<Inward> inwardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        session.putToSession("wh_id", MainActivity.getWhAdminId());
        session.putToSession("whUser_id", MainActivity.getWhUserId());
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_data_sync);
        cardInwardSync = (CardView) findViewById(R.id.cardInwardSyncId);
        cardOutwardSync = (CardView) findViewById(R.id.cardOutwardSyncId);
        btnCancel = (Button) findViewById(R.id.btnSyncCancel);
        btnCancel.setOnClickListener(this);
        cardInwardSync.setOnClickListener(this);
        cardOutwardSync.setOnClickListener(this);
        databaseObject = new DbQueryExecutor(WarehouseDataSyncActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardInwardSyncId:
                checkInwardDetailsPresentInAppDB();
                break;
            case R.id.cardOutwardSyncId:
                checkOutwardDetailsPresentInAppDB();
                break;
            case R.id.btnSyncCancel:
                startActivity(new Intent(this, WarehouseUserActivity.class));
                break;
        }
    }

    public void checkInwardDetailsPresentInAppDB() {
        Log.i(TAG, "checkInwardDetailsPresentInAppDB()");
        inwardList = databaseObject.findInwardDetailsInDB();
        if (inwardList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Inward Details Not Found to Sync : ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        syncDataWithServer();
        return;
    }

    public void checkOutwardDetailsPresentInAppDB() {
        List<Outward> outwardList = new ArrayList<>();

        return;
    }

    public void syncDataWithServer() {
        Log.i(TAG, "SyncDataWithServer");
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlAdress = "/synchronize/inward/" + session.getFromSession("whUser_id");
                    try {
                        HttpURLConnection conn = HttpUtils.getPostConnection(urlAdress);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(convertInwardListToJson().toString());
                        os.flush();
                        os.close();
                        Log.i("Request : ", convertInwardListToJson().toString());
                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        if (conn.getResponseCode() == 200) {
                            databaseObject.deleteTableData(DatabaseHelper.TABLE_INWARD);
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

    public JSONArray convertInwardListToJson() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (Inward inward : inwardList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("inwardId", inward.getInwardId());
                jsonObject.put("inwardDate", inward.getInwardDate());
                jsonObject.put("lotName", inward.getLotName());
                jsonObject.put("traderId", inward.getTraderId());
                jsonObject.put("commodityId", inward.getCommodityId());
                jsonObject.put("categoryId", inward.getCategoryId());
                jsonObject.put("totalQuantity", inward.getTotalQuantity());
                jsonObject.put("weightPerBag", inward.getWeightPerBag());
                jsonObject.put("totalWeight", inward.getTotalWeight());
                jsonObject.put("physicalAddress", inward.getPhysicalAddress());
                jsonObject.put("whAdminId", inward.getWhAdminId());
                jsonObject.put("whUserId", inward.getWhUserId());
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            Log.e(TAG, "List to JSON Failed");
        }
        return jsonArray;
    }
}