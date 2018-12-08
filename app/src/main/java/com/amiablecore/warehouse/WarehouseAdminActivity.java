package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class WarehouseAdminActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardWarehouseUser, cardTrader, cardCommodity, cardCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_admin);
        initViews();
    }

    private void initViews() {
        cardWarehouseUser = (CardView) findViewById(R.id.cardWHUserId);
        cardTrader = (CardView) findViewById(R.id.cardTraderId);
        cardCommodity = (CardView) findViewById(R.id.cardCommodityId);
        cardCategory = (CardView) findViewById(R.id.cardCategoryId);
        cardWarehouseUser.setOnClickListener(this);
        cardTrader.setOnClickListener(this);
        cardCommodity.setOnClickListener(this);
        cardCategory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(),
                "OnCartSelected : " + v.getId(),
                Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.cardWHUserId:
                startActivity(new Intent(this, WarehouseUserForm.class));
                break;
            case R.id.cardTraderId:
                startActivity(new Intent(this, WarehouseTraderForm.class));
                break;
            case R.id.cardCommodityId:
                startActivity(new Intent(this, WarehouseCommodityForm.class));
                break;
            case R.id.cardCategoryId:
                startActivity(new Intent(this, WarehouseCategoryForm.class));
                break;
        }
    }
}