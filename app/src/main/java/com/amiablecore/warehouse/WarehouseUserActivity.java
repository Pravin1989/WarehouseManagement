package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class WarehouseUserActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardInward, cardOutward, cardSyncData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_user_dashboard);
        initViews();
    }

    private void initViews() {
        cardInward = (CardView) findViewById(R.id.cardInwardId);
        cardOutward = (CardView) findViewById(R.id.cardOutwardId);
        cardSyncData = (CardView) findViewById(R.id.cardSyncDataId);
        cardInward.setOnClickListener(this);
        cardOutward.setOnClickListener(this);
        cardSyncData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(),
                "OnCartSelected : " + v.getId(),
                Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.cardInwardId:
                startActivity(new Intent(this, WarehouseUserInwardActivity.class));
                break;
            case R.id.cardOutwardId:
                startActivity(new Intent(this, WarehouseUserOutwardActivity.class));
                break;
            case R.id.cardSyncDataId:
                startActivity(new Intent(this, WarehouseUserDataSync.class));
                break;
        }
    }
}
