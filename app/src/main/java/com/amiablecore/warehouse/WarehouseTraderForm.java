package com.amiablecore.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WarehouseTraderForm extends AppCompatActivity implements View.OnClickListener {

    EditText txtTraderName, txtContactNo, txtTraderEmail;
    Button btnRegisterTrader, btnTraderCancel, btnTraderSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_trader_form);
        initViews();
    }

    private void initViews() {
        txtTraderEmail = (EditText) findViewById(R.id.txtEmail);
        txtTraderName = (EditText) findViewById(R.id.txtTraderName);
        txtContactNo = (EditText) findViewById(R.id.txtTraderContactNo);
        btnTraderSearch = findViewById(R.id.btnSearch);
        btnTraderCancel = findViewById(R.id.btnTraderCancel);
        btnRegisterTrader = findViewById(R.id.btnRegTrader);

        btnTraderSearch.setOnClickListener(this);
        btnRegisterTrader.setOnClickListener(this);
        btnTraderCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                Toast.makeText(getApplicationContext(),
                        "Search Trader Info...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRegTrader:
                Toast.makeText(getApplicationContext(),
                        "Register Trader Info...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnTraderCancel:
                startActivity(new Intent(WarehouseTraderForm.this, WarehouseAdminActivity.class));//Redirect to Admin Dashboard Page
                break;
        }
    }

    public boolean findTrader() {
        //The database code will go here to find trader by Admin
        return true;
    }

    public boolean addTrader() {
        //The database code will go here to add trader by Admin
        return true;
    }
}
