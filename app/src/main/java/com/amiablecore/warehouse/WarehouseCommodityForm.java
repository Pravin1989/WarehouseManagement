package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WarehouseCommodityForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtCommodityName;
    Button btnAddCommodity, btnCancelCommodity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_commodity_form);
        initViews();
    }

    private void initViews() {
        txtCommodityName = (EditText) findViewById(R.id.txtCommodity);
        btnCancelCommodity = findViewById(R.id.btnCancelCommodity);
        btnAddCommodity = findViewById(R.id.btnAddCommodity);

        btnAddCommodity.setOnClickListener(this);
        btnCancelCommodity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCommodity:
                Toast.makeText(getApplicationContext(),
                        "Commodity is added...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCancelCommodity:
                startActivity(new Intent(WarehouseCommodityForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public boolean addCommodity() {
        //The database code will go here to add trader by Admin
        return true;
    }
}
