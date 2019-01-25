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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class WarehouseInCompleteActivity extends AppCompatActivity implements View.OnClickListener {

    private Session session;//global variable
    Button btnCancel;
    private CardView cardInwardComplete, cardOutwardComplete;
    private static final String TAG = "WarehouseDataSync";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        session.putToSession("wh_id", MainActivity.getWhAdminId());
        session.putToSession("whUser_id", MainActivity.getWhUserId());
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_incomplete_tasks);
        cardInwardComplete = (CardView) findViewById(R.id.cardInwardInCompleteId);
        cardOutwardComplete = (CardView) findViewById(R.id.cardOutwardInCompleteId);
        btnCancel = (Button) findViewById(R.id.btnInCompleteCancel);
        btnCancel.setOnClickListener(this);
        cardInwardComplete.setOnClickListener(this);
        cardOutwardComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardInwardInCompleteId:
                startActivity(new Intent(this, WarehouseInCompleteInwardActivity.class));
                break;
            case R.id.cardOutwardInCompleteId:
                startActivity(new Intent(this, WarehouseInCompleteOutwardActivity.class));
                break;
            case R.id.btnInCompleteCancel:
                startActivity(new Intent(this, WarehouseUserActivity.class));
                break;
        }
    }

}
